package com.example.quicksave.data.source.local

import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.migration.Migration
import android.content.Context
import com.example.quicksave.data.source.local.dao.PostDao
import com.example.quicksave.data.source.local.entity.PostInfo

/**
 * Created by kimcy929 on 1/27/2018.
 */

@Database(entities = [(PostInfo::class)], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun postDao(): PostDao



    companion object {

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
                INSTANCE ?: synchronized(this) {
                    INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
                }

        private fun buildDatabase(context: Context)
                = Room.databaseBuilder(context.applicationContext,
                    AppDatabase::class.java, "Insta.db")
                //.addMigrations(Migration1To2())
                .build()

        class Migration1To2 : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                /*val sql = "CREATE INDEX index_post_username ON post (username)"
                database.execSQL(sql)*/
            }
        }
    }
}