package com.example.quicksave.utils

import android.app.Activity
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat

/**
 * Created by kimcy929 on 3/8/2018.
 *
 * https://developer.android.com/training/permissions/requesting.html#kotlin
 *
 */

object PermissionHelper {

    const val PERMISSION_REQUEST = 1

    fun checkPermission(permissions: Array<String> = arrayOf(), activity: Activity) {

        permissions.forEach {
            if (ActivityCompat.checkSelfPermission(activity, it) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, arrayOf(it), PERMISSION_REQUEST)
            }
        }
    }

    fun permissionGranted(permissions: Array<String> = arrayOf(), activity: Activity): Boolean {
        permissions.forEach {
            if (ActivityCompat.checkSelfPermission(activity, it) != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }
}