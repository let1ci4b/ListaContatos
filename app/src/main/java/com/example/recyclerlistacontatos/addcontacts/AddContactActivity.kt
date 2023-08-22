package com.example.recyclerlistacontatos.addcontacts

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
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
        setupListeners()
    }

    private fun setupListeners() {
        with(binding) {
           buttonCancel.setOnClickListener {
                finish()
                printTextOnScreen("Alterações não salvas.")
            }
            buttonSave.setOnClickListener {
                saveNewContact()
            }
            /// todo validate inputs content with editText
            fieldContactName.doOnTextChanged { text, start, before, count ->
                if (invalidInputs(fieldContactName.text.toString(), fieldContactPhone.text.toString())) {
                    fieldContactName.setError("Esse campo não pode conter apenas números.")
                    buttonSave.isEnabled = false
                }
            }
        }
    }

    private fun saveNewContact() {
        with(binding) {
            val name = fieldContactName.text.toString()
            val phone = fieldContactPhone.text.toString()

            if (!invalidInputs(name, phone) && !ContactList.phoneExist(phone, null)) {
                val contact = Contacts(name[0].toString().uppercase(), name, phone)
                ContactList.addContact(contact)
                printTextOnScreen("$name foi adicionado(a) com sucesso!")
                finish()
            }
        }
    }
    private fun invalidInputs(name: String, phone: String) : Boolean {
        return if((name.isEmpty() || phone.isEmpty() || !phone.matches(Regex("[0-9]*")) || name.matches(Regex("[0-9]*")))) {
            //printTextOnScreen("Preencha os campos corretamente!")
            true
        } else phone.length != 11
    }

    private fun printTextOnScreen(warning: String) {
        Toast.makeText(this@AddContactActivity, warning, Toast.LENGTH_SHORT).show()
    }
}
