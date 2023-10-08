package com.example.recyclerlistacontatos.addcontacts
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
        title = getString(R.string.tollbar_addcontact_title)
        binding.buttonSave.isEnabled = false
        setupListeners()
    }
    private fun setupListeners() {
        with(binding) {
            buttonCancel.setOnClickListener {
                if(fieldContactName.text.isNullOrEmpty() && fieldContactPhone.text.isNullOrEmpty()) finish()
                else showCancelDialog()
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
    private fun isFieldValidated(field: Field) {
        with(binding){
            when(field) {
                Field.NAME -> {
                    if(fieldContactName.text.toString().isBlank()) layoutContactName.error = getString(R.string.empty_name_warning)
                    else layoutContactName.error = null
                    isFieldNameValidated = (layoutContactName.error.isNullOrEmpty())

                }
                Field.PHONE -> {
                    if(!fieldContactPhone.text?.toString()?.isDigitsOnly()!!) layoutContactPhone.error = getString(R.string.incorrect_phone_format_warning)
                    else if(fieldContactPhone.text?.toString()?.length != 11) layoutContactPhone.error = getString(R.string.incorrect_phone_size_warning)
                    else layoutContactPhone.error = null
                    isFieldPhoneValidated = (layoutContactPhone.error.isNullOrEmpty())
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
                val contact = Contacts(name[0].toString().uppercase(), name, phone, false)
                ContactList.addContact(contact)
                printTextOnScreen(name + getString(R.string.added_contact_warning))
                finish()
            } else printTextOnScreen(getString(R.string.duplicated_contact_warning))
        }
    }
    private fun printTextOnScreen(warning: String) {
        Toast.makeText(this@AddContactActivity, warning, Toast.LENGTH_SHORT).show()
    }

    private fun showCancelDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.button_cancel))
            .setMessage(getString(R.string.cancel_dialog_message))
            .setPositiveButton(getString(R.string.yes)){dialog, witch ->
                finish()
                printTextOnScreen(getString(R.string.unsaved_alterations_warning))
            }
            .setNegativeButton(getString(R.string.no)){dialog, witch ->
                dialog.dismiss()
            }
        val alertDialog : AlertDialog = builder.create()
        alertDialog.show()
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(resources.getColor(R.color.box_stroke))
        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(resources.getColor(R.color.box_stroke))
    }
}