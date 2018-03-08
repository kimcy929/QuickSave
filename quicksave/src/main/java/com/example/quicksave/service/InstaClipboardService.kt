package com.example.quicksave.service

import android.app.Service
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.text.TextUtils
import com.example.quicksave.Injection
import com.example.quicksave.data.source.local.dao.PostDao
import com.example.quicksave.data.source.local.entity.PostInfo
import com.example.quicksave.data.source.remote.ProviderInstaService
import com.example.quicksave.data.source.remote.instamodel.InstaData
import com.example.quicksave.utils.systemService
import kotlinx.coroutines.experimental.*
import kotlinx.coroutines.experimental.android.UI
import timber.log.Timber
import kotlin.coroutines.experimental.CoroutineContext

/**
 * Created by kimcy929 on 1/26/2018.
 */

class InstaClipboardService : Service() {

    private var isLockClipboard: Boolean = false

    private var postLink: String? = null

    private var jobs: List<Job> = emptyList()

    lateinit var postDao: PostDao

    override fun onBind(intent: Intent?): IBinder {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        Timber.d("************** Start clipboard service ****************")

        postDao = Injection.providerPostDaoSource(this)

        registerClipboard()

        return START_STICKY;
    }

    override fun onDestroy() {

        jobs.forEach { it.cancel() }

        //compositeDisposable.dispose()

        super.onDestroy()
    }

    private fun registerClipboard() {
        val clipboardManager = systemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

        clipboardManager.addPrimaryClipChangedListener {

            postLink = readFromClipboard()

            Timber.d("Link $postLink")


            if (!TextUtils.isEmpty(postLink) && !isLockClipboard) {

                if (!TextUtils.isEmpty(postLink)) {
                    try {

                        Timber.d("*****************************************************")

                        jobs += parseLink(postLink)

                        isLockClipboard = true

                        Handler().postDelayed({ isLockClipboard = false }, 100)

                    } catch (e: Exception) {
                        Timber.e("Error -> ${e.message}")
                    }
                }

            }
        }
    }

    private fun readFromClipboard(): String? {

        val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

        if (clipboardManager.hasPrimaryClip()) {
            val clipData = clipboardManager.primaryClip
            if (clipData != null) {
                if (clipData.description.hasMimeType("text/plain")) {
                    val item = clipData.getItemAt(0)
                    if (item != null) {
                        val clipboardContent = item.text.toString()
                        if (!TextUtils.isEmpty(clipboardContent) && clipboardContent.contains("instagram.com/p/")) {
                            return "$clipboardContent?__a=1"
                        }
                    }
                }
            }
        }

        return null
    }

    // https://proandroiddev.com/android-coroutine-recipes-33467a4302e9
    private val uiContext: CoroutineContext = UI
    private val bgContext: CoroutineContext = CommonPool
    private val exceptionHandler: CoroutineExceptionHandler = CoroutineExceptionHandler { _, throwable -> throwable.printStackTrace() }

    //data class Result<out T>(val success: T? = null, val error: Throwable? = null)

    private fun parseLink(url: String?) = launch(uiContext + exceptionHandler) {

        // 2 tasks executed sequentially
        //val response = loadData(url).await()
        //handleData(response).await()

        // Use Retrofit
        if (!url.isNullOrBlank()) {
            val task = loadingJsonData(url)
            jobs += task
            jobs += handleInstaData(task.await())
        } else {
            Timber.d("URL null")
        }
    }

    private fun loadingJsonData(link: String?): Deferred<InstaData> {
        val instaService = ProviderInstaService.instaService
        return instaService.getInstaData(link)
    }

    private fun handleInstaData(result: InstaData) = async(bgContext){
        result.let {

            val graphql = it.graphql

            graphql.run {

                val shortcodeMedia = graphql.shortcodeMedia

                shortcodeMedia.let {
                    val postInfo = PostInfo()

                    //Get user name info
                    val owner = shortcodeMedia.owner
                    owner?.let {
                        postInfo.username = owner.username
                        postInfo.fullName = owner.fullName
                        postInfo.profilePicUrl = owner.profilePicUrl
                    }

                    //Get caption
                    val mediaToCaption = shortcodeMedia.edgeMediaToCaption

                    val edgesCaption = mediaToCaption?.edges
                    if (!(edgesCaption == null || edgesCaption.isEmpty())) {
                        val caption = edgesCaption.get(0).node
                        caption?.let {
                            postInfo.caption = it.text

                            if (!TextUtils.isEmpty(postInfo.caption)) {
                                if (postInfo.caption!!.contains("#")) {
                                    val matchedResult = Regex(pattern = """(#\w+)""").findAll(input = postInfo.caption!!)
                                    val resultTag = StringBuilder()
                                    for (tag in matchedResult) {
                                        resultTag.append(tag.value + " ")
                                    }
                                    postInfo.hashtag = resultTag.toString().trim()
                                }

                            }
                        }
                    }

                    //Get full link video and photo
                    val edgeSidecarToChildren = shortcodeMedia.edgeSidecarToChildren

                    edgeSidecarToChildren?.let {
                        val edges = edgeSidecarToChildren.edges
                        val listMedia = mutableListOf<PostInfo>()

                        for (edge in edges) { //multiple data

                            val node = edge.node
                            if (node.isVideo) {
                                postInfo.displayUrl = node.displayUrl
                                postInfo.videoUrl = node.videoUrl
                                postInfo.isVideo = 1
                            } else {
                                postInfo.displayUrl = node.displayUrl
                                postInfo.videoUrl = node.videoUrl
                                postInfo.isVideo = 0
                            }

                            val newPost = postInfo.copy()
                            listMedia.add(newPost)
                        }

                        insertMultiplePost(listMedia)

                    }?: let {
                        if (shortcodeMedia.isVideo) {
                            postInfo.displayUrl = shortcodeMedia.displayUrl
                            postInfo.videoUrl = shortcodeMedia.videoUrl
                            postInfo.isVideo = 1
                            //showPostInfo(displayUrl = postInfo.displayUrl, videoUrl = postInfo.videoUrl, isVideo = postInfo.isVideo)
                        } else {
                            postInfo.displayUrl = shortcodeMedia.displayUrl
                            postInfo.videoUrl = shortcodeMedia.videoUrl
                            postInfo.isVideo = 0
                        }
                        insertSinglePost(postInfo)
                    }

                }
            }
        }
    }

    /*private fun loadData(url: String?) {
        URL(url).readText()
    }

    private fun handleData(data: String) {

        val moshi = Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()

        val jsonAdapter = moshi.adapter<InstaData>(InstaData::class.java)

        val result = jsonAdapter.fromJson(data)

        result?.let {
            handleInstaData(it)
        }
    }*/

    private fun insertSinglePost(postInfo: PostInfo) = async(bgContext) {
        postDao.insertPost(postInfo)
    }

    private fun insertMultiplePost(posts: MutableList<PostInfo>) = async(bgContext) {
        postDao.insertPosts(posts)
    }

    /*private fun insertSinglePost(postInfo: PostInfo) {
        Flowable.fromCallable({postDao.insertPost(postInfo)})
                .subscribeOn(Schedulers.io())
                .subscribe()
    }*/

    /*private fun insertMultiplePost(posts: MutableList<PostInfo>) {

        compositeDisposable += Flowable.fromIterable(posts)
                .onBackpressureBuffer()
                .concatMap { post -> Flowable.just(post) }
                .subscribeOn(Schedulers.io())
                .doOnNext { post -> postDao.insertPost(post) }
                .subscribe()

    }

    private fun showPostInfo(displayUrl: String?, videoUrl: String?, isVideo: Int) {
        Timber.d("displayUrl $displayUrl")
        Timber.d("videoUrl $videoUrl")
        Timber.d("isVideo $isVideo")
    }*/

    /*val compositeDisposable: CompositeDisposable = CompositeDisposable()

    operator fun CompositeDisposable.plusAssign(disposable: Disposable) {
        add(disposable)
    }*/

}
