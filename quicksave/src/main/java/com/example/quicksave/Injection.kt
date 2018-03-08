package com.example.quicksave

import android.content.Context
import com.example.quicksave.data.source.local.AppDatabase
import com.example.quicksave.data.source.local.dao.PostDao

/**
 * Created by kimcy929 on 1/27/2018.
 */
object Injection {

    fun providerPostDaoSource(context: Context): PostDao {
        val database = AppDatabase.getInstance(context)
        return database.postDao()
    }

}