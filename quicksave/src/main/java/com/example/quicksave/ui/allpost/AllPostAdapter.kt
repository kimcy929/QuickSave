package com.example.quicksave.ui.allpost

import android.arch.paging.PagedListAdapter
import android.support.v7.util.DiffUtil
import android.view.ViewGroup
import com.example.quicksave.R
import com.example.quicksave.data.source.local.entity.PostInfo
import com.example.quicksave.utils.inflate

/**
 * Created by kimcy929 on 1/28/2018.
 */
class AllPostAdapter : PagedListAdapter<PostInfo, PostViewHolder>(diffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder
            = PostViewHolder(parent.inflate(R.layout.photo_item))

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.bindTo(getItem(position))
    }

    fun getItemAtPosition(position: Int): PostInfo? {
        return getItem(position)
    }

    companion object {

        private val diffCallback = object : DiffUtil.ItemCallback<PostInfo>() {
            override fun areItemsTheSame(oldItem: PostInfo, newItem: PostInfo): Boolean {
                return oldItem.displayUrl == newItem.displayUrl
            }

            override fun areContentsTheSame(oldItem: PostInfo, newItem: PostInfo): Boolean {
                return oldItem == newItem
            }

        }
    }
}