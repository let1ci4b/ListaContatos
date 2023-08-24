package com.example.recyclerlistacontatos.editcontacts

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.isDigitsOnly
import androidx.core.widget.doOnTextChanged
import com.example.recyclerlistacontatos.databinding.EditContactBinding
import com.example.recyclerlistacontatos.models.ContactList
import com.example.recyclerlistacontatos.models.Contacts

class EditContactActivity : AppCompatActivity() {
    private lateinit var binding: EditContactBinding
    private var position: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = EditContactBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
                validateNewContact()
                updateContactInformation()
            }

            fieldContactName.doOnTextChanged { text, start, before, count ->
                validateNewContact()
            }

            fieldContactPhone.doOnTextChanged { text, start, before, count ->
                validateNewContact()
            }
        }
    }

    private fun validateNewContact() {
        with(binding) {
            buttonSave.isEnabled = false
            /// todo verify fieldContactPhone error
            when {
                fieldContactName.text.isEmpty() -> fieldContactName.setError("Insira um nome.")
                fieldContactName.text.isDigitsOnly() -> fieldContactName.setError("Esse campo não pode conter apenas números.")
                !fieldContactPhone.text.isDigitsOnly() -> fieldContactPhone.setError("Insira apenas números.")
                fieldContactPhone.text.length != 11 -> fieldContactPhone.setError("Telefone deve conter 11 dígitos.")
                else ->  buttonSave.isEnabled = true
            }
        }
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
                printTextOnScreen("$name foi editado(a) com sucesso!")
                finish()
            } else printTextOnScreen("Esse número já está na lista.")
        }
    }

    private fun printTextOnScreen(warning: String) {
        Toast.makeText(this@EditContactActivity, warning, Toast.LENGTH_SHORT).show()
    }
}
