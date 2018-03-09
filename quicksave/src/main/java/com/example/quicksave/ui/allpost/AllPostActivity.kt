package com.example.quicksave.ui.allpost

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import com.example.quicksave.R
import com.example.quicksave.utils.PermissionHelper
import com.example.quicksave.utils.getStatusBarHeight
import kotlinx.android.synthetic.main.activity_all_post.*
import timber.log.Timber

class AllPostActivity : AppCompatActivity(), AllPostContract.View {

    lateinit var adapter: AllPostAdapter
    lateinit var allPostPresenter: AllPostPresenter

    private val arrayPermissions = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE)

    private var statusBarSize: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_post)

        PermissionHelper.checkPermission(arrayPermissions, this)

        initView()

        allPostPresenter = AllPostPresenter(this)
        allPostPresenter.startPresenter()

    }

    override fun initView() {

        parentLayout.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                //or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)

        setSupportActionBar(toolbar)

        fabReturnTop.setOnClickListener {
            try {
                if (!recyclerView.isComputingLayout && recyclerView.scrollState == RecyclerView.SCROLL_STATE_IDLE) {
                    scrollToFirst()
                }
            } catch (e: Exception) {
                Timber.e(e.message)
            }
        }

        adapter = AllPostAdapter()
        recyclerView.adapter = adapter
        recyclerView.addItemDecoration(DividerItemDecoration(resources.getDimensionPixelSize(R.dimen.vertical_divider)))

        val itemTouchHelper = ItemTouchHelper(swipeHandler);
        itemTouchHelper.attachToRecyclerView(recyclerView)

        handleWindowInsets()
    }

    private val swipeHandler = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

        override fun onMove(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder?, target: RecyclerView.ViewHolder?): Boolean {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder?, direction: Int) {
            val position = viewHolder!!.adapterPosition
            val postInfo = adapter.getItemAtPosition(position)
            postInfo?.let {
                allPostPresenter.deletePost(it)
            }
        }
    }

    private fun handleWindowInsets() {

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT_WATCH) {
            val layoutParams = statusBarBackground.layoutParams
            statusBarSize = getStatusBarHeight()
            layoutParams.height = statusBarSize
            statusBarBackground.layoutParams = layoutParams
            (toolbar.layoutParams as ViewGroup.MarginLayoutParams).topMargin = statusBarSize
        } else {
            ViewCompat.setOnApplyWindowInsetsListener(toolbar) { v, insets ->
                statusBarSize = insets.systemWindowInsetTop
                (v.layoutParams as ViewGroup.MarginLayoutParams).topMargin = statusBarSize
                val layoutParams = statusBarBackground.layoutParams
                layoutParams.height = statusBarSize
                statusBarBackground.layoutParams = layoutParams
                insets.consumeSystemWindowInsets()
            }
        }

        appbarLayout.addOnOffsetChangedListener { appBarLayout, verticalOffset ->

            if (-verticalOffset == appBarLayout.totalScrollRange) {
                statusBarBackground.setBackgroundColor(ContextCompat.getColor(this, R.color.colorStatusBarTranslucent))
                clearLightStatusBar(parentLayout)
            } else {
                statusBarBackground.setBackgroundColor(ContextCompat.getColor(this, R.color.colorStatusBarVisible))
                setLightStatusBar(parentLayout)
            }

            fabReturnTop.translationY = -verticalOffset.toFloat()
        }
    }

    private fun setLightStatusBar(view: View) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            var flags = view.systemUiVisibility
            flags = flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            view.systemUiVisibility = flags
        }
    }

    private fun clearLightStatusBar(view: View) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            var flags = view.systemUiVisibility
            flags = flags and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
            view.systemUiVisibility = flags
        }
    }

    override fun showContentLoading() {
        progressBar.visibility = View.VISIBLE
    }

    override fun hideContentLoading() {
        progressBar.visibility = View.GONE
    }

    override fun getAllPostAdapter(): AllPostAdapter {
        return adapter
    }

    override fun scrollToFirst() {
        recyclerView.scrollToPosition(0)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        when (item?.itemId) {
            R.id.action_open_instagram -> openApp("com.instagram.android")
        }

        return true
    }

    private fun openApp(packageName: String?) {
        val launchIntent = packageManager.getLaunchIntentForPackage(packageName)
        if (launchIntent != null) {
            startActivity(launchIntent)
        }
    }

    override fun onStop() {
        super.onStop()
        allPostPresenter.stopPresenter()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode) {
            PermissionHelper.PERMISSION_REQUEST -> {
                if (grantResults.isNotEmpty() && !PermissionHelper.permissionGranted(arrayPermissions, this)) {
                    PermissionHelper.checkPermission(arrayPermissions, this)
                }
            }
        }
    }
}
