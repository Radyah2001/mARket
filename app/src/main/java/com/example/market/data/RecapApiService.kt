package com.example.market.data

import com.example.market.model.AddImagesResponse
import com.example.market.model.CheckProgressResponse
import com.example.market.model.CreateSceneRequest
import com.example.market.model.CreateSceneResponse
import com.example.market.model.PhotosceneResultResponse
import com.example.market.model.ProcessSceneResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface RecapApiService {
    // 1) Create a photoscene
    @POST("api/recap/photoscene")
    suspend fun createPhotoscene(
        @Body request: CreateSceneRequest
    ): Response<CreateSceneResponse>

    // 2) Upload photos (multipart)
    @Multipart
    @POST("api/recap/photoscene/{photosceneid}/file")
    suspend fun uploadPhotos(
        @Path("photosceneid") photosceneId: String,
        @Part files: List<MultipartBody.Part>
    ): Response<AddImagesResponse>

    // 3) Start processing
    @POST("api/recap/photoscene/{photosceneid}/process")
    suspend fun processPhotoscene(
        @Path("photosceneid") photosceneId: String
    ): Response<ProcessSceneResponse>

    // 4) Check progress
    @GET("api/recap/photoscene/{photosceneid}/progress")
    suspend fun checkProgress(
        @Path("photosceneid") photosceneId: String
    ): Response<CheckProgressResponse>

    // 5) Get result
    @GET("api/recap/photoscene/{photosceneid}/result")
    suspend fun getPhotosceneResult(
        @Path("photosceneid") photosceneId: String,
        @Query("format") format: String
    ): Response<PhotosceneResultResponse>
}