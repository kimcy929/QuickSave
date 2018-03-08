package com.example.quicksave.data.source.local.dao

import android.arch.paging.DataSource
import android.arch.persistence.room.*
import com.example.quicksave.data.source.local.entity.PostInfo


/**
 * Created by kimcy929 on 1/27/2018.
 */

@Dao
interface PostDao {

    @Query("SELECT * FROM post ORDER BY id DESC")
    fun getAllPosts(): DataSource.Factory<Int, PostInfo>

    @Query("SELECT * FROM post")
    fun getAllPosts2(): List<PostInfo>

    @Query("SELECT * FROM post WHERE username = :username")
    fun getPostByUserName(username: String): DataSource.Factory<Int, PostInfo>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPost(postInfo: PostInfo)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPosts(postInfos: MutableList<PostInfo>)

    @Delete
    fun deletePost(postInfo: PostInfo)

    @Query("DELETE FROM post WHERE display_url = :displayUrl")
    fun deletePostBydisplayUrl(displayUrl: String)

}