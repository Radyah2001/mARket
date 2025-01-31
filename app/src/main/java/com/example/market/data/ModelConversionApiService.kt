package com.example.market.data

import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ModelConversionApiService {
    @Multipart
    @POST("api/models/convert/gltf-to-stl")
    suspend fun convertGLTFtoSTL(
        @Part file: MultipartBody.Part
    ): Response<ResponseBody>

    @Multipart
    @POST("api/models/convert/fbx-to-glb")
    suspend fun convertFBXtoGLTF(
        @Part file: MultipartBody.Part
    ): Response<ResponseBody>

}