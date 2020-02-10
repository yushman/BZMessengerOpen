package ooo.emessi.messenger.data.model.bz_model.user

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bz_users")
data class BZUser(
    @PrimaryKey
    var userJid : String

)