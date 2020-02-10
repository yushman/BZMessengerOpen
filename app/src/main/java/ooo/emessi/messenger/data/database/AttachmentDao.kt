package ooo.emessi.messenger.data.database

import androidx.room.*
import ooo.emessi.messenger.data.model.bz_model.attachment.BZAttachment

@Dao
interface AttachmentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertBZAttachment(bzAttachment: BZAttachment)

    @Update
    fun updateBZAttachment(bzAttachment: BZAttachment)

    @Delete
    fun deleteBZAttachment(bzAttachment: BZAttachment)

    @Query("Select * from attachments where attachmentId like :id limit 1")
    fun selectBZAttachment(id: String): BZAttachment?

    @Query("Select * from attachments where messageId like :messageId")
    fun getBZAttachmentByMessageId(messageId: String): List<BZAttachment>
}