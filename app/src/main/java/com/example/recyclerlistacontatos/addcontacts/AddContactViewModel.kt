package com.example.recyclerlistacontatos.addcontacts

import androidx.core.text.isDigitsOnly
import com.example.recyclerlistacontatos.ContactsApplication
import com.example.recyclerlistacontatos.contactsList.ContactList
import com.example.recyclerlistacontatos.models.Contacts
import com.example.recyclerlistacontatos.models.Field
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

/// TODO get Strings file reference

class AddContactViewModel {
    private val context: ContactsApplication = ContactsApplication() // returning NPE
    var isFieldPhoneValidated: Boolean = false
    var isFieldNameValidated: Boolean = false

    fun isFieldValidated(field: Field, inputText: TextInputEditText, inputLayout: TextInputLayout) {
        when (field) {
            Field.NAME -> {
                inputText.error = null
                when {
                    inputText.text.toString().isBlank() -> inputLayout.error = "Insira um nome."//context.resources.getString(R.string.empty_name_warning)
                    else -> inputLayout.error = null
                }
                isFieldNameValidated = (inputLayout.error.isNullOrEmpty())
            }

            Field.PHONE -> {
                when {
                    !inputText.text?.toString()?.isDigitsOnly()!! -> inputLayout.error = "Insira apenas números."//context.resources.getString(R.string.incorrect_phone_format_warning)
                    inputText.text?.toString()?.length != 11 -> inputLayout.error = "Telefone deve conter 11 dígitos."//context.resources.getString(R.string.incorrect_phone_size_warning)
                    else -> inputLayout.error = null
                }
                isFieldPhoneValidated = (inputLayout.error.isNullOrEmpty())
            }
        }
    }

    fun contactSaved(name: String, phone: String) : Boolean {
        val shouldAddPhone = !ContactList.phoneExist(phone, null)
        return if (shouldAddPhone) {
            val contact = Contacts(name[0].uppercase(), name, phone, false)
            ContactList.addContact(contact)
            true
        } else false
    }
}