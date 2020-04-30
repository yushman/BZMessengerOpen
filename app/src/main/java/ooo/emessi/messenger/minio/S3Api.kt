package ooo.emessi.messenger.minio

import android.text.format.DateFormat
import android.webkit.MimeTypeMap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import ooo.emessi.messenger.utils.helpers.StorageHelper
import ooo.emessi.messenger.utils.parseFileName
import java.io.File
import java.io.IOException
import java.net.URL
import java.util.*

object S3Api {

    private const val HOST = "Host"
    private const val CONTENT_SHA256 = "X-Amz-Content-Sha256"
    private const val DATE = "X-Amz-Date"
    private const val AUTHORIZATION = "Authorization"
    private const val CONNECTION = "Connection"
    private const val KEEP_ALIVE = "keep-alive"

    private const val hostx = "s3.mossales.ru"
    private const val endpoint = "https://$hostx"
    private const val aKey = ""//
    private const val sKey = ""//
    private const val contentSha256 = ""

    private val MEDIA_TYPE_MARKDOWN = "text/x-markdown; charset=utf-8".toMediaTypeOrNull()


    suspend fun downloadFile(url: String): String? = CoroutineScope(Dispatchers.IO).async {


        val offsetTime = TimeZone.getTimeZone(TimeZone.getDefault().id).rawOffset
        val date = Date(Date().time - offsetTime)
        val dateFormatted = DateFormat.format("yyyyMMdd'T'HHmmss'Z'", date).toString()

        val client = OkHttpClient()
        val signature = AwsSignatureGenerator.generateSignature(url, hostx, aKey, sKey, AwsSignatureGenerator.RequestMethod.GET, dateFormatted)
        val fileName = url.parseFileName()

        val request = Request.Builder()
            .url(url)
            .addHeader(HOST, hostx)
            .addHeader(CONTENT_SHA256, contentSha256)
            .addHeader(DATE, dateFormatted)
            .addHeader(AUTHORIZATION, signature)
            .addHeader(CONNECTION, KEEP_ALIVE)
            .build()

        try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) throw IOException("Unexpected code $response")
                return@async StorageHelper.saveFile(response.body?.bytes(), fileName) ////mimeType,
            }

            //attach to message
        } catch (e: Exception){
            e.printStackTrace()
        }
        return@async null 
    }.await()

    fun uploadFile(file: File, url: URL): Boolean {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url(url)
            .put(file.asRequestBody(MEDIA_TYPE_MARKDOWN))
            .build()
        try {
            client.newCall(request).execute().use {response ->
                if (!response.isSuccessful) throw IOException("Unexpected code $response")
                return response.isSuccessful
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return false
    }

    private fun getFileName(path: String) = path.substring(path.lastIndexOf("/")).substring(1)

    private fun getMimeType(path: String): String{
        val ext = path.substring(path.lastIndexOf(".")).substring(1)
        var type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext)
        if (type == null || type.isEmpty()) type = "*/*"
        return type
    }

}