package com.example.market.presentation.viewModel

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.market.data.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class ModelConversionViewModel: ViewModel() {
    private val api = RetrofitClient.modelConversionApi

    fun convertRemoteGLTFToSTL(
        context: Context,
        remoteUrl: String,            // listing.modelUrl
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                // Step 1: Download the remote GLB to a temp file
                val tempFile = withContext(Dispatchers.IO) {
                    downloadRemoteFile(context, remoteUrl, "temp_converted.glb")
                }
                if (tempFile == null) {
                    onError("Failed to download remote file.")
                    return@launch
                }

                // Step 2: Build the multipart request
                val requestFile = tempFile.asRequestBody("model/gltf-binary".toMediaTypeOrNull())
                val multipartPart = MultipartBody.Part.createFormData("file", tempFile.name, requestFile)

                // Step 3: Call the conversion API
                val response = api.convertGLTFtoSTL(multipartPart)
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody == null) {
                        onError("Response body was null.")
                    } else {
                        // Step 4: Write the STL file to a new temp
                        val tempOutputFile = File(context.cacheDir, "${System.currentTimeMillis()}_model.stl")
                        withContext(Dispatchers.IO) {
                            responseBody.byteStream().use { input ->
                                tempOutputFile.outputStream().use { output ->
                                    input.copyTo(output)
                                }
                            }
                        }
                        onSuccess(tempOutputFile.absolutePath)
                    }
                } else {
                    val err = response.errorBody()?.string() ?: "Unknown error"
                    onError("Conversion failed: $err")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                onError("Exception: ${e.message}")
            }
        }
    }

    fun convertFBXToGLB(
        context: Context,
        objFile: File,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                // Create a RequestBody with the proper MIME type.
                // For FBX files, if they are text, you might use "text/plain".
                // You may choose a different type if needed.
                val requestFile = objFile.asRequestBody("text/plain".toMediaTypeOrNull())
                // Ensure the field name here ("file") matches what the server expects.
                val multipartPart = MultipartBody.Part.createFormData("file", objFile.name, requestFile)

                val response = api.convertFBXtoGLTF(multipartPart)
                if (response.isSuccessful) {
                    response.body()?.let { responseBody ->
                        // Save the GLB response to a temporary file.
                        val tempOutputFile = File(context.cacheDir, "${System.currentTimeMillis()}_output.glb")
                        withContext(Dispatchers.IO) {
                            tempOutputFile.outputStream().use { output ->
                                responseBody.byteStream().copyTo(output)
                            }
                        }
                        // Return the temporary file path as success.
                        onSuccess(tempOutputFile.absolutePath)
                    } ?: onError("Response body was null.")
                } else {
                    val err = response.errorBody()?.string() ?: "Unknown error"
                    onError("Conversion failed: $err")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                onError(e.message ?: "Conversion exception")
            }
        }
    }

    fun saveConvertedFile(context: Context, destinationUri: android.net.Uri, tempFilePath: String) {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    val inputStream = context.contentResolver.openInputStream(android.net.Uri.fromFile(File(tempFilePath)))
                    val outputStream = context.contentResolver.openOutputStream(destinationUri)
                    if (inputStream != null && outputStream != null) {
                        inputStream.copyTo(outputStream)
                        inputStream.close()
                        outputStream.close()
                    }
                }
                Log.d("ModelConversionViewModel", "file successfully saved to destination.")
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("ModelConversionViewModel", "Error saving file: ${e.message}")
            }
        }
    }

    suspend fun downloadRemoteFile(context: Context, fileUrl: String, outputFileName: String): File? {
        return try {
            // minimal example with OkHttp
            val client = okhttp3.OkHttpClient()
            val request = okhttp3.Request.Builder().url(fileUrl).build()
            val response = client.newCall(request).execute()
            if (!response.isSuccessful) return null

            val tempFile = File(context.cacheDir, outputFileName)
            response.body?.byteStream()?.use { input ->
                tempFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            tempFile
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun uriToFile(uri: Uri, context: Context): File? {
        // We create a temp file in the app's cache dir
        val contentResolver = context.contentResolver ?: return null
        val file = File(context.cacheDir, "${System.currentTimeMillis()}_tempfile")
        try {
            contentResolver.openInputStream(uri).use { inputStream ->
                FileOutputStream(file).use { outputStream ->
                    copyStream(inputStream!!, outputStream)
                }
            }
            return file
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    // Copy the bytes from InputStream to OutputStream
    fun copyStream(input: InputStream, out: FileOutputStream) {
        val buffer = ByteArray(1024)
        var len: Int
        while (input.read(buffer).also { len = it } != -1) {
            out.write(buffer, 0, len)
        }
        out.flush()
    }


}