package com.example.quicksave.ui.allpost


import android.support.v4.content.FileProvider
import android.support.v7.widget.RecyclerView
import android.view.View
import com.example.quicksave.BuildConfig
import com.example.quicksave.MyApp
import com.example.quicksave.R
import com.example.quicksave.data.source.local.entity.PostInfo
import com.example.quicksave.ui.videoplayer.VideoPlayerActivity
import com.example.quicksave.utils.*
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.photo_item.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import java.io.File


/**
 * Created by kimcy929 on 1/28/2018.
 */

class PostViewHolder(override val containerView: View?) : RecyclerView.ViewHolder(containerView),
        LayoutContainer {

    var postInfo: PostInfo? = null

    private fun getMimeType(isVideo: Boolean) = if (isVideo) "video/mp4" else "image/jpg"

    private fun checkInternetConnection() : Boolean {
        val context = MyApp.INSTANCE
        return if (context.hasInternet()) {
            true
        } else {
            context.toast(context.getString(R.string.no_internet_connection))
            false
        }
    }

    fun bindTo(postInfo: PostInfo?) {
        this.postInfo = postInfo
        this.postInfo?.apply {

            instaImage.load(displayUrl)
            imageProfile.load(profilePicUrl, TransformationType.CIRCLE)
            txtUserName.text = username

            val isVideo = isVideo == 1
            videoIndicator.show(isVideo)

            btnSave.setOnClickListener {
                if (!checkInternetConnection()) return@setOnClickListener

                val url = if (isVideo) videoUrl!! else displayUrl!!
                val mimeType = getMimeType(isVideo)
                DownloadHelper.downloadFile(url, mimeType)
            }

            btnShare.setOnClickListener {
                if (!checkInternetConnection()) return@setOnClickListener

                val url = if (isVideo) videoUrl!! else displayUrl!!

                launch(UI) {
                    val mimeType = getMimeType(isVideo)
                    val path = DownloadHelper.downloadAndShare(url, mimeType).await()
                    path?.let {
                        val context = MyApp.INSTANCE
                        val uri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", File(it))

                        uri?.let {
                            val captionPost = if (caption.isNullOrEmpty()) username else caption
                            if (isVideo) {
                                InstaUtils.shareVideo(context, uri)
                            } else {
                                InstaUtils.sharePhoto(context, uri)
                            }
                            InstaUtils.setCaption(context, captionPost!!)
                            MyApp.INSTANCE.apply {
                                toast(getString(R.string.copied_caption))
                            }
                        }
                    }
                }
            }

            btnRepost.setOnClickListener {
                if (!checkInternetConnection()) return@setOnClickListener

                val url = if (isVideo) videoUrl!! else displayUrl!!

                launch(UI) {
                    val mimeType = getMimeType(isVideo)
                    val path = DownloadHelper.downloadAndShare(url, mimeType).await()
                    path?.let {
                        val context = MyApp.INSTANCE

                        val uri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", File(it))

                        uri?.let {
                            val captionPost = if (caption.isNullOrEmpty()) username else caption

                            if (isVideo) {
                                InstaUtils.repostVideo(context, it, captionPost!!)
                            } else {
                                InstaUtils.repostPhoto(context, it, captionPost!!)
                            }
                        }
                    }
                }
            }

            imageProfile.setOnClickListener {
                InstaUtils.openProfileOnInstagramApp(MyApp.INSTANCE, this.username!!)
            }

            itemView.setOnClickListener {
                if (isVideo) {
                    val intent = newIntent<VideoPlayerActivity>(context = itemView.context)
                    intent.putExtra("post_info", this)
                    itemView.context.startActivity(intent)
                } else {

                }
            }
        }
    }
}