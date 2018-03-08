package com.example.quicksave.utils

import android.app.ActivityManager
import android.content.Context
import android.support.v4.app.ActivityManagerCompat
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.cache.LruResourceCache
import com.bumptech.glide.module.AppGlideModule
import com.bumptech.glide.request.RequestOptions

/**
 * Created by kimcy929 on 1/28/2018.
 */

@GlideModule
class InstaAppGlideModule : AppGlideModule() {

    override fun applyOptions(context: Context, builder: GlideBuilder) {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memoryCacheSizeBytes = 1024 * 1024 * 20 // 20mb
        builder.setMemoryCache(LruResourceCache(memoryCacheSizeBytes.toLong()))
        builder.setDefaultRequestOptions(RequestOptions().format(
                if (ActivityManagerCompat.isLowRamDevice(activityManager)) {
                    DecodeFormat.PREFER_RGB_565
                } else {
                    DecodeFormat.PREFER_ARGB_8888
                })
        )
    }
}