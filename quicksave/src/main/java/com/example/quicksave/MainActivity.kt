package com.example.quicksave

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.example.quicksave.service.InstaClipboardService
import com.example.quicksave.ui.allpost.AllPostActivity
import com.example.quicksave.utils.newIntent
import com.example.quicksave.utils.serviceStarter

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_main)

        serviceStarter<InstaClipboardService>(true)

        startActivity(newIntent<AllPostActivity>(this))
    }

}
