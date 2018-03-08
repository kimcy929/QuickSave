package com.example.quicksave.utils

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Environment
import android.webkit.URLUtil
import android.widget.Toast
import com.example.quicksave.MyApp
import com.example.quicksave.R
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.async
import timber.log.Timber
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import kotlin.coroutines.experimental.CoroutineContext


/**
 * Created by kimcy929 on 3/8/2018.
 */

object DownloadHelper {

    private var downloadReference: Long = 0
    private lateinit var downloadManager: DownloadManager

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (action == DownloadManager.ACTION_DOWNLOAD_COMPLETE) {
                val downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
                if (downloadId != downloadReference) {
                    context.unregisterReceiver(this)
                    return
                }
                val query = DownloadManager.Query()
                query.setFilterById(downloadReference)
                val cursor = downloadManager.query(query)
                cursor?.let {
                    if (cursor.moveToFirst()) {
                        val columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
                        if (DownloadManager.STATUS_SUCCESSFUL == cursor.getInt(columnIndex)) {
                            var localFile = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI))

                            if (localFile.contains("file:///")) {
                                 localFile = localFile.removePrefix("file:///").substringBeforeLast(File.separator)
                            }
                            context.toast(context.resources.getString(R.string.saved, localFile), Toast.LENGTH_LONG)

                        } else if (DownloadManager.STATUS_FAILED == cursor.getInt(columnIndex)) {
                            val message = context.resources.getString(R.string.error_download, cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_REASON)))
                            context.toast(message, Toast.LENGTH_LONG)
                        }
                    }
                    cursor.close()
                }

                context.unregisterReceiver(this)

            }
        }
    }

    fun downloadFile(url: String, mimeType: String? = null) {

        val guessFileName = URLUtil.guessFileName(url, null, mimeType)

        Timber.d("mimeType -> $mimeType guessFileName -> $guessFileName created by url -> $url")

        val context = MyApp.INSTANCE

        downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

        val downloadUri = Uri.parse(url)

        val request = DownloadManager.Request(downloadUri)
        request.apply {
            setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE or DownloadManager.Request.NETWORK_WIFI)
            //setAllowedOverRoaming(true)
            setTitle(guessFileName)
            setDescription(guessFileName)
            setVisibleInDownloadsUi(true)
            allowScanningByMediaScanner()
            setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)

            //request.setDestinationUri(Uri.fromFile(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)))
            setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES, guessFileName)

            context.registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

            downloadReference = downloadManager.enqueue(this)
        }
    }

    private val bgContext: CoroutineContext = CommonPool

    fun downloadAndShare(url: String?, mimeType: String? = null) = async(bgContext) {

        var input: InputStream? = null
        var output: OutputStream? = null
        var connection: HttpURLConnection? = null

        val tempVideoPath = """${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).path}${File.separator}${URLUtil.guessFileName(url, null, mimeType)}"""
        val targetFile = File(tempVideoPath)

        try {
            val urlConnection = URL(url)
            connection = urlConnection.openConnection() as HttpURLConnection
            connection.connect()

            // expect HTTP 200 OK, so we don't mistakenly save error report
            // instead of the file
            if (connection.responseCode != HttpURLConnection.HTTP_OK) {
                Timber.e("""Server returned HTTP ${connection.responseCode} ${connection.responseMessage}""")
                return@async null
            }

            // download the file
            //isDownloadingVideo = true

            input = connection.inputStream

            input?.let {
                output = FileOutputStream(targetFile, false)

                /*val videoSize: Long

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    videoSize = connection.contentLengthLong
                } else {
                    videoSize = connection.contentLength.toLong()
                }

                var downloaded: Long = 0*/

                val data = ByteArray(4096)
                var count: Int

                do {
                    count = input.read(data)
                    if (count != 1) {
                        output!!.write(data, 0, count)

                        /*downloaded += count.toLong()
                        if (publishSubject != null) {
                            if (videoSize != 0L) {
                                publishSubject.onNext((100 * currentSize / videoSize).toString())
                            }
                        }*/

                    } else {
                        break
                    }

                } while (count != -1)
            }

        } catch (e: Exception) {
            Timber.e(e.message)
        } finally {
            try {
                output?.close()
                input?.close()
                connection?.disconnect()
            } catch (e: IOException) {
                Timber.e(e.message)
            }
        }

        if (targetFile.exists()) targetFile.path else null
    }
}