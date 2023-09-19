package com.example.recyclerlistacontatos.editcontacts

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.isDigitsOnly
import androidx.core.widget.doOnTextChanged
import com.example.recyclerlistacontatos.R
import com.example.recyclerlistacontatos.databinding.EditContactBinding
import com.example.recyclerlistacontatos.models.ContactList
import com.example.recyclerlistacontatos.models.Contacts

class EditContactActivity : AppCompatActivity() {
    private lateinit var binding: EditContactBinding
    private var position: Int = 0
    private var isFieldPhoneValidated: Boolean = false
    private var isFieldNameValidated: Boolean = false
    enum class Field { NAME, PHONE }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = EditContactBinding.inflate(layoutInflater)
        setContentView(binding.root)
        /// todo fix toolbar
        supportActionBar?.hide()
        position = intent.extras?.getInt("contact") as Int
        setupListeners()
        setupFields()
    }

    private fun setupListeners() {
        with(binding) {
            buttonCancel.setOnClickListener {
                finish()
            }

            buttonSave.setOnClickListener {
                updateContactInformation()
            }

            fieldContactName.doOnTextChanged { text, start, before, count ->
                isFieldValidated(Field.NAME)
            }

            fieldContactPhone.doOnTextChanged { text, start, before, count ->
                isFieldValidated(Field.PHONE)
            }

        }
    }

    private fun isFieldValidated(field: Field) {
        with(binding){
            when(field) {
                Field.NAME -> {
                    fieldContactName.error = null
                    if(fieldContactName.text.isEmpty()) fieldContactName.error = getString(R.string.empty_name_warning)
                    isFieldNameValidated = (fieldContactName.error.isNullOrEmpty())
                }
                Field.PHONE -> {
                    fieldContactPhone.error = null
                    if(!fieldContactPhone.text.isDigitsOnly()) fieldContactPhone.error = getString(R.string.incorrect_phone_format_warning)
                    else if(fieldContactPhone.text.toString().length != 11) fieldContactPhone.error = getString(R.string.incorrect_phone_size_warning)
                    isFieldPhoneValidated = (fieldContactPhone.error.isNullOrEmpty())
                }
            }
            buttonSave()
        }
    }

    private fun buttonSave() {
        binding.buttonSave.isEnabled = isFieldNameValidated && isFieldPhoneValidated
    }
    private fun setupFields() {
        with(binding) {
            val (_, name, number) = ContactList.getContact(position)
            fieldContactName.setText(name)
            fieldContactPhone.setText(number)
        }
    }

    private fun updateContactInformation() {
        with(binding) {
            val name = fieldContactName.text.toString()
            val phone = fieldContactPhone.text.toString()

            val shouldAddPhone = !ContactList.phoneExist(phone, position)

            if (shouldAddPhone) {
                val contact = Contacts(name[0].toString().uppercase(), name, phone)
                ContactList.editContact(contact, position)
                printTextOnScreen(name + getString(R.string.edited_contact_warning))
                finish()
            } else printTextOnScreen(getString(R.string.duplicated_contact_warning))
        }
    }

    private fun printTextOnScreen(warning: String) {
        Toast.makeText(this@EditContactActivity, warning, Toast.LENGTH_SHORT).show()
    }
}
