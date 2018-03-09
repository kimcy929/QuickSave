package com.example.quicksave.utils

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.widget.Toast

/**
 * Created by kimcy929 on 1/26/2018.
 */

inline fun <reified T : Any> Context.serviceStarter(isStart: Boolean) =
        if (isStart) {
            startService(newIntent<T>(this))
        } else {
            stopService(newIntent<T>(this))
        }

inline fun <reified T : Any> newIntent(context: Context) = Intent(context, T::class.java)

fun Context.systemService(serviceName: String) = getSystemService(serviceName)

fun Context.toast(message: String? = null, length: Int = Toast.LENGTH_SHORT) {
    message?.apply {
        Toast.makeText(this@toast, message, length).show()
    }
}

fun Context.hasInternet(): Boolean {
    val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val networkInfo = connectivityManager.activeNetworkInfo
    return networkInfo?.isAvailable ?: false
}




