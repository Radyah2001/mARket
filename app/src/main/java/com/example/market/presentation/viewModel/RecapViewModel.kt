package com.example.market.presentation.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.market.data.RetrofitClient
import com.example.market.model.CheckProgressResponse
import com.example.market.model.CreateSceneRequest
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class RecapViewModel : ViewModel() {
    private val api = RetrofitClient.recapApi
    private var photosceneId: String = ""
    private val _downloadUrl = MutableLiveData<String?>()
    val downloadUrl: LiveData<String?> = _downloadUrl
    private val _progress = MutableLiveData<String?>(null)
    val progress: LiveData<String?> = _progress

    fun setProgress(newProgress: String?) {
        _progress.value = newProgress
    }

    fun setDownloadUrl(url: String?) {
        _downloadUrl.value = url
    }

    fun createPhotoscene(
        sceneName: String,
        format: String? = "obj",
        onResult: (String?) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = api.createPhotoscene(CreateSceneRequest(sceneName, format))
                if (response.isSuccessful) {
                    val newId = response.body()?.Photoscene?.photosceneid
                    photosceneId = newId.orEmpty()
                    onResult(photosceneId)
                } else {
                    // Handle error
                    onResult(null)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                onResult(null)
            }
        }
    }

    suspend fun uploadPhotosInBatches(
        files: List<File>,
        batchSize: Int = 10
    ) {
        // chunk the list
        val chunkedLists = files.chunked(batchSize)
        for ((index, chunk) in chunkedLists.withIndex()) {
            // Build the multipart parts for this chunk
            val parts = chunk.mapIndexed { i, file ->
                val requestBody = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                // name them as file[0], file[1], etc. if your server expects that pattern:
                MultipartBody.Part.createFormData("file[$i]", file.name, requestBody)
            }

            val response = api.uploadPhotos(photosceneId, parts)
            if (!response.isSuccessful) {
                throw Exception("Failed batch upload: ${response.errorBody()?.string()}")
            }
            Log.d("UPLOAD", "Batch ${index + 1}/${chunkedLists.size} uploaded.")
            Log.d("UPLOAD", "Response: ${response.body()}")
        }
    }

    suspend fun processPhotoscene() {
        val response = api.processPhotoscene(photosceneId)
        if (!response.isSuccessful) {
            throw Exception("Process failed: ${response.errorBody()?.string()}")
        }
    }

    suspend fun pollProgress(): CheckProgressResponse? {
        val response = api.checkProgress(photosceneId)
        return if (response.isSuccessful) response.body() else null
    }

    fun getProgress() {
        viewModelScope.launch {
            // Start processing
            processPhotoscene()

            // Poll every 5 seconds
            while (true) {
                delay(5000)
                val progressResponse = pollProgress()
                val progressVal = progressResponse?.Photoscene?.progress
                setProgress(progressVal)
                val progressMsg = progressResponse?.Photoscene?.progressmsg
                Log.d("RECAP", "Progress = $progressVal%, status = $progressMsg")

                if (progressMsg == "DONE") {
                    // Retrieve result, break from loop
                    val resultResponse = api.getPhotosceneResult(photosceneId, "fbx")
                    if (resultResponse.isSuccessful) {
                        val resultUrl = resultResponse.body()?.Photoscene?.scenelink
                        Log.d("RECAP", "Result URL = $resultUrl")
                        setDownloadUrl(resultUrl)
                    } else {
                        Log.e("RECAP", "Result failed: ${resultResponse.errorBody()?.string()}")
                    }

                    break
                }
            }
        }
    }
}