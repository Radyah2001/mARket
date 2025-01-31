package com.example.market.model

import com.example.market.data.ProgressDeserializer
import com.google.gson.annotations.JsonAdapter

data class CreateSceneRequest(
    val scenename: String,
    val format: String? = null
)

data class CreateSceneResponse(
    val Photoscene: PhotosceneInfo?
)

data class PhotosceneInfo(
    val photosceneid: String?,
    @JsonAdapter(ProgressDeserializer::class)
    val progress: String?,
    val progressmsg: String?,
    val scenelink: String?
)


data class CheckProgressResponse(
    val Photoscene: PhotosceneInfo?
)

data class ProcessSceneResponse(
    val Photoscene: PhotosceneInfo?,
    val msg: String?
)

data class PhotosceneResultResponse(
    val Photoscene: PhotosceneInfo?
)

data class AddImagesResponse(
    val photosceneid: String?,
    val file: List<FileResponse>

)

data class FileResponse(
    val filename: String,
    val fileid: String,
    val filetype: String,
    val filesize: String,
    val msg: String
)