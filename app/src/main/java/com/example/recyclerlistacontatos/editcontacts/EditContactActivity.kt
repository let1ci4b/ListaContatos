package com.example.recyclerlistacontatos.editcontacts

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
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
        setSupportActionBar(binding.mainToolbar.mainToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = getString(R.string.tollbar_editcontact_title)
        position = intent.extras?.getInt("contact") as Int
        binding.buttonSave.isEnabled = false
        setupListeners()
        setupFields()
    }

    private fun setupListeners() {
        with(binding) {
            buttonCancel.setOnClickListener {
                if(ContactList.isContactUnchanged(fieldContactName.text.toString(), fieldContactPhone.text.toString(), position)) finish()
                else showCancelDialog()
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
        with(binding) {
            binding.buttonSave.isEnabled = isFieldNameValidated
                                        && isFieldPhoneValidated
                                        && !ContactList.isContactUnchanged(fieldContactName.text.toString(), fieldContactPhone.text.toString(), position)
        }
    }

    private fun setupFields() {
        with(binding) {
            val (titleImage, name, number) = ContactList.getContact(position)
            fieldContactName.setText(name)
            fieldContactPhone.setText(number)
            contactNameInicial.text = titleImage
        }
    }

    private fun updateContactInformation() {
        with(binding) {
            val name = fieldContactName.text.toString()
            val phone = fieldContactPhone.text.toString()

            val shouldAddPhone = !ContactList.phoneExist(phone, position)

            if (shouldAddPhone) {
                val contact = Contacts(name[0].toString().uppercase(), name, phone, false)
                ContactList.editContact(contact, position)
                printTextOnScreen(name + getString(R.string.edited_contact_warning))
                finish()
            } else printTextOnScreen(getString(R.string.duplicated_contact_warning))
        }
    }

    private fun printTextOnScreen(warning: String) {
        Toast.makeText(this@EditContactActivity, warning, Toast.LENGTH_SHORT).show()
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
