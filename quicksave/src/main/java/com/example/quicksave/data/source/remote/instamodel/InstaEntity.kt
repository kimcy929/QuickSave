package com.example.quicksave.data.source.remote.instamodel

import com.squareup.moshi.Json

/**
 * Created by kimcy929 on 1/26/2018.
 */

data class InstaData(val graphql: Graphql)

data class EdgeCaption(val node: NodeCaption?)

data class EdgeMediaInfo(val node: NodeMediaInfo)

data class EdgeMediaToCaption(val edges: List<EdgeCaption> = listOf())

data class EdgeSidecarToChildren(val edges: List<EdgeMediaInfo> = listOf())

data class Graphql(@Json(name = "shortcode_media") val shortcodeMedia: ShortcodeMedia)

data class NodeCaption(@Json(name = "text") val text: String?)

data class NodeMediaInfo(@Json(name = "display_url") val displayUrl: String,
                         @Json(name = "video_url") val videoUrl: String? = null,
                         @Json(name = "is_video") val isVideo: Boolean = false)

data class Owner(@Json(name = "profile_pic_url") val profilePicUrl: String, val username: String, @Json(name = "full_name") val fullName: String)

data class ShortcodeMedia(@Json(name = "display_url") val displayUrl: String,
                          @Json(name = "video_url") val videoUrl: String? = null,
                          @Json(name = "is_video") val isVideo: Boolean = false,
                          @Json(name = "edge_media_to_caption") val edgeMediaToCaption: EdgeMediaToCaption? = null,
                          @Json(name = "owner") val owner: Owner?,
                          @Json(name = "edge_sidecar_to_children") val edgeSidecarToChildren: EdgeSidecarToChildren? = null)


