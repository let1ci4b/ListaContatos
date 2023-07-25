package com.example.recyclerlistacontatos

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.recyclerlistacontatos.databinding.ContactCardBinding

class Adapter(private val contactsList : ArrayList<Contacts>) : RecyclerView.Adapter<Adapter.MyViewHolder>() {

    class  MyViewHolder(val binding: ContactCardBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(ContactCardBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return contactsList.size
    }
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = contactsList[position]
        holder.binding.contactImage.setImageResource(currentItem.titleImage)
        holder.binding.contactName.text = currentItem.nameContact
        holder.binding.contactNumber.text = currentItem.numberContact

        val isVisible : Boolean = currentItem.visibility
        holder.binding.expandedLayout.visibility = if(isVisible) View.VISIBLE else View.GONE

        holder.binding.contactName.setOnClickListener {
            currentItem.visibility = !currentItem.visibility
            notifyItemChanged(position)
        }

        holder.binding.buttonEdit.setOnClickListener {
            val intent = Intent(it.context, editContactActivity::class.java)
            it.context.startActivity(intent)
        }
    }
}