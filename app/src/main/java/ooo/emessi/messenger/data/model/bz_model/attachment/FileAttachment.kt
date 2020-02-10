package ooo.emessi.messenger.data.model.bz_model.attachment

import ooo.emessi.messenger.utils.helpers.FileCategory
import java.util.*
import kotlin.collections.HashMap

data class FileAttachment(
    override val attachmentName: String,
    override val size: String,
    override val hash: String,
    override var url: String?,
    override var attachmentPath: String?,
    override val type: FileCategory = FileCategory.file,
    override var messageId: String? = null,
    override val attachmentId: String = UUID.randomUUID().toString()
) : ABZAttachment(
    attachmentName,
    size,
    hash,
    url,
    attachmentPath, null, null, null,
    type, messageId, attachmentId){

    companion object{
        fun fromHashMap(hm: HashMap<String, String?>): FileAttachment {
            return FileAttachment(
                hm["name"]!!,
                hm["size"]!!,
                hm["hash"]!!,
                hm["url"]!!,
                hm["attachmentPath"]
            )
        }
    }


    override fun toHashMap(): MutableMap<String, String?> {
        return super.toHashMap()
    }

//    override fun toBZAttachment()= BZAttachment(attachmentName, size, hash, url, attachmentPath, type, messageId, attachmentId)

}
