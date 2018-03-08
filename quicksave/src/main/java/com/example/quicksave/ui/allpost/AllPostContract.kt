package com.example.quicksave.ui.allpost

import com.example.quicksave.BasePresenter
import com.example.quicksave.BaseView
import com.example.quicksave.data.source.local.entity.PostInfo

/**
 * Created by kimcy929 on 1/28/2018.
 */

interface AllPostContract {

    interface View : BaseView {

        fun showContentLoading()
        fun hideContentLoading()
        fun getAllPostAdapter(): AllPostAdapter
        fun scrollToFirst()

    }

    interface Presenter : BasePresenter {

        fun loadData()

        fun deletePost(postInfo: PostInfo)

    }
}