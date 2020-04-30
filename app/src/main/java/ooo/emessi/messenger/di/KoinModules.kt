package ooo.emessi.messenger.di

import androidx.room.Room
import com.google.gson.Gson
import ooo.emessi.messenger.IOScope
import ooo.emessi.messenger.XMPPScope
import ooo.emessi.messenger.data.database.BZDatabase
import ooo.emessi.messenger.data.model.dto_model.AttachmentDtoMaker
import ooo.emessi.messenger.data.repo.AccountRepo
import ooo.emessi.messenger.data.repo.ChatRepo
import ooo.emessi.messenger.data.repo.ContactRepo
import ooo.emessi.messenger.data.repo.MessageRepo
import ooo.emessi.messenger.managers.account.AccountManager
import ooo.emessi.messenger.managers.chat.ChatFactory
import ooo.emessi.messenger.managers.roster.RosterManager
import ooo.emessi.messenger.utils.helpers.SharedPreferencesHelper
import ooo.emessi.messenger.xmpp.XMPPApi
import ooo.emessi.messenger.xmpp.XMPPManagersFactory
import ooo.emessi.messenger.xmpp.connection.ConnectionOptions
import ooo.emessi.messenger.xmpp.managers.ConnectionManager
import ooo.emessi.messenger.xmpp.managers.StanzaManager
import ooo.emessi.messenger.xmpp.managers.XRosterManager
import org.jivesoftware.smack.tcp.XMPPTCPConnection
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val myModule = module {
    single {
        Room.databaseBuilder(
            androidContext(),
            BZDatabase::class.java,
            "BZ_database"
        ).build()
    }
    single { ConnectionOptions.getDefaultOptions() }
    single {
        XMPPTCPConnection(
            ConnectionManager.configureConnectionBuilder(get()).build()
        )
    }
    single { ConnectionManager(get()) }
    single { StanzaManager(get()) }
    single { XRosterManager(get()) }
    single { XMPPManagersFactory(get()) }
    single { XMPPApi(get()) }
    single { get<BZDatabase>().accountDao() }
    single { get<BZDatabase>().chatDao() }
    single { get<BZDatabase>().messageDao() }
    single { get<BZDatabase>().contactDao() }
    single { SharedPreferencesHelper(androidContext()) }
    single { AccountRepo(get()) }
    single { ContactRepo(get()) }
    single { MessageRepo(get()) }
    single { ChatRepo(get()) }
    single { Gson() }
    single { AccountManager() }
    single { RosterManager() }
    factory { AttachmentDtoMaker() }
    factory { IOScope() }
    factory { XMPPScope() }
}