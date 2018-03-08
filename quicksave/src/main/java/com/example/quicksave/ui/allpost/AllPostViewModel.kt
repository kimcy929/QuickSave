package com.example.quicksave.ui.allpost

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.paging.LivePagedListBuilder
import com.example.quicksave.Injection
import com.example.quicksave.viewmodel.BaseViewModel
import java.util.concurrent.Executors

/**
 * Created by kimcy929 on 1/28/2018.
 */
class AllPostViewModel(app: Application) : AndroidViewModel(app), BaseViewModel {

    //constructor(context: Context, query: String? = null) : this(context = context.applicationContext)

    /*override fun <PostInfo>getAllPosts(context: Context, query: String?): LiveData<PagedList<PostInfo>> {
        return LivePagedListBuilder(dao.getAllPosts(), pagingConfig)
                .setBackgroundThreadExecutor(Executors.newFixedThreadPool(THREAD_COUNT))
                .build()
    }*/

    val dao = Injection.providerPostDaoSource(app)

    val allPost = LivePagedListBuilder(dao.getAllPosts(), pagingConfig)
            .setBackgroundThreadExecutor(Executors.newFixedThreadPool(THREAD_COUNT))
            .build()
}