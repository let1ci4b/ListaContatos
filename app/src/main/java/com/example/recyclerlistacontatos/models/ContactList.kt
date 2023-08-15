package com.example.recyclerlistacontatos.models

object ContactList {

    private var contactList = arrayListOf<Contacts>()

    fun addContact(contact: Contacts){
        contactList.add(contact)
    }

    fun removeContact(contact: Contacts){
        contactList.remove(contact)
    }

    fun editContact(contact: Contacts, position: Int) {
        contactList[position] = contact
    }

    fun listSize(): Int {
        return contactList.size
    }

    fun getContact(position: Int): Contacts {
        return contactList[position]
    }

    fun getList(): ArrayList<Contacts> {
        return contactList
    }

    fun phoneExist(phone: String, position: Int?): Boolean {
        return position?.let {
            contactList.any { it.numberContact == phone && phone != contactList[position].numberContact }
        } ?: contactList.any { it.numberContact == phone }
    }
}

