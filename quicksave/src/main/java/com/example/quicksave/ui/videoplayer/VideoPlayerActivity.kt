package com.example.quicksave.ui.videoplayer

import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import com.example.quicksave.BaseView
import com.example.quicksave.R
import com.example.quicksave.data.source.local.entity.PostInfo
import com.example.quicksave.utils.USER_AGENT
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import kotlinx.android.synthetic.main.activity_video_player.*


class VideoPlayerActivity : AppCompatActivity(), BaseView {


    var postInfo: PostInfo? = null
    var exoPlayer: SimpleExoPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_player)

        getIntentData()

    }

    fun getIntentData() {

        intent.apply {
            postInfo = getParcelableExtra("post_info")

            postInfo?.let {
                initView()
            }
        }
    }

    override fun initView() {

        showBackButton()

        configVideoView()

    }

    private fun showBackButton() {
        supportActionBar?.apply {
            setDisplayShowHomeEnabled(true)
            setDisplayHomeAsUpEnabled(true)
        }
    }

    private fun configVideoView() {

        val videoSource = Uri.parse(postInfo!!.videoUrl)

        val dataSourceFactory = DefaultDataSourceFactory(this, USER_AGENT, DefaultBandwidthMeter())
        val factory = ExtractorMediaSource.Factory(dataSourceFactory)
        factory.setExtractorsFactory(DefaultExtractorsFactory())

        val mediaSource = factory.createMediaSource(videoSource)

        playerView.setControllerVisibilityListener({ visibility ->
            if (visibility == View.GONE) hideSystemUI() else showSystemUI()
        })

        exoPlayer = ExoPlayerFactory.newSimpleInstance(this, DefaultTrackSelector())
        exoPlayer?.apply {
            prepare(mediaSource)
            playerView.player = exoPlayer
            playerView.requestFocus()
        }
    }

    override fun onPause() {

        if (exoPlayer!!.playWhenReady) {
            exoPlayer!!.playWhenReady = false
        }

        super.onPause()
    }

    override fun onDestroy() {

        exoPlayer?.release()

        super.onDestroy()
    }

    // This snippet hides the system bars.
    private fun hideSystemUI() {
        // Set the IMMERSIVE flag.
        // Set the content to appear under the system bars so that the content
        // doesn't resize when the system bars hide and show.

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                    or View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                    or View.SYSTEM_UI_FLAG_IMMERSIVE)
        else
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_FULLSCREEN)
    }

    // This snippet shows the system bars. It does this by removing all the flags
    // except for the ones that make the content appear under the system bars.
    private fun showSystemUI() {
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        item?.let {
            if (item.itemId == android.R.id.home) {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
