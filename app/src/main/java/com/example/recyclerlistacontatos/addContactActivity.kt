package com.example.recyclerlistacontatos

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.recyclerlistacontatos.databinding.AddContactBinding

class addContactActivity : AppCompatActivity() {
    private lateinit var binding: AddContactBinding
    //val contacts = ArrayList<Contacts>()
    val contact1 = ArrayContacts()

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
                finish()
            }
            buttonSave.setOnClickListener {
                val name = fieldContactName.text.toString()
                val phone = fieldContactPhone.text.toString()

                val contact = Contacts(1, name, phone, true)
                contact1.contacts.add(contact)
                Toast.makeText(this@addContactActivity, "$contact1.contacts", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}
