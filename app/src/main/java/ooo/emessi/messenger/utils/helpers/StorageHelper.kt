package ooo.emessi.messenger.utils.helpers

import android.os.Environment
import java.io.File

object StorageHelper {

    fun saveFile(bytes: ByteArray?, fileName: String): String? {//mimeType: String,
        if (bytes == null) return null
//        val mediaType = FileCategory.getFileCategoryByMime(mimeType)
        val root = getDownloadDirPath()
        val rootDir = File(root)
        if (!rootDir.exists()) rootDir.mkdir()
        val file = File(rootDir, fileName)
        if (file.exists()) file.delete()
        if (file.createNewFile()){
            file.writeBytes(bytes)
        }
        return "$root/$fileName"
    }

    fun getDownloadDirPath(): String {
        return (Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).path
                + File.separator + "bz-messenger")
    }
}