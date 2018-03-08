package com.example.quicksave.utils

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat

/**
 * Created by kimcy929 on 3/8/2018.
 */
object PermissionHelper {

    private const val PERMISSION_REQUEST = 1

    fun checkPermission(permissions: Array<String> = arrayOf(), activity: Activity) {

        permissions.forEach {
            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, arrayOf(it), PERMISSION_REQUEST)
            }
        }

    }
}