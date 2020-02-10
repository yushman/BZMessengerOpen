package ooo.emessi.messenger.data.model.bz_model.converters

import androidx.room.TypeConverter
import ooo.emessi.messenger.utils.helpers.FileCategory
import java.io.File

class FileCategoryConverter {
    @TypeConverter
    fun fromFileCategory(category: FileCategory): String{
        return category.name
    }

    @TypeConverter
    fun toFileCategory(category: String): FileCategory{
        return FileCategory.valueOf(category)
    }
}