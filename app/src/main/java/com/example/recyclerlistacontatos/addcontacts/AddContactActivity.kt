package com.example.recyclerlistacontatos.addcontacts
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import com.example.recyclerlistacontatos.R
import com.example.recyclerlistacontatos.databinding.AddContactBinding
import com.example.recyclerlistacontatos.models.ConfirmationDialog
import com.example.recyclerlistacontatos.models.Field

class AddContactActivity : AppCompatActivity(), ConfirmationDialog.ConfirmationDialogListener {
    private lateinit var binding: AddContactBinding
    private var viewModel: AddContactViewModel = AddContactViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AddContactBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.buttonSave.isEnabled = false
        setupToolbar()
        setupListeners()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.mainToolbar.mainToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = getString(R.string.tollbar_addcontact_title)
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
                saveNewContact()
            }
            fieldContactName.doOnTextChanged { text, start, before, count ->
                viewModel.isFieldValidated(Field.NAME, fieldContactName, layoutContactName)
            }
            fieldContactPhone.doOnTextChanged { text, start, before, count ->
                viewModel.isFieldValidated(Field.PHONE, fieldContactPhone, layoutContactPhone)
            }
        }
    }

    private fun returnToMainActivity() {
        with(binding) {
            if(fieldContactName.text.isNullOrEmpty() && fieldContactPhone.text.isNullOrEmpty()) finish()
            else showCancelDialog()
        }
    }

    private fun saveNewContact() {
        with(binding) {
            if(viewModel.contactSaved(viewModel.extractName(fieldContactName.text.toString()), fieldContactPhone.text.toString(), buttonSave)) {
                printTextOnScreen(viewModel.extractName(fieldContactName.text.toString()) + getString(R.string.added_contact_warning))
                finish()
            } else printTextOnScreen(getString(R.string.duplicated_contact_warning))
        }
    }

    private fun printTextOnScreen(warning: String) {
        Toast.makeText(this@AddContactActivity, warning, Toast.LENGTH_SHORT).show()
    }

    private fun showCancelDialog() {
        val dialog = ConfirmationDialog.newInstance(R.string.button_cancel, R.string.cancel_dialog_message, R.string.button_save,
                                                    R.string.button_discard, R.string.button_cancel)
        dialog.show(supportFragmentManager, ConfirmationDialog.TAG)
    }

    override fun onPositiveButtonClicked() {
        if(binding.buttonSave.isEnabled) saveNewContact()
    }

    override fun onNegativeButtonClicked() {
        finish()
        printTextOnScreen(getString(R.string.unsaved_alterations_warning))
    }

    override fun onNeutralButtonClicked() {}
}