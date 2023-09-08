package com.example.recyclerlistacontatos.main

import android.content.Intent
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.recyclerlistacontatos.databinding.ContactCardBinding
import com.example.recyclerlistacontatos.editcontacts.EditContactActivity
import com.example.recyclerlistacontatos.models.Contacts

class RecyclerViewViewHolder(private var binding: ContactCardBinding, private val onItemClickListener: RecyclerViewAdapter.OnItemClickListener) : RecyclerView.ViewHolder(binding.root) {

    fun bind(contact: Contacts, isExpanded: Boolean, expandableCallback: () -> Unit) {
        with(binding) {
            nameInicialImage.text = contact.titleImage
            contactName.text = contact.nameContact
            contactNumber.text = "(" + contact.numberContact.substring(0, 2) + ")" + contact.numberContact.substring(2, 7) + "-" + contact.numberContact.substring(7, 11)
        }
        setupListeners()
        setupView(isExpanded, expandableCallback)
    }

    private fun setupListeners(){
        with(binding) {
            buttonCall.setOnClickListener {
                onItemClickListener.onClick(adapterPosition, 1)
            }
            buttonMessage.setOnClickListener {
                onItemClickListener.onClick(adapterPosition, 2)
            }
            unfoldLayout.setOnLongClickListener {
                onItemClickListener.onLongClick(adapterPosition)
                return@setOnLongClickListener true
            }
        }
    }

    private fun setupView(isExpanded: Boolean, expandableCallback: (() -> Unit)?) {
        with(binding) {
            if (isExpanded) {
                expandedLayout.visibility = View.VISIBLE

                buttonEdit.setOnClickListener {
                    val intent = Intent(it.context, EditContactActivity::class.java)
                    intent.putExtra("contact", position)
                    it.context.startActivity(intent)
                }

            } else {
                expandedLayout.visibility = View.GONE
            }

            unfoldLayout.setOnClickListener {
                expandableCallback?.invoke()
            }
        }
    }

    fun unfoldContact() {
        setupView(false, null)
    }

}