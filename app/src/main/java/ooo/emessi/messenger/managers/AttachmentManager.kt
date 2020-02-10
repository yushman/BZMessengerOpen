package ooo.emessi.messenger.managers

import android.graphics.Bitmap
import android.util.Base64
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ooo.emessi.messenger.data.model.bz_model.attachment.ABZAttachment
import ooo.emessi.messenger.data.model.bz_model.attachment.BZAttachment
import ooo.emessi.messenger.data.model.bz_model.attachment.FileAttachment
import ooo.emessi.messenger.data.model.bz_model.attachment.ImageAttachment
import ooo.emessi.messenger.data.repo.AttachmentRepo
import ooo.emessi.messenger.utils.createBitmap
import ooo.emessi.messenger.utils.helpers.FileCategory
import org.koin.core.KoinComponent
import org.koin.core.get
import java.io.ByteArrayOutputStream
import java.io.File


class AttachmentManager() : KoinComponent{
    private val attachmentRepo: AttachmentRepo = get()
    val fuManager = FileUploadManager(this)
    var attachments= mutableListOf<ABZAttachment>()

    fun saveAttachment(attachment: BZAttachment) = CoroutineScope(Dispatchers.IO).launch{
        attachmentRepo.saveAttachment(attachment)
    }

    fun createAttachmentsFromPaths(docPaths: List<String>): MutableList<ABZAttachment> {
        docPaths.forEach { attachments.add(createAttachment(it))  }
        return attachments
    }

    fun createAttachment(path: String): ABZAttachment {
        val type = FileCategory.getFileCategoryByPath(path)
        val attachment = when (type) {
            FileCategory.image -> createImageAttachment(path)
//            FileCategory.audio -> createAudioAttachment(path)
//            FileCategory.video -> createVideoAttachment(path)
//            FileCategory.document -> createDocumentAttachment(path)
//            FileCategory.pdf -> createPdfAttachment(path)
//            FileCategory.table -> createTableAttachment(path)
//            FileCategory.presentation -> createPresentationAttachment(path)
//            FileCategory.archive -> createArchiveAttachment(path)
            FileCategory.file -> createFileAttachment(path)
            else -> createFileAttachment(path)
        }
        saveAttachment(attachment.toBZAttachment())
        return attachment
    }

    private fun createImageAttachment(path: String): ImageAttachment {
        val file = File(path)
        val name = file.name
        val size = file.length()
        val hash = calcHash(file)
        val bitmap = createBitmap(path)
        val height = bitmap.height.toDouble()
        val width = bitmap.width.toDouble()
        val ratio = width/height
        val scaledHeight = if (height>width) 50.0 else 50/ratio
        val scaledWidth = if (height<width) 50.0 else 50*ratio
        val thumbBitmap = Bitmap.createScaledBitmap(bitmap, scaledWidth.toInt(), scaledHeight.toInt(), false)
        val os = ByteArrayOutputStream()
        val thumbPNG = thumbBitmap.compress(Bitmap.CompressFormat.PNG, 80, os)
        val byteArray =  os.toByteArray()
        val thumb = Base64.encodeToString(byteArray, Base64.DEFAULT)
        return ImageAttachment(
            name, size.toString(), hash, null, path, scaledHeight.toInt().toString(),
            scaledWidth.toInt().toString(), thumb, FileCategory.image
        )
    }

    private fun calcThumb(file: File): Any {
        return ""
    }

    private fun calcHash(file: File): String {
//        val buffer = ByteArray(8192)
//        var count: Int = 1
//        val digest = MessageDigest.getInstance("SHA")
//        val bis = BufferedInputStream(FileInputStream(file))
//
//        while (count > 0) {
//            count = bis.read(buffer)
//            digest.update(buffer, 0, count)
//        }
//        bis.close()
//
//        val hash = digest.digest()//byteArrayOf()//
//        return Base64.encodeToString(hash, 0)
        return file.name
    }

    private fun createFileAttachment(path: String): FileAttachment {
        val file = File(path)
        val name = file.name
        val size = file.length()
        val hash = calcHash(file)
        return FileAttachment(name, size.toString(), hash, null, path, FileCategory.file)
    }

    fun clearAttachment(){

    }

//    fun attachToMessage(bzMessage: BZMessage) {
//        attachments.forEach {
//            it.messageId = bzMessage.id
//            saveAttachment(it.toBZAttachment())
//        }
//        bzMessage.payload = attachments.map { it.attachmentId }
//    }

    fun getAttachments(list: List<String>): List<ABZAttachment> {
        val attachments = mutableListOf<ABZAttachment>()
        list.forEach { id ->
            val attachment = attachmentRepo.getAttachment(id)
            attachment?.let {
                attachments.add(it.toTypedAttachment(it.type))
            }

        }
        return attachments
    }

    fun createAttachments(attachmentPaths: List<String>) {
        attachmentPaths.forEach {
            val abzAttachment = createAttachment(it)
            attachments.add(abzAttachment)
        }
    }

//    fun createAttachment(path: String) = createAttachment(path)

//    suspend fun tryUpload() = CoroutineScope(Dispatchers.IO).async {
//        //Need to make upload Scheduler
//
//        attachments.forEach {
//            it.url = fuManager.requestSlot(it)
//            saveAttachment(it.toBZAttachment())
//        }
////        d("SingleChatManager", attachments.toString())
//        return@async fuManager.uploadFiles()
//    }.await()

    suspend fun requestSlot(abzAttachment: ABZAttachment): String {
        return fuManager.requestSlot(abzAttachment)
    }

    suspend fun tryUpload(): MutableMap<ABZAttachment, Boolean> {

//        saveAttachment(abzAttachment.toBZAttachment())
        return fuManager.uploadFiles()
    }

}