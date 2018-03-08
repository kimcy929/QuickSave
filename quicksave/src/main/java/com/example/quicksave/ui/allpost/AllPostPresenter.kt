package com.example.quicksave.ui.allpost

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import com.example.quicksave.data.source.local.entity.PostInfo
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.async
import kotlin.coroutines.experimental.CoroutineContext

/**
 * Created by kimcy929 on 1/28/2018.
 */
class AllPostPresenter(private val view: AllPostActivity) : AllPostContract.Presenter {

    var jobs: List<Job> = emptyList()

    private val bgContext: CoroutineContext = CommonPool

    val viewModel by lazy(LazyThreadSafetyMode.NONE) {
        ViewModelProviders.of(view).get(AllPostViewModel::class.java)
    }

    override fun startPresenter() {
        loadData()
    }

    override fun stopPresenter() {
        jobs.forEach {
            it.cancel()
        }
    }

    override fun loadData() {

        view.showContentLoading()

        viewModel.allPost.observe(view, Observer {

            view.getAllPostAdapter().submitList(it)

            view.hideContentLoading()
        })

    }

    override fun deletePost(postInfo: PostInfo) {
        val task = async (CommonPool) {
            viewModel.dao.deletePost(postInfo)
        }
        jobs += task
    }
}