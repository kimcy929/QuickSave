package com.example.quicksave.utils

import android.content.Context
import android.support.annotation.LayoutRes
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

/**
 * Created by kimcy929 on 1/28/2018.
 */

fun ViewGroup.inflate(@LayoutRes layoutRes: Int, attachToRoot: Boolean = false): View {
    return LayoutInflater.from(context).inflate(layoutRes, this, attachToRoot)
}

fun View.show(isVisible: Boolean = true) = if (isVisible) this.visibility = View.VISIBLE else this.visibility = View.GONE

inline fun ViewGroup.forEach(action: (View) -> Unit) {
    for (index in 0 until childCount) {
         action(getChildAt(index))
    }
}

fun Context.getStatusBarHeight(): Int {
    var result = 0
    val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
    if (resourceId > 0) {
        result = resources.getDimensionPixelSize(resourceId)
    }
    return result
}