package com.example.recyclerlistacontatos.main

import android.content.Intent
import android.graphics.Color
import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.recyclerlistacontatos.R
import com.example.recyclerlistacontatos.databinding.ContactCardBinding
import com.example.recyclerlistacontatos.editcontacts.EditContactActivity
import com.example.recyclerlistacontatos.models.ContactList
import com.example.recyclerlistacontatos.models.Contacts

class RecyclerViewViewHolder(private var binding: ContactCardBinding, private val onItemClickListener: RecyclerViewAdapter.OnItemClick) : RecyclerView.ViewHolder(binding.root) {

    fun bind(contact: Contacts, isExpanded: Boolean, expandableCallback: (() -> Unit)?) {
        with(binding) {
            setupCheckedCardsLayout(contact)
            nameInicialImage.text = contact.titleImage
            contactName.text = contact.nameContact
            contactNumber.text = "(" + contact.numberContact.substring(0, 2) + ")" + contact.numberContact.substring(2, 7) + "-" + contact.numberContact.substring(7, 11)
            expandedLayout.isVisible = isExpanded
        }
        setupListeners(expandableCallback, contact, isExpanded)
    }

    private fun setupListeners(expandableCallback: (() -> Unit)?, contact: Contacts, isExpanded: Boolean) {
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
            unfoldLayout.setOnLongClickListener {
                ContactList.setupCheck(adapterPosition)
                ContactList.isOnDeleteMode = true
                onItemClickListener.onLongPress(itemView, ContactList.getContact(adapterPosition), adapterPosition)
                setupCheckedCardsLayout(contact)
                if(isExpanded) expandableCallback?.invoke()
                return@setOnLongClickListener true
            }
            unfoldLayout.setOnClickListener {
                if(ContactList.isOnDeleteMode) {
                    ContactList.setupCheck(adapterPosition)
                    setupCheckedCardsLayout(contact)
                    if (ContactList.selectedItemsCount() <= 0) onItemClickListener.onItemClick(adapterPosition, 3)
                    else onItemClickListener.onItemClick(adapterPosition, 4)
                }
                else expandableCallback?.invoke()
            }
        }
    }

    private fun setupCheckedCardsLayout(contact: Contacts) {
        with(binding) {
            if(contact.isChecked) {
                nameInicialImage.visibility = View.GONE
                checkedImage.visibility = View.VISIBLE
                contactImage.isActivated = true
                cardView.isActivated = true
            } else {
                nameInicialImage.visibility = View.VISIBLE
                checkedImage.visibility = View.GONE
                contactImage.isActivated = false
                cardView.isActivated = false
            }
        }
    }
}