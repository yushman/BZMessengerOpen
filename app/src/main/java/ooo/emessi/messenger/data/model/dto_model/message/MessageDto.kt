package ooo.emessi.messenger.data.model.dto_model.message

import androidx.room.*
import java.util.*
import java.util.stream.Collectors

@TypeConverters(MessageDto.PayloadConverter::class)
@Entity(tableName = "messages")
data class MessageDto(
    @ColumnInfo(name = "chat_id")
    val chatJid: String,
    val from: String,
    val to: String,
    val body: String,
    val isIncoming: Boolean,
    val timeStamp: Long = System.currentTimeMillis(),
    //Дополнительные параметры
    val isSended: Boolean = false,
    val isSendingError: Boolean = false,
    val isDelivered: Boolean = false,
    val payload: String = "",
    val payloadType: PayloadType = PayloadType.NONE,
    val messageCorrectedBody: String? = null,
    val messageReplyedId: String? = null,
    val isCorrected: Boolean = false,
    val isForwrded: Boolean = false,
    val isReplyed: Boolean = false,
    @PrimaryKey
    val id: String = UUID.randomUUID().toString()
){

    enum class PayloadType{
        NONE,
        FILE,
        IMAGE,
//        THUMB,
//        DOCUMENT,
        URL
    }

    inner class PayloadConverter {
        @TypeConverter
        fun fromPayloadType(type: MessageDto.PayloadType): String {
            return type.name
        }

        @TypeConverter
        fun toPayloadType(s: String): MessageDto.PayloadType {
            return MessageDto.PayloadType.valueOf(s)
        }

        @TypeConverter
        fun toPayloadList(s: String): List<String>{
            return if (s.contains(",")) s.split(",")
            else if (s.isNotEmpty()) listOf<String>(s)
            else listOf()
        }

        @TypeConverter
        fun fromPayloadList(list: List<String>): String{
            var s = ""
            if (list.isNotEmpty()) {
                s = list.stream().collect(Collectors.joining(","))
            }
            return s
        }
    }

}
