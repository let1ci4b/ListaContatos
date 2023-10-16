package com.example.recyclerlistacontatos.editcontacts

import android.widget.TextView
import androidx.core.text.isDigitsOnly
import com.example.recyclerlistacontatos.ContactsApplication
import com.example.recyclerlistacontatos.contactsList.ContactList
import com.example.recyclerlistacontatos.models.Contacts
import com.example.recyclerlistacontatos.models.Field
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

/// TODO get Strings file reference

class EditContactViewModel {
    private val context: ContactsApplication = ContactsApplication() // returning NPE
    var isFieldPhoneValidated: Boolean = false
    var isFieldNameValidated: Boolean = false

    fun setupFields(position: Int, fieldContactName: TextInputEditText,
                    fieldContactPhone: TextInputEditText, contactNameInitial: TextView) {
        val (titleImage, name, number) = ContactList.getContact(position)
        fieldContactName.setText(name)
        fieldContactPhone.setText(number)
        contactNameInitial.text = titleImage
    }

    fun isFieldValidated(field: Field, inputText: TextInputEditText, inputLayout: TextInputLayout) {
        when (field) {
            Field.NAME -> {
                inputText.error = null
                when {
                    inputText.text.toString().isBlank() -> inputLayout.error = "Insira um nome."
                    else -> inputLayout.error = null
                }
                isFieldNameValidated = (inputLayout.error.isNullOrEmpty())
            }

            Field.PHONE -> {
                when {
                    !inputText.text?.toString()?.isDigitsOnly()!! -> inputLayout.error = "Insira apenas números."
                    inputText.text?.toString()?.length != 11 -> inputLayout.error = "Telefone deve conter 11 dígitos."
                    else -> inputLayout.error = null
                }
                isFieldPhoneValidated = (inputLayout.error.isNullOrEmpty())
            }
        }
    }

    fun updateContactInformation(name: String, phone: String, position: Int) : Boolean {
        val shouldAddPhone = !ContactList.phoneExist(phone, position)
        return if (shouldAddPhone) {
            val contact = Contacts(name[0].uppercase(), name, phone, false)
            ContactList.editContact(contact, position)
            true
        } else false
    }

    fun isContactUnchanged(name: String, phone:String, position: Int) : Boolean {
        return ContactList.isContactUnchanged(name, phone, position)
    }
}