package com.example.recyclerlistacontatos

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.recyclerlistacontatos.databinding.EditContactBinding

class EditContactActivity : AppCompatActivity() {
    private lateinit var binding: EditContactBinding
    private lateinit var contactsList: ArrayList<Contacts>
    private var position: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = EditContactBinding.inflate(layoutInflater)
        supportActionBar?.hide()
        setContentView(binding.root)
        contactsList = intent.extras?.getSerializable("list") as? ArrayList<Contacts> ?: arrayListOf()
        position = intent.extras?.getInt("contact") as Int
        onClickListeners()
        recoverContactInformation()
    }

    private fun onClickListeners() {
        with(binding) {
            buttonCancel.setOnClickListener {
                finish()
            }
            buttonSave.setOnClickListener {

            }
        }
    }
    private fun recoverContactInformation() {
        with(binding) {
            fieldContactName.setText(contactsList[position].nameContact)
            fieldContactPhone.setText(contactsList[position].numberContact)
        }
    }
}
