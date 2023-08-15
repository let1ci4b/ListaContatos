package com.example.recyclerlistacontatos.addcontacts

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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
        onClickListeners()
    }

    private fun onClickListeners() {
        with(binding) {
           buttonCancel.setOnClickListener {
                setResult(RESULT_CANCELED)
                finish()
            }
            buttonSave.setOnClickListener {
                saveNewContact()
            }
        }
    }

    private fun saveNewContact() {
        with(binding) {
            val name = fieldContactName.text.toString()
            val phone = fieldContactPhone.text.toString()

            if (!invalidInputs(name, phone) && !ContactList.phoneExist(phone, null)) {
                val contact = Contacts(name[0].toString().uppercase(), name, phone, false)
                ContactList.addContact(contact)
                finish()
            }
        }
    }
    private fun invalidInputs(name: String, phone: String) : Boolean {

        return if((name.isEmpty() || phone.isEmpty() || !phone.matches(Regex("[0-9]*")) || name.matches(Regex("[0-9]*")))) {
            Toast.makeText(this@AddContactActivity, "Preencha os campos corretamente!", Toast.LENGTH_SHORT).show()
            true
        } else if(phone.length != 11) {
            Toast.makeText(this@AddContactActivity, "Telefone deve conter 11 digitos!", Toast.LENGTH_SHORT).show()
            true
        } else {
            false
        }
    }



//    private fun phoneExists(name: String, phone: String) : Boolean {
//
//        var exist = false
//
//        contactsList.forEach {
//            if(it.numberContact.equals(phone)) {
//                exist = true
//            }
//        }
//
//        if (exist && !invalidInputs(name, phone)) {
//           Toast.makeText(this@AddContactActivity, "Este número já esta na lista!", Toast.LENGTH_SHORT).show()
//        }
//
//        return exist
//    }
}
