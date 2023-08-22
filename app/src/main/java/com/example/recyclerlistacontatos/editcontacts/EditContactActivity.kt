package com.example.recyclerlistacontatos.editcontacts

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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
                updateContactInformation()
            }

            /// todo validate inputs content with editText
//            fieldContactName.doOnTextChanged { text, start, before, count ->
//                val invalidText = text?.matches(Regex("[0-9]*"))
//                buttonSave.isEnabled = invalidText == false
//            }

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

            val isValidInput = !invalidInputs(name, phone)
            val shouldAddPhone = !ContactList.phoneExist(phone, position)

            if (isValidInput && shouldAddPhone) {
                val contact = Contacts(name[0].toString().uppercase(), name, phone)
                ContactList.editContact(contact, position)
                printTextOnScreen("$name foi editado(a) com sucesso!")
                finish()
            }
        }
    }

    private fun invalidInputs(name: String, phone: String) : Boolean {
        return if((name.isEmpty() || phone.isEmpty() || !phone.matches(Regex("[0-9]*")) || name.matches(Regex("[0-9]*")))) {
            printTextOnScreen("Preencha os campos corretamente!")
            true
        } else if(phone.length != 11) {
            printTextOnScreen("Telefone deve conter 11 digitos!")
            true
        } else {
            false
        }
    }

    private fun printTextOnScreen(warning: String) {
        Toast.makeText(this@EditContactActivity, warning, Toast.LENGTH_SHORT).show()
    }
}