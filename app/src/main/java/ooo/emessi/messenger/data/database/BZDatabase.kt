package ooo.emessi.messenger.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import ooo.emessi.messenger.data.model.bz_model.account.BZAccount
import ooo.emessi.messenger.data.model.bz_model.attachment.ABZAttachment
import ooo.emessi.messenger.data.model.bz_model.attachment.BZAttachment
import ooo.emessi.messenger.data.model.bz_model.chat.BZChat
import ooo.emessi.messenger.data.model.bz_model.contact.BZContact
import ooo.emessi.messenger.data.model.bz_model.message.BZMessage

@Database(entities = [

    //insert new entities
    BZAccount::class,
    BZChat::class,
    BZMessage::class,
    BZContact::class,
    BZAttachment::class
], version = 2)

abstract class BZDatabase : RoomDatabase() {


    //insert new dao's
    abstract fun accountDao(): AccountDao
    abstract fun chatDao(): ChatDao
    abstract fun messageDao(): MessageDao
    abstract fun contactDao(): ContactDao
    abstract fun attachmentDao(): AttachmentDao

    companion object {
        @Volatile
        private var INSTANCE: BZDatabase? = null

        val MIGRATION_1_2 = object: Migration(1,2){
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE chats ADD COLUMN message_replyed_id TEXT DEFAULT null")
                database.execSQL("ALTER TABLE chats ADD COLUMN message_replyed_from TEXT DEFAULT null")
                database.execSQL("ALTER TABLE chats ADD COLUMN message_replyed_body TEXT DEFAULT null")
                database.execSQL("ALTER TABLE messages ADD COLUMN message_replyed_id TEXT DEFAULT null")
                database.execSQL("ALTER TABLE messages ADD COLUMN message_replyed_from TEXT DEFAULT null")
                database.execSQL("ALTER TABLE messages ADD COLUMN message_replyed_body TEXT DEFAULT null")
            }

        }

        fun getInstance(context: Context): BZDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BZDatabase::class.java,
                    "BZ_database"
                )
                    .addMigrations(MIGRATION_1_2)
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }

}