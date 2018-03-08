package com.example.quicksave.utils

import android.content.*
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import android.text.TextUtils
import android.widget.Toast
import com.example.quicksave.R
import timber.log.Timber

/**
 * Created by kimcy929 on 3/8/2018.
 */
object InstaUtils {

    private const val INSTAGRAM_PACKAGE_NAME = "com.instagram.android"

    fun openProfileOnInstagramApp(context: Context, userName: String) {
        val localIntent = Intent("android.intent.action.VIEW")
        try {
            if (context.packageManager.getPackageInfo("com.instagram.android", 0) != null) {
                localIntent.data = Uri.parse("http://instagram.com/_u/$userName")
                localIntent.`package` = "com.instagram.android"
                context.startActivity(localIntent)
            }
        } catch (e: PackageManager.NameNotFoundException) {
            Timber.e("Error open profile -> %s", e.message)
        } catch (e: ActivityNotFoundException) {
            Timber.e("Error open profile -> %s", e.message)
        }

    }

    fun sharePhoto(context: Context, uri: Uri) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.putExtra(Intent.EXTRA_STREAM, uri)
        intent.type = "image/*"
        val intentShare = Intent.createChooser(intent, context.getString(R.string.share_to))
        intentShare.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intentShare)
    }

    fun shareVideo(context: Context, uri: Uri) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.putExtra(Intent.EXTRA_STREAM, uri)
        intent.type = "video/mp4"
        val intentShare = Intent.createChooser(intent, context.getString(R.string.share_to))
        intentShare.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intentShare)
    }

    fun repostPhoto(context: Context, uri: Uri, caption: String) {
        val photoIntent = Intent(Intent.ACTION_SEND)
        photoIntent.type = "image/*"
        val iterator = context.packageManager.queryIntentActivities(photoIntent, 0).iterator()

        var resolveInfo: ResolveInfo? = null
        var hasInstagramApp = false

        while (iterator.hasNext()) {
            resolveInfo = iterator.next() as ResolveInfo
            if (resolveInfo.activityInfo.packageName.contains(INSTAGRAM_PACKAGE_NAME)) {
                hasInstagramApp = true
                break
            }
        }

        if (hasInstagramApp) {
            photoIntent.putExtra(Intent.EXTRA_TEXT, caption)
            photoIntent.putExtra(Intent.EXTRA_STREAM, uri)
            photoIntent.setClassName(resolveInfo!!.activityInfo.packageName, resolveInfo.activityInfo.name)
            setCaption(context, caption)
            photoIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            photoIntent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            context.startActivity(photoIntent)
        } else {
            Toast.makeText(context, "Not found Instagram", Toast.LENGTH_SHORT).show()
        }
    }

    fun repostVideo(context: Context, uri: Uri, caption: String) {
        val videoIntent = Intent(Intent.ACTION_SEND)
        videoIntent.type = "video/*"
        val iterator = context.packageManager.queryIntentActivities(videoIntent, 0).iterator()

        var resolveInfo: ResolveInfo? = null
        var hasInstagramApp = false

        while (iterator.hasNext()) {
            resolveInfo = iterator.next() as ResolveInfo
            if (resolveInfo.activityInfo.packageName.contains(INSTAGRAM_PACKAGE_NAME)) {
                hasInstagramApp = true
                break
            }
        }

        if (hasInstagramApp) {
            videoIntent.putExtra(Intent.EXTRA_TEXT, caption)
            videoIntent.putExtra(Intent.EXTRA_STREAM, uri)
            videoIntent.setClassName(resolveInfo!!.activityInfo.packageName, resolveInfo.activityInfo.name)
            setCaption(context, caption)
            videoIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            videoIntent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            context.startActivity(videoIntent)
        } else {
            Toast.makeText(context, "Not found Instagram", Toast.LENGTH_SHORT).show()
        }
    }

    fun setCaption(context: Context, caption: String) {
        if (!TextUtils.isEmpty(caption)) {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            clipboard.let {
                clipboard.primaryClip = ClipData.newPlainText("Caption", caption)
            }
        }
    }
}