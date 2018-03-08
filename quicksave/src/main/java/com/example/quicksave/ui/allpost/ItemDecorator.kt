package com.example.quicksave.ui.allpost

import android.graphics.Rect
import android.support.v7.widget.RecyclerView
import android.view.View

/**
 * Created by kimcy929 on 3/7/2018.
 */
public class DividerItemDecoration(private val space: Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect?, view: View?, parent: RecyclerView?, state: RecyclerView.State?) {
        super.getItemOffsets(outRect, view, parent, state)

        parent?.let {
            if (it.getChildAdapterPosition(view) > 0) {
                outRect?.top = space
            }

            if (it.getChildAdapterPosition(view) == it.adapter.itemCount - 1) {
                outRect?.bottom = space
            }
        }

    }
}