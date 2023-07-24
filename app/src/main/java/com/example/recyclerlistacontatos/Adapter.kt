package com.example.recyclerlistacontatos

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView

class Adapter(private val contactsList : ArrayList<Contacts>) : RecyclerView.Adapter<Adapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.contact_card, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return contactsList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = contactsList[position]
        holder.titleImage.setImageResource(currentItem.titleImage)
        holder.contactName.text = currentItem.nameContact
        holder.contactNumber.text = currentItem.numberContact

        val isVisible : Boolean = currentItem.visibility
        holder.expandableLayout.visibility = if(isVisible) View.VISIBLE else View.GONE

        holder.contactName.setOnClickListener {
            currentItem.visibility = !currentItem.visibility
            notifyItemChanged(position)
        }
    }

    class  MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleImage : ImageView = itemView.findViewById(R.id.contactImage)
        val contactName : TextView = itemView.findViewById(R.id.contactName)
        val contactNumber : TextView = itemView.findViewById(R.id.contactNumber)
        val expandableLayout : ConstraintLayout = itemView.findViewById(R.id.expandedLayout)
    }
}