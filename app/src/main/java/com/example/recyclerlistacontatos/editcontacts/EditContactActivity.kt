package com.example.recyclerlistacontatos.editcontacts

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import com.example.recyclerlistacontatos.R
import com.example.recyclerlistacontatos.databinding.EditContactBinding
import com.example.recyclerlistacontatos.models.ConfirmationDialog
import com.example.recyclerlistacontatos.models.Field

class EditContactActivity : AppCompatActivity() , ConfirmationDialog.ConfirmationDialogListener {
    private lateinit var binding: EditContactBinding
    private var position: Int = 0
    private var viewModel: EditContactViewModel = EditContactViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = EditContactBinding.inflate(layoutInflater)
        setContentView(binding.root)
        position = intent.extras?.getInt("contact") as Int
        binding.buttonSave.isEnabled = false
        setupToolbar()
        setupListeners()
        with(binding) {
            viewModel.setupFields(position, fieldContactName, fieldContactPhone, contactNameInicial)
        }
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
                viewModel.isFieldValidated(Field.NAME, fieldContactName, layoutContactName)
            }
            fieldContactPhone.doOnTextChanged { text, start, before, count ->
                viewModel.isFieldValidated(Field.PHONE, fieldContactPhone, layoutContactPhone)
            }

        }
    }

    private fun returnToMainActivity() {
        with(binding) {
            if (viewModel.isContactUnchanged(fieldContactName.text.toString(), fieldContactPhone.text.toString(), position)) finish()
            else showCancelDialog()
        }
    }


    private fun updateContactInformation() {
        with(binding) {
            if(viewModel.updateContactInformation(viewModel.extractName(fieldContactName.text.toString()), fieldContactPhone.text.toString(), position, buttonSave)) {
                printTextOnScreen(viewModel.extractName(fieldContactName.text.toString()) + getString(R.string.edited_contact_warning))
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
        if(binding.buttonSave.isEnabled) updateContactInformation()
    }

    override fun onNegativeButtonClicked() {
        finish()
        printTextOnScreen(getString(R.string.unsaved_alterations_warning))
    }

    override fun onNeutralButtonClicked() {}
}
