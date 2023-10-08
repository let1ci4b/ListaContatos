package com.example.recyclerlistacontatos.main

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.recyclerlistacontatos.databinding.ContactCardBinding
import com.example.recyclerlistacontatos.models.Contacts


class RecyclerViewAdapter(
    private var contactsList: ArrayList<Contacts>,
    private val recyclerView: RecyclerView,
    private val onItemClickListener: OnItemClick,
) : RecyclerView.Adapter<RecyclerViewViewHolder>() {

    private var expandedPosition: Int? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewViewHolder {
        val binding = ContactCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecyclerViewViewHolder(binding, onItemClickListener)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun filterList(filterlist: ArrayList<Contacts>) {
        contactsList = filterlist
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: RecyclerViewViewHolder, position: Int) {
        holder.bind(contactsList[position], position == expandedPosition) {

            expandedPosition?.let { (recyclerView.findViewHolderForAdapterPosition(it) as? RecyclerViewViewHolder)?.bind(contactsList[it], false, null) }
            expandedPosition = if (expandedPosition != holder.adapterPosition) holder.adapterPosition else null
            notifyDataSetChanged()
        }
    }

    private companion object : DiffUtil.ItemCallback<Contacts>() {
        override fun areItemsTheSame(oldItem: Contacts, newItem: Contacts): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

        override fun areContentsTheSame(oldItem: Contacts, newItem: Contacts): Boolean {
            return oldItem == newItem
        }
    }

    override fun getItemCount(): Int = contactsList.size

    interface OnItemClick {
        fun onItemClick(position: Int, action: Int)
        fun onLongPress(view: View, contact: Contacts, position: Int)

    }
}