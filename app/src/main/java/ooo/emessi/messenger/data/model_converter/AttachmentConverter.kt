package ooo.emessi.messenger.data.model_converter

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ooo.emessi.messenger.data.model.bz_model.attachment.ABZAttachment
import ooo.emessi.messenger.data.model.bz_model.attachment.BZAttachment
import ooo.emessi.messenger.data.model.bz_model.attachment.ImageAttachment
import ooo.emessi.messenger.data.model.bz_model.message.BZMessage
import ooo.emessi.messenger.data.model.bz_model.message.ReplyedMessage

class AttachmentConverter(){
    val gson = Gson()
    val type = object :TypeToken<List<BZAttachment>>(){}.type

    fun toJson(attachments: List<ABZAttachment>) : String {

        return gson.toJson(attachments.map { it.toBZAttachment() }, type)
    }

    fun fromJson(message: BZMessage): List<ABZAttachment>{
        if (message.payload.isBlank()) return emptyList()
        val list = gson.fromJson<List<BZAttachment>>(message.payload, type)
        return list.map { it.toTypedAttachment(message.payloadType) }
    }
}