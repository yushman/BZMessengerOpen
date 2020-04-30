package ooo.emessi.messenger.data.model.view_item_model

import ooo.emessi.messenger.data.repo.ChatRepo
import ooo.emessi.messenger.data.repo.ContactRepo
import ooo.emessi.messenger.data.repo.MessageRepo
import org.koin.core.KoinComponent
import org.koin.core.inject

abstract class AbstractMaker : KoinComponent {
    protected val messageRepo by inject<MessageRepo>()
    protected val chatRepo by inject<ChatRepo>()
    protected val contactRepo by inject<ContactRepo>()
}