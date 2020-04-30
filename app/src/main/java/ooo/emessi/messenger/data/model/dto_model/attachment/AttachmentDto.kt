package ooo.emessi.messenger.data.model.dto_model.attachment

import ooo.emessi.messenger.data.model.dto_model.message.MessageDto
import ooo.emessi.messenger.utils.helpers.FileCategoryUtils
import java.util.*

data class AttachmentDto(
    val type: FileCategoryUtils,
    val attachmentName: String,
    val size: Int,
    val hash: String,
    var url: String? = null,
    var attachmentPath:String? = null,
    var height: Int? = null,
    var width: Int? = null,
    var thumb: String? = null,
    var messageId: String? = null,
    val attachmentId: String = hash
) {
    companion object{
        fun fromHashMap(hm: HashMap<String, String?>): AttachmentDto {
            return AttachmentDto(
                FileCategoryUtils.getCategoryByName(hm["name"]!!),
                hm["name"]!!,
                hm["size"]!!.toInt(),
                hm["hash"]!!,
                hm["url"],
                hm["attachmentPath"],
                hm["height"]?.toInt(),
                hm["width"]?.toInt(),
                hm["preview"]
            )
        }
    }

    fun calcPayloadType(): MessageDto.PayloadType {
        return when(this.type){
            FileCategoryUtils.image -> MessageDto.PayloadType.IMAGE
            else -> MessageDto.PayloadType.FILE
        }
    }
}