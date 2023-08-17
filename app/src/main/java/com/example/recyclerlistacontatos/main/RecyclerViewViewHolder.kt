package com.example.recyclerlistacontatos.main

import android.content.Intent
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.recyclerlistacontatos.databinding.ContactCardBinding
import com.example.recyclerlistacontatos.editcontacts.EditContactActivity
import com.example.recyclerlistacontatos.models.Contacts

class RecyclerViewViewHolder(private var binding: ContactCardBinding) : RecyclerView.ViewHolder(binding.root) {
    
    fun bind(contact: Contacts) {
        with(binding) {
            nameInicialImage.text = contact.titleImage
            contactName.text = contact.nameContact
            contactNumber.text = "(" + contact.numberContact.substring(0, 2) + ")" + contact.numberContact.substring(2, 7) + "-" + contact.numberContact.substring(7, 11)

            val isVisible : Boolean = contact.isExpanded
            expandedLayout.visibility = if(true) View.VISIBLE else View.GONE

            cardContact.setOnClickListener {
                contact.isExpanded = !contact.isExpanded
            }

            buttonEdit.setOnClickListener {
                val intent = Intent(it.context, EditContactActivity::class.java)
                intent.putExtra("contact", position)

                it.context.startActivity(intent)

            }
        }
    }



}