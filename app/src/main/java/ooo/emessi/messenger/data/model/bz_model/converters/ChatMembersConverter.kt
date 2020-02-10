package ooo.emessi.messenger.data.model.bz_model.converters

import androidx.room.TypeConverter
import java.util.stream.Collectors

class ChatMembersConverter {
    @TypeConverter
    fun fromMembers(members: List<String>): String{
        return members.stream().collect(Collectors.joining(","))
    }

    @TypeConverter
    fun toMembers(data: String): List<String>{
        return data.split(",")
    }
}