package ooo.emessi.messenger.data.model.bz_model.attachment

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import ooo.emessi.messenger.data.model.bz_model.converters.FileCategoryConverter
import ooo.emessi.messenger.data.model.bz_model.message.BZMessage
import ooo.emessi.messenger.utils.createBitmap
import ooo.emessi.messenger.utils.createThumbnail
import ooo.emessi.messenger.utils.createThumbnailBase64
import ooo.emessi.messenger.utils.helpers.FileCategory
import java.util.*

@Entity(tableName = "attachments")
@TypeConverters(FileCategoryConverter::class)
data class BZAttachment(
    val attachmentName: String,
    val size: String,
    val hash: String,
    var url: String?,
    var attachmentPath: String?,
    var height: String?,
    var width: String?,
    var thumb: String?,
    val type: FileCategory,
    val messageId: String? = null,
    @PrimaryKey
    val attachmentId: String = UUID.randomUUID().toString()
)
//    : ABZAttachment(attachmentName, size, hash,
//    url, attachmentPath, type, messageId, attachmentId)
{

    fun toTypedAttachment(type: BZMessage.PayloadType): ABZAttachment{
        return when (type){
            BZMessage.PayloadType.IMAGE -> toImageAttachment()
            BZMessage.PayloadType.FILE -> toFileAttachment()
            else -> toFileAttachment()
        }
    }

    fun toTypedAttachment(type: FileCategory): ABZAttachment{
        return when (type){
            FileCategory.image -> toImageAttachment()
            FileCategory.file -> toFileAttachment()
            else -> toFileAttachment()
        }
    }

    private fun toFileAttachment(): FileAttachment{
        return FileAttachment(attachmentName, size, hash, url, attachmentPath, type, messageId, attachmentId)
    }

    private fun toImageAttachment(): ImageAttachment{
//        val bitmap = createBitmap(attachmentPath!!)
//        val thumbnail = createThumbnail(bitmap)
//        val height = thumbnail.height.toString()
//        val width =  thumbnail.width.toString()
//        val thumbCoded = createThumbnailBase64(thumbnail)
        return ImageAttachment(attachmentName, size, hash, url, attachmentPath, height, width, thumb, type, messageId, attachmentId )
    }

}