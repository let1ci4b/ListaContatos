package com.example.recyclerlistacontatos.main

import android.content.Intent
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.recyclerlistacontatos.databinding.ContactCardBinding
import com.example.recyclerlistacontatos.editcontacts.EditContactActivity
import com.example.recyclerlistacontatos.models.ContactList
import com.example.recyclerlistacontatos.models.Contacts

class RecyclerViewViewHolder(private var binding: ContactCardBinding, private val onItemClickListener: RecyclerViewAdapter.OnItemClick) : RecyclerView.ViewHolder(binding.root) {

    private lateinit var recyclerViewAdapter: RecyclerViewAdapter
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
                onItemClickListener.onItemClick(adapterPosition, 1)
            }
            buttonMessage.setOnClickListener {
                onItemClickListener.onItemClick(adapterPosition, 2)
            }
            unfoldLayout.setOnLongClickListener {
                recyclerViewAdapter.toggleIcon(binding, adapterPosition)
                onItemClickListener.onLongPress(itemView, ContactList.getContact(adapterPosition), adapterPosition)
//                if (itemClick == null) {
//                    return false;
//                } else {
//                    itemClick.onLongPress(view, list.get(position), position);
//                    return true;
//                }
                //onItemClickListener.onLongClick(adapterPosition)
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