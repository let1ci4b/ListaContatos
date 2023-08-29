package com.example.recyclerlistacontatos.addcontacts

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.isDigitsOnly
import androidx.core.widget.doOnTextChanged
import com.example.recyclerlistacontatos.R
import com.example.recyclerlistacontatos.databinding.AddContactBinding
import com.example.recyclerlistacontatos.models.ContactList
import com.example.recyclerlistacontatos.models.Contacts

class AddContactActivity : AppCompatActivity() {
    private lateinit var binding: AddContactBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AddContactBinding.inflate(layoutInflater)
        supportActionBar?.hide()
        setContentView(binding.root)
        /// todo implement actionBar with binding
        setSupportActionBar(findViewById(R.id.mainToolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setupListeners()
    }

    private fun setupListeners() {
        with(binding) {
           buttonCancel.setOnClickListener {
                finish()
                printTextOnScreen("Alterações não salvas.")
            }

            buttonSave.setOnClickListener {
                validateNewContact()
                if(buttonSave.isEnabled) saveNewContact() else printTextOnScreen("Preencha os campos corretamente.")
            }

            fieldContactName.doOnTextChanged { text, start, before, count ->
                validateNewContact()
            }

            fieldContactPhone.doOnTextChanged { text, start, before, count ->
                validateNewContact()
            }

            //fieldContactPhone.onFocusChangeListener
        }
    }

    private fun validateNewContact() {
        with(binding) {
            buttonSave.isEnabled = false
            when {
                fieldContactName.text.isEmpty() -> fieldContactName.setError("Insira um nome.")
                fieldContactName.text.isDigitsOnly() -> fieldContactName.setError("Esse campo não pode conter apenas números.")
                !fieldContactPhone.text.isDigitsOnly() -> fieldContactPhone.setError("Insira apenas números.")
                fieldContactPhone.text.length != 11 -> fieldContactPhone.setError("Telefone deve conter 11 dígitos.")
                else ->  buttonSave.isEnabled = true
            }
        }
    }

    private fun saveNewContact() {
        with(binding) {
            val name = fieldContactName.text.toString()
            val phone = fieldContactPhone.text.toString()

            val shouldAddPhone = !ContactList.phoneExist(phone, null)

            if (shouldAddPhone) {
                val contact = Contacts(name[0].toString().uppercase(), name, phone)
                ContactList.addContact(contact)
                printTextOnScreen("$name foi adicionado(a) com sucesso!")
                finish()
            } else printTextOnScreen("Esse número já está na lista.")
        }
    }

    private fun printTextOnScreen(warning: String) {
        Toast.makeText(this@AddContactActivity, warning, Toast.LENGTH_SHORT).show()
    }
}
