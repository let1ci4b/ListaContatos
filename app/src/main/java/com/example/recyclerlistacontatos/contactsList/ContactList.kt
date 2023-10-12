package com.example.recyclerlistacontatos.contactsList

import com.example.recyclerlistacontatos.models.Contacts

object ContactList {

    private var contactList = arrayListOf<Contacts>()
    var isOnDeleteMode: Boolean = false

    fun addContact(contact: Contacts) = contactList.add(contact)

    fun removeContact() = contactList.removeAll { it.isChecked }

    fun editContact(contact: Contacts, position: Int) { contactList[position] = contact }

    fun listSize(): Int = contactList.size

    fun getContact(position: Int): Contacts = contactList[position]

    fun getList(): ArrayList<Contacts> = contactList

    fun phoneExist(phone: String, position: Int?) : Boolean {
        return position?.let {
            contactList.any { it.numberContact == phone && phone != contactList[position].numberContact }
        } ?: contactList.any { it.numberContact == phone }
    }

    fun isContactUnchanged(name: String, phone: String, position: Int?) : Boolean {
        return position?.let {
            contactList[position].numberContact == phone && contactList[position].nameContact == name
        } ?: false
    }

    fun setupCheck(position: Int) { contactList[position].isChecked = !(contactList[position].isChecked) }

    fun clearCheckSelection() = contactList.forEach { it.isChecked = false }

    fun selectedItemsCount() : Int = contactList.filter { it.isChecked }.size

    fun selectAll() = contactList.forEach { it.isChecked = true }

    fun getContactPosition(contact: Contacts) : Int = contactList.indexOf(contact)
}

