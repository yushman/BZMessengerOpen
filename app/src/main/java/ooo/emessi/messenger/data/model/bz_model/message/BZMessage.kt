package ooo.emessi.messenger.data.model.bz_model.message

import androidx.room.*
import ooo.emessi.messenger.data.model.bz_model.attachment.ABZAttachment
import ooo.emessi.messenger.data.model.bz_model.attachment.FileAttachment
import ooo.emessi.messenger.data.model.bz_model.attachment.Payload
import ooo.emessi.messenger.data.model.bz_model.contact.BZContact
import ooo.emessi.messenger.data.model.bz_model.converters.PayloadConverter
import ooo.emessi.messenger.data.model.wrapped_model.MessageItem
import ooo.emessi.messenger.utils.helpers.FileCategory
import java.util.*

@TypeConverters(PayloadConverter::class)
@Entity(tableName = "messages")
data class BZMessage (
    @ColumnInfo(name = "chat_id")
    val chatJid: String,
    val from: String,
    val to: String,
    val body: String,
    val timeStamp: Long = System.currentTimeMillis(),
    val isIncoming: Boolean = true,
    var isSended: Boolean = false,
    var isDelivered: Boolean = false,
    //Дополнительные параметры
    var payload: String = "",
    var payloadType: PayloadType = PayloadType.NONE,
    var messageCorrectedBody: String? = null,
    var messageReplyedId: String? = null,
    var isCorrected: Boolean = false,
    var isForwrded: Boolean = false,
    var isReplyed: Boolean = false,
    @PrimaryKey
    val id: String = UUID.randomUUID().toString()
){

    enum class PayloadType{
        NONE,
        FILE,
        IMAGE,
        THUMB,
        DOCUMENT,
        URL
    }

    fun toReplyMessage() = ReplyedMessage(
        this.id,
        this.from,
        this.body
    )

    companion object{
        fun calcPayloadType(attachment: ABZAttachment): PayloadType {
            return when(attachment.type){
                FileCategory.image -> PayloadType.IMAGE
                else -> PayloadType.FILE
            }
        }
    }

}
