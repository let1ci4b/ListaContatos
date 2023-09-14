package com.example.recyclerlistacontatos.main

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.recyclerlistacontatos.databinding.ContactCardBinding
import com.example.recyclerlistacontatos.models.Contacts

class RecyclerViewAdapter(
    private var contactsList : ArrayList<Contacts>,
    private val recyclerView: RecyclerView,
    private val onItemClickListener: OnItemClickListener
    ) : RecyclerView.Adapter<RecyclerViewViewHolder>() {

    private var expandadedPosition : Int? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewViewHolder {
        val binding = ContactCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecyclerViewViewHolder(binding, onItemClickListener)
    }

    // method for filtering our recyclerview items.
    fun filterList(filterlist: ArrayList<Contacts>) {
        contactsList = filterlist
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: RecyclerViewViewHolder, position: Int) {
        holder.bind(contactsList[position], position == expandadedPosition) {
            expandadedPosition?.let { (recyclerView.findViewHolderForAdapterPosition(it) as? RecyclerViewViewHolder)?.unfoldContact() }
            expandadedPosition = if (expandadedPosition != holder.adapterPosition) holder.adapterPosition else null

            notifyDataSetChanged()
        }
    }

    private companion object: DiffUtil.ItemCallback<Contacts>() {
        override fun areItemsTheSame(oldItem: Contacts, newItem: Contacts): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

        override fun areContentsTheSame(oldItem: Contacts, newItem: Contacts): Boolean {
            return oldItem == newItem
        }
    }

    override fun getItemCount(): Int = contactsList.size

    /// todo implements swipe gesture
    interface OnItemClickListener {
        fun onClick(position: Int, action: Int)
        fun onLongClick(position: Int)

    }

}