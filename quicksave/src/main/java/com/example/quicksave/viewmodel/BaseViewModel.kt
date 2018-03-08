package com.example.quicksave.viewmodel

import android.arch.paging.PagedList

/**
 * Created by kimcy929 on 1/28/2018.
 */
interface BaseViewModel {

    val THREAD_COUNT get() = 2

    val PAGE_SIZE: Int
        get() = 30

    val REPLACE_HOLDER: Boolean get() = true

    val pagingConfig: PagedList.Config get()
        = PagedList.Config.Builder()
            .setEnablePlaceholders(REPLACE_HOLDER)
            .setPageSize(PAGE_SIZE).build()


    //fun <T> getAllPosts(context: Context, query: String? = null): LiveData<PagedList<T>>
}