package ooo.emessi.messenger.data.model.bz_model.attachment

import ooo.emessi.messenger.utils.helpers.FileCategory
import java.util.*

abstract class ABZAttachment (
    open val attachmentName: String,
    open val size: String,
    open val hash: String,
    open var url: String?,
    open var attachmentPath: String?,
    open var height: String? = null,
    open var width: String? = null,
    open var thumb: String? = null,
    open val type: FileCategory,
    open var messageId: String? = null,
    open val attachmentId: String = UUID.randomUUID().toString()
) {
    open fun toHashMap(): MutableMap<String, String?> {
        val hm = mutableMapOf<String, String?>()
        hm["name"] = attachmentName
        hm["size"] = size
        hm["hash"] = hash
        hm["url"] = url
        hm["attachmentPath"] = attachmentPath
        hm["type"] = type.name
        hm["attachmentId"] = attachmentId
        return hm
    }

    open fun toBZAttachment() = BZAttachment(
        attachmentName,
        size,
        hash,
        url,
        attachmentPath,
        height,
        width,
        thumb,
        type,
        messageId,
        attachmentId
    )
}