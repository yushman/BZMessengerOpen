package ooo.emessi.messenger.di

import ooo.emessi.messenger.data.repo.*
import ooo.emessi.messenger.managers.AccountManager
import ooo.emessi.messenger.managers.ChatManager
import ooo.emessi.messenger.managers.MessagesManager
import ooo.emessi.messenger.managers.MucChatManager
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val myModule = module {
    single { AccountRepo(androidContext()) }
    single { ContactRepo(androidContext()) }
    single { MessageRepo(androidContext()) }
    single { ChatRepo(androidContext()) }
    single { AttachmentRepo(androidContext()) }
    single { AccountManager() }
}
