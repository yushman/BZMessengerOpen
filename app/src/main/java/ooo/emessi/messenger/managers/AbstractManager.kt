package ooo.emessi.messenger.managers

import ooo.emessi.messenger.data.repo.AccountRepo
import ooo.emessi.messenger.data.repo.ChatRepo
import ooo.emessi.messenger.data.repo.ContactRepo
import ooo.emessi.messenger.data.repo.MessageRepo
import org.koin.core.KoinComponent
import org.koin.core.inject

abstract class AbstractManager: KoinComponent {
    val chatRepo by inject<ChatRepo>()
    val contactRepo by inject<ContactRepo>()
    val messageRepo by inject<MessageRepo>()
    val accountRepo by inject<AccountRepo>()
}