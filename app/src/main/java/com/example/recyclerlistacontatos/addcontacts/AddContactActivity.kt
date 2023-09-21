package com.example.recyclerlistacontatos.addcontacts

import android.content.DialogInterface
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.isDigitsOnly
import androidx.core.widget.doOnTextChanged
import com.example.recyclerlistacontatos.R
import com.example.recyclerlistacontatos.databinding.AddContactBinding
import com.example.recyclerlistacontatos.models.ContactList
import com.example.recyclerlistacontatos.models.Contacts

class AddContactActivity : AppCompatActivity() {
    private lateinit var binding: AddContactBinding
    private var isFieldPhoneValidated: Boolean = false
    private var isFieldNameValidated: Boolean = false
    enum class Field { NAME, PHONE }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AddContactBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.mainToolbar.mainToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = "Novo Contato"

        setupListeners()
        binding.buttonSave.isEnabled = false
    }

    /// TODO implement dialogs
    private fun setupListeners() {
        with(binding) {
           buttonCancel.setOnClickListener {
               finish()
//               setupOnCancelDialog()
                printTextOnScreen(getString(R.string.unsaved_alterations_warning))
            }

            buttonSave.setOnClickListener {
                saveNewContact()
            }

            fieldContactName.doOnTextChanged { text, start, before, count ->
                isFieldValidated(Field.NAME)
            }

            fieldContactPhone.doOnTextChanged { text, start, before, count ->
                isFieldValidated(Field.PHONE)
            }

        }
    }

//    private fun setupOnCancelDialog() {
//        val builder = AlertDialog.Builder(this)
//        builder.setTitle("Androidly Alert")
//        builder.setMessage("We have a message")
//        builder.setPositiveButton("OK", DialogInterface.OnClickListener(function = x))
//
//        builder.setPositiveButton(android.R.string.yes) { dialog, which ->
//            Toast.makeText(applicationContext,
//                android.R.string.yes, Toast.LENGTH_SHORT).show()
//        }
//
//        builder.setNegativeButton(android.R.string.no) { dialog, which ->
//            Toast.makeText(applicationContext,
//                android.R.string.no, Toast.LENGTH_SHORT).show()
//        }
//
//        builder.setNeutralButton("Maybe") { dialog, which ->
//            Toast.makeText(applicationContext,
//                "Maybe", Toast.LENGTH_SHORT).show()
//        }
//        builder.show()
//    }

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

    private fun saveNewContact() {
        with(binding) {
            val name = fieldContactName.text.toString()
            val phone = fieldContactPhone.text.toString()

            val shouldAddPhone = !ContactList.phoneExist(phone, null)

            if (shouldAddPhone) {
                val contact = Contacts(name[0].toString().uppercase(), name, phone)
                ContactList.addContact(contact)
                printTextOnScreen(name + getString(R.string.added_contact_warning))
                finish()
            } else printTextOnScreen(getString(R.string.duplicated_contact_warning))
        }
    }

    private fun printTextOnScreen(warning: String) {
        Toast.makeText(this@AddContactActivity, warning, Toast.LENGTH_SHORT).show()
    }
}
