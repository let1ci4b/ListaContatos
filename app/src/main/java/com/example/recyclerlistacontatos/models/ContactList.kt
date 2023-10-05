package com.example.recyclerlistacontatos.models

object ContactList {

    private var contactList = arrayListOf<Contacts>()
    var isOnDeleteMode: Boolean = false

    fun addContact(contact: Contacts){
        contactList.add(contact)
    }

    fun removeContact(){
        contactList.removeAll { it.isChecked }
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

    fun setupCheck(position: Int) {
        contactList[position].isChecked = !(contactList[position].isChecked)
    }

    fun clearCheckSelection() {
        for (contact in contactList) {
            contact.isChecked = false
        }
    }

    fun selectedItemsCount() : Int {
        return contactList.filter { it.isChecked }.size
    }
}

