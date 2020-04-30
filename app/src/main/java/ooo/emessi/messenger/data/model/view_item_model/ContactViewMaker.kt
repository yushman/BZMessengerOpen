package ooo.emessi.messenger.data.model.view_item_model

import ooo.emessi.messenger.data.model.dto_model.contact.ContactDto
import ooo.emessi.messenger.data.model.view_item_model.contact.ContactViewItem

class ContactViewMaker : AbstractMaker() {
    fun makeList(list: List<ContactDto>): List<ContactViewItem>{
        val result = mutableListOf<ContactViewItem>()

        if (list.isNullOrEmpty()) return result
        if (list.size == 1) return listOf(
            ContactViewItem(
                list.first(),
                true
            )
        )
        else result.add(ContactViewItem(list.first(), true))
        for (i in 1 until list.size) {
            val current = list[i]
            val previous = list[i - 1]
            if (current.name.first().toUpperCase() != previous.name.first().toUpperCase()) {
                result.add(ContactViewItem(current, true))
            } else {
                result.add(ContactViewItem(current, false))
            }
        }
        return result
    }
}