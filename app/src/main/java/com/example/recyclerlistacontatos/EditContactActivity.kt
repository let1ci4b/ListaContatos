package com.example.recyclerlistacontatos

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
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
                setResult(RESULT_CANCELED)
                finish()
            }
            buttonSave.setOnClickListener {
                updateContactInformation()
            }
        }
    }
    private fun recoverContactInformation() {
        with(binding) {
            fieldContactName.setText(contactsList[position].nameContact)
            fieldContactPhone.setText(contactsList[position].numberContact)
        }
    }

    private fun updateContactInformation() {
        with(binding) {
            val name = fieldContactName.text.toString()
            val phone = fieldContactPhone.text.toString()

            if (!invalidInputs(name, phone) && !phoneExists(name, phone)) {
                val contact = Contacts(name.get(0).toString().uppercase(), name, phone, false)

                //atualizar a lista.
                val returnIntent: Intent = Intent()
                returnIntent.putExtra("contactPosition", position)
                returnIntent.putExtra("updatedContact", contact)
                setResult(RESULT_OK, returnIntent)
                finish()
            }
        }
    }

    private fun invalidInputs(name: String, phone: String) : Boolean {

        if((name.isEmpty() || phone.isEmpty() || !phone.matches(Regex("[0-9]*")) || name.matches(Regex("[0-9]*")))) {
            Toast.makeText(this@EditContactActivity, "Preencha os campos corretamente!", Toast.LENGTH_SHORT).show()
            return true
        } else if(phone.length != 11) {
            Toast.makeText(this@EditContactActivity, "Telefone deve conter 11 digitos!", Toast.LENGTH_SHORT).show()
            return true
        } else {
            return false
        }
    }

    private fun phoneExists(name: String, phone: String) : Boolean {

        var exist = false

        contactsList.forEach {
            if(it.numberContact.equals(phone) && phone != contactsList[position].numberContact) {
                exist = true
            }
        }

        if (exist && !invalidInputs(name, phone)) {
            Toast.makeText(this@EditContactActivity, "Este número já esta na lista!", Toast.LENGTH_SHORT).show()
        }

        return exist
    }
}
