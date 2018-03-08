package com.example.quicksave.data.source.local.entity

import android.annotation.SuppressLint
import android.arch.persistence.room.*
import android.os.Parcelable
import android.support.annotation.NonNull
import kotlinx.android.parcel.Parcelize

/**
 * Created by kimcy929 on 1/27/2018.
 */
// it can be an entity of Room in Android architect components
@SuppressLint("ParcelCreator")
@Parcelize
@Entity(tableName = "post", indices = [(Index(value = ["display_url"], unique = true)), (Index(value = ["username"]))])
data class PostInfo (

        @PrimaryKey(autoGenerate = true)
        @NonNull
        var id: Int,

        @ColumnInfo(name = "display_url")
        var displayUrl: String? = null,

        @ColumnInfo(name = "video_url")
        var videoUrl: String? = null,

        @ColumnInfo(name = "is_video")
        var isVideo: Int = 0,

        var username: String? = null,

        @ColumnInfo(name = "full_name")
        var fullName: String? = null,

        @ColumnInfo(name = "profile_pic_url")
        var profilePicUrl: String? = null,

        var caption: String? = null,

        var hashtag: String? = null) : Parcelable {

        @Ignore
        constructor() : this(0)
}