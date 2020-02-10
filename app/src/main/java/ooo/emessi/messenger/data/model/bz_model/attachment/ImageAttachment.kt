package ooo.emessi.messenger.data.model.bz_model.attachment

import ooo.emessi.messenger.utils.helpers.FileCategory
import java.util.*
import kotlin.collections.HashMap

data class ImageAttachment(
    override val attachmentName: String,
    override val size: String,
    override val hash: String,
    override var url: String?,
    override var attachmentPath: String?,
    override var height: String?,
    override var width: String?,
    override var thumb: String?,
    override val type: FileCategory = FileCategory.image,
    override var messageId: String? = null,
    override val attachmentId: String = UUID.randomUUID().toString()

) : ABZAttachment(
        attachmentName,
        size,
        hash,
        url,
        attachmentPath,
        height,
        width,
        thumb,
        type, messageId, attachmentId){
    companion object{
        fun fromHashMap(hm: HashMap<String, String?>): ImageAttachment {
            return ImageAttachment(
                hm["name"]!!,
                hm["size"]!!,
                hm["hash"]!!,
                hm["url"]!!,
                hm["attachmentPath"],
                hm["height"],
                hm["width"],
                hm["preview"]
            )
        }
    }
    override fun toHashMap(): MutableMap<String, String?> {
        val hm = super.toHashMap()
        hm["height"] = height
        hm["width"] = width
        hm["thumb"] = thumb
        return hm
    }

//    override fun toBZAttachment()= BZAttachment(attachmentName, size, hash, url, attachmentPath, height, width, thumb, type, messageId, attachmentId)

}