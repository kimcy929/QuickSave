package com.example.quicksave.data.source.remote

import com.example.quicksave.data.source.remote.instamodel.InstaData
import kotlinx.coroutines.experimental.Deferred
import retrofit2.http.GET
import retrofit2.http.Url

/**
 * Created by kimcy929 on 3/6/2018.
 */
interface InstaService {

    @GET
    fun getInstaData(@Url url: String?): Deferred<InstaData>

}