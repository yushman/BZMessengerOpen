package ooo.emessi.messenger.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Environment
import android.os.Environment.MEDIA_MOUNTED
import android.os.Environment.getExternalStorageDirectory
import android.util.Base64
import ooo.emessi.messenger.App
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.*


fun convertCompressedByteArrayToBitmap(src: ByteArray?): Bitmap? {
    return if (src == null) null
    else BitmapFactory.decodeByteArray(src, 0 ,src.size)
}

fun saveTempBitmap(bitmap: Bitmap?, jid: String): String? {
    return if (isExternalStorageWritable() && bitmap != null) {
        saveImage(bitmap, jid)
    } else {
        null
    }
}

private fun saveImage(finalBitmap: Bitmap, jid: String): String? {
    var root = App.applicationContext().getExternalFilesDir(Environment.MEDIA_MOUNTED).toString()

    root += "/bz-messenger/saved_avatars"
    val myDir = File(root)
    if (!myDir.exists()) myDir.mkdirs()
    val fName = jid.replace(Regex("[^A-Za-z0-9_]"), "") + ".png"

    var path: String? = null
//    if (file.exists()) file.delete()
    try {
        val file = File(myDir, fName)
        val out = FileOutputStream(file)
        finalBitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        out.flush()
        out.close()
        path = "$root/$fName"
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return path

}

/* Checks if external storage is available for read and write */
fun isExternalStorageWritable(): Boolean {
    val state = Environment.getExternalStorageState()
    return MEDIA_MOUNTED == state
}

fun getCameraFilePath():File{
    val root = Environment.getExternalStorageDirectory().toString() + "/bz-messenger/media"
    val myDir = File(root)
    if (!myDir.exists()) myDir.mkdirs()
    val fName = "IMG_" + Date().format(Template.STRING_FILE_SAVE) + ".jpg"
    return File(myDir, fName)
}

fun createBitmap(path: String): Bitmap {
    return BitmapFactory.decodeFile(path)
}

fun createThumbnail(bitmap: Bitmap): Bitmap{
    val height = bitmap.height.toDouble()
    val width = bitmap.width.toDouble()
    val ratio = width/height
    val scaledHeight = if (height>width) 10.0 else 10/ratio
    val scaledWidth = if (height<width) 10.0 else 10*ratio
    return Bitmap.createScaledBitmap(bitmap, scaledWidth.toInt(), scaledHeight.toInt(), false)

}

fun createThumbnailBase64(bitmap: Bitmap): String{
    val os = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.PNG, 80, os)
    val byteArray = os.toByteArray()
    return Base64.encodeToString(byteArray, Base64.DEFAULT)
}