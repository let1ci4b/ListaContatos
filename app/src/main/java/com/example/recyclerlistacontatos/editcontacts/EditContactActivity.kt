package com.example.recyclerlistacontatos.editcontacts

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.isDigitsOnly
import androidx.core.widget.doOnTextChanged
import com.example.recyclerlistacontatos.R
import com.example.recyclerlistacontatos.databinding.EditContactBinding
import com.example.recyclerlistacontatos.contactsList.ContactList
import com.example.recyclerlistacontatos.models.ConfirmationDialog
import com.example.recyclerlistacontatos.models.Contacts

class EditContactActivity : AppCompatActivity() , ConfirmationDialog.ConfirmationDialogListener {
    private lateinit var binding: EditContactBinding
    private var position: Int = 0
    private var isFieldPhoneValidated: Boolean = false
    private var isFieldNameValidated: Boolean = false
    enum class Field { NAME, PHONE }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = EditContactBinding.inflate(layoutInflater)
        setContentView(binding.root)
        position = intent.extras?.getInt("contact") as Int
        binding.buttonSave.isEnabled = false
        setupToolbar()
        setupListeners()
        setupFields()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.mainToolbar.mainToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = getString(R.string.tollbar_editcontact_title)
    }

    private fun setupListeners() {
        with(binding) {
            mainToolbar.mainToolbar.setNavigationOnClickListener {
                returnToMainActivity()
            }
            buttonCancel.setOnClickListener {
                returnToMainActivity()
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

    private fun returnToMainActivity() {
        with(binding) {
            if (ContactList.isContactUnchanged(fieldContactName.text.toString(), fieldContactPhone.text.toString(), position)) finish()
            else showCancelDialog()
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
        val dialog = ConfirmationDialog.newInstance(R.string.button_cancel, R.string.cancel_dialog_message, R.string.button_save, R.string.button_discard, R.string.button_cancel)
        dialog.show(supportFragmentManager, ConfirmationDialog.TAG)
    }

    override fun onPositiveButtonClicked() {
        updateContactInformation()
    }

    override fun onNegativeButtonClicked() {
        finish()
        printTextOnScreen(getString(R.string.unsaved_alterations_warning))
    }

    override fun onNeutralButtonClicked() {}
}
