package com.example.recyclerlistacontatos.main

import android.content.Intent
import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.recyclerlistacontatos.databinding.ContactCardBinding
import com.example.recyclerlistacontatos.editcontacts.EditContactActivity
import com.example.recyclerlistacontatos.models.ContactList
import com.example.recyclerlistacontatos.models.Contacts

class RecyclerViewViewHolder(private var binding: ContactCardBinding, private val onItemClickListener: RecyclerViewAdapter.OnItemClick) : RecyclerView.ViewHolder(binding.root) {

    private var isOnDeleteAction: Boolean = false
    fun bind(contact: Contacts, isExpanded: Boolean, expandableCallback: (() -> Unit)?) {
        with(binding) {
            /// TODO set icon check visibility with an checked property
//            nameInicialImage.isVisible = !contactImage.isActivated
//            checkedImage.isVisible = contactImage.isActivated
            nameInicialImage.text = contact.titleImage
            contactName.text = contact.nameContact
            contactNumber.text = "(" + contact.numberContact.substring(0, 2) + ")" + contact.numberContact.substring(2, 7) + "-" + contact.numberContact.substring(7, 11)
            expandedLayout.isVisible = isExpanded
        }
        setupListeners(expandableCallback)
    }

    private fun setupListeners(expandableCallback: (() -> Unit)?) {
        with(binding) {
            buttonEdit.setOnClickListener {
                val intent = Intent(it.context, EditContactActivity::class.java)
                intent.putExtra("contact", position)
                it.context.startActivity(intent)
            }

            buttonCall.setOnClickListener {
                onItemClickListener.onItemClick(adapterPosition, 1)
            }
            buttonMessage.setOnClickListener {
                onItemClickListener.onItemClick(adapterPosition, 2)
            }
            root.setOnLongClickListener {
                ContactList.setupCheck(adapterPosition)
                isOnDeleteAction = true
                onItemClickListener.onLongPress(itemView, ContactList.getContact(adapterPosition), adapterPosition)
                return@setOnLongClickListener true
            }
            unfoldLayout.setOnClickListener {
                if(isOnDeleteAction) ContactList.setupCheck(adapterPosition)
                else expandableCallback?.invoke()
            }
        }
    }
}