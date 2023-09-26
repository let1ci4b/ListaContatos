package com.example.recyclerlistacontatos.main

import android.annotation.SuppressLint
import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.recyclerlistacontatos.databinding.ContactCardBinding
import com.example.recyclerlistacontatos.models.Contacts


class RecyclerViewAdapter(
    private var contactsList : ArrayList<Contacts>,
    private val recyclerView: RecyclerView,
    private val onItemClickListener: OnItemClick,
    ) : RecyclerView.Adapter<RecyclerViewViewHolder>() {

    private var expandedPosition : Int? = null
    private var selectedItems: SparseBooleanArray = SparseBooleanArray()
    var selectedIndex: Int = -1
    private lateinit var itemClick: OnItemClick

//    fun setItemClick(itemClick: OnItemClick?) {
//        this.itemClick = itemClick!!
//    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewViewHolder {
        val binding = ContactCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecyclerViewViewHolder(binding, onItemClickListener)
    }

    fun filterList(filterlist: ArrayList<Contacts>) {
        contactsList = filterlist
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: RecyclerViewViewHolder, position: Int) {
        holder.bind(contactsList[position], position == expandedPosition) {
            expandedPosition?.let { (recyclerView.findViewHolderForAdapterPosition(it) as? RecyclerViewViewHolder)?.unfoldContact() }
            expandedPosition = if (expandedPosition != holder.adapterPosition) holder.adapterPosition else null
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

    interface OnItemClick {
        fun onItemClick(position: Int, action: Int)
        fun onLongPress(view: View, contact: Contacts, position: Int)

    }

    //This method will trigger when we we long press the item and it will change the icon of the item to check icon.
    fun toggleIcon(binding: ContactCardBinding, position: Int) {
        with(binding){
            if (selectedItems.get(position, false)) {
//                lytImage.setVisibility(View.GONE)
//                lytChecked.setVisibility(View.VISIBLE)
                if (selectedIndex === position) selectedIndex = -1
            } else {
//                lytImage.setVisibility(View.VISIBLE)
//                lytChecked.setVisibility(View.GONE)
                if (selectedIndex === position) selectedIndex = -1
            }
        }
    }

    //This method helps you to get all selected items from the list
    fun getSelectedItems(): List<Int>? {
        val items: MutableList<Int> = arrayListOf(selectedItems.size())
        for (i in 0 until selectedItems.size()) {
            items.add(selectedItems.keyAt(i))
        }
        return items
    }

    //for clearing our selection
    fun clearSelection() {
        selectedItems.clear()
        notifyDataSetChanged()
    }

    //this function will toggle the selection of items
    fun toggleSelection(position: Int) {
        selectedIndex = position
        if (selectedItems.get(position, false)) {
            selectedItems.delete(position)
        } else {
            selectedItems.put(position, true)
        }
        notifyItemChanged(position)
    }


    //How many items have been selected? this method exactly the same . this will return a total number of selected items.
    fun selectedItemCount(): Int {
        return selectedItems.size()
    }

}