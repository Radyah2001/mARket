package com.example.market.data

import com.example.market.model.ImageUploadResponse
import com.example.market.model.ModelUploadResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Url

interface ModelApiService {

    @Multipart
    @POST("api/listings/{id}/model")
    suspend fun uploadListingModel(
        @Path("id") listingId: Int,
        @Part modelFile: MultipartBody.Part
    ): Response<ModelUploadResponse>

    @Multipart
    @POST("api/listings/{id}/image")
    suspend fun uploadListingImage(
        @Path("id") listingId: Int,
        @Part imageFile: MultipartBody.Part
    ): Response<ImageUploadResponse>

}