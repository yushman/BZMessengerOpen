package ooo.emessi.messenger.data.model.dto_model

import android.graphics.Bitmap
import android.util.Base64
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ooo.emessi.messenger.data.model.dto_model.attachment.AttachmentDto
import ooo.emessi.messenger.data.model.dto_model.message.MessageDto
import ooo.emessi.messenger.settings.Settings
import ooo.emessi.messenger.utils.createBitmap

import ooo.emessi.messenger.utils.helpers.FileCategoryUtils
import org.koin.core.KoinComponent
import org.koin.core.get
import java.io.ByteArrayOutputStream
import java.io.File

class AttachmentDtoMaker: KoinComponent {

    private val gson = get<Gson>()
    private val type = object : TypeToken<AttachmentDto>(){}.type

    fun toJson(attachment:AttachmentDto): String {
        return gson.toJson(attachment, type)
    }

    fun fromJson(messageDto: MessageDto): AttachmentDto? {
        if (messageDto.payload.isBlank()) return null
        return gson.fromJson<AttachmentDto>(messageDto.payload, type)
    }

    fun makeAttachment(path: String): AttachmentDto {
        val type = FileCategoryUtils.getFileCategoryByPath(path)
        val attachment = when (type) {
            FileCategoryUtils.image -> createImageAttachment(path)
            FileCategoryUtils.file -> createFileAttachment(path)
            else -> createFileAttachment(path)
        }
        return attachment
    }

    private fun createImageAttachment(path: String): AttachmentDto {
        val file = File(path)
        val name = file.name
        val size = file.length().toInt()
        val hash = calcHash(file)
        val bitmap = createBitmap(path)
        val height = bitmap.height.toDouble()
        val width = bitmap.width.toDouble()
        val ratio = width/height
        val scaledHeight = if (height>width) Settings.THUMB_MAX_SIZE else Settings.THUMB_MAX_SIZE/ratio
        val scaledWidth = if (height<width) Settings.THUMB_MAX_SIZE else Settings.THUMB_MAX_SIZE*ratio
        val thumbBitmap = Bitmap.createScaledBitmap(bitmap, scaledWidth.toInt(), scaledHeight.toInt(), false)
        val os = ByteArrayOutputStream()
        val thumbPNG = thumbBitmap.compress(Bitmap.CompressFormat.PNG, 80, os)
        val byteArray =  os.toByteArray()
        val thumb = Base64.encodeToString(byteArray, Base64.DEFAULT)
        return AttachmentDto(
            FileCategoryUtils.image, name, size, hash, null, path, scaledHeight.toInt(),
            scaledWidth.toInt(), thumb
        )
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
        return Base64.encodeToString((file.name + file.hashCode().toString()).toByteArray(), Base64.DEFAULT)
    }

    private fun createFileAttachment(path: String): AttachmentDto {
        val file = File(path)
        val name = file.name
        val size = file.length().toInt()
        val hash = calcHash(file)
        return AttachmentDto(FileCategoryUtils.file, name, size, hash, null, path)
    }
}