package ooo.emessi.messenger.data.model.bz_model.converters

import androidx.room.TypeConverter
import ooo.emessi.messenger.data.model.bz_model.message.BZMessage
import java.util.stream.Collectors

class PayloadConverter {
    @TypeConverter
    fun fromPayloadType(type: BZMessage.PayloadType): String{
        return type.name
    }

    @TypeConverter
    fun toPayloadType(s: String): BZMessage.PayloadType{
        return BZMessage.PayloadType.valueOf(s)
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