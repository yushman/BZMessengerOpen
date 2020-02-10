package ooo.emessi.messenger.data.model.bz_model.attachment

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Payload(
    @PrimaryKey
    val attachmentId: String,
    val messageId: String
)