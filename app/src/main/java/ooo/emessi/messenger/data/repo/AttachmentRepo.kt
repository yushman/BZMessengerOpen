package ooo.emessi.messenger.data.repo

import android.content.Context
import ooo.emessi.messenger.data.database.BZDatabase
import ooo.emessi.messenger.data.model.bz_model.attachment.BZAttachment

class AttachmentRepo(context: Context){
    private val bzDatabase = BZDatabase.getInstance(context)
    private val dao = bzDatabase.attachmentDao()

    fun getAttachments(messageId: String): List<BZAttachment> {
        return dao.getBZAttachmentByMessageId(messageId)
    }

    fun getAttachment(id: String): BZAttachment? {
        return dao.selectBZAttachment(id)
    }

    fun saveAttachment(attachment: BZAttachment){
        dao.insertBZAttachment(attachment)
    }

    fun deleteAttachment(attachment: BZAttachment){
        dao.deleteBZAttachment(attachment)
    }

    fun updateAttachment(attachment: BZAttachment){
        dao.updateBZAttachment(attachment)
    }
}