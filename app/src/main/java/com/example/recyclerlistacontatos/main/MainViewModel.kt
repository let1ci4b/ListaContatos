package com.example.recyclerlistacontatos.main

import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.recyclerlistacontatos.contactsList.ContactList
import com.example.recyclerlistacontatos.models.Contacts
import com.google.android.material.bottomappbar.BottomAppBar

/// TODO get Strings file reference

class MainViewModel () {

    fun noResultOnSearch(noContactsWarning: TextView, recyclerView: RecyclerView) {
        noContactsWarning.visibility = View.VISIBLE
        noContactsWarning.text = "Sem resultados."
        recyclerView.visibility = View.GONE
    }

    fun resultOnSearch(noContactsWarning: TextView, recyclerView: RecyclerView) {
        noContactsWarning.visibility = View.GONE
        recyclerView.visibility = View.VISIBLE
    }

    fun noSearch(noContactsWarning: TextView, iconNoContactWarning: ImageView, recyclerView: RecyclerView) {
        noContactsWarning.visibility = View.VISIBLE
        iconNoContactWarning.visibility = View.GONE
        noContactsWarning.text = "Realize uma busca."
        recyclerView.visibility = View.GONE
    }

    fun onSearchDefault(recyclerViewAdapter: RecyclerViewAdapter, searchBar: ConstraintLayout, searchIcon: ImageView, searchViewQuery: EditText) {
        searchBar.visibility = View.VISIBLE
        searchIcon.visibility = View.GONE
        searchViewQuery.text = null
        recyclerViewAdapter.filterList(getAlphabeticalSortedList())
    }

    fun onSearchModeExit(recyclerViewAdapter: RecyclerViewAdapter, searchIcon: ImageView, searchBar: ConstraintLayout,
                         noContactsWarning: TextView, iconNoContactWarning: ImageView, recyclerView: RecyclerView) {
        searchIcon.visibility = View.VISIBLE
        searchBar.visibility = View.GONE
        noContactsWarning.visibility = View.GONE
        recyclerView.visibility = View.VISIBLE
        recyclerViewAdapter.filterList(ContactList.getList().sortedBy { it.nameContact.lowercase() }.toCollection(ArrayList<Contacts>()))
        showNoContactsWarning(iconNoContactWarning, noContactsWarning)
    }

    fun deleteModeOn(selectionItems: ConstraintLayout, bottomBar: BottomAppBar, bottomBarTitle: TextView,
                    selectAll: ImageView, allSelected: ImageView) {
        selectionItems.visibility = View.VISIBLE
        bottomBar.visibility = View.VISIBLE
        bottomBarTitle.visibility = View.VISIBLE
        bottomBarTitle.text = getSelectItemsCount().toString().plus(" Selecionados")
        if (getSelectItemsCount() == ContactList.listSize()) {
            selectAll.visibility = View.GONE
            allSelected.visibility = View.VISIBLE
        } else {
            selectAll.visibility = View.VISIBLE
            allSelected.visibility = View.GONE
        }
    }

    fun deleteModeOff(recyclerViewAdapter: RecyclerViewAdapter, selectionItems: ConstraintLayout, bottomBar: BottomAppBar, bottomBarTitle: TextView,
                    iconNoContactWarning: ImageView, noContactsWarning: TextView) {
        ContactList.isOnDeleteMode = false
        ContactList.clearCheckSelection()
        selectionItems.visibility = View.GONE
        bottomBar.visibility = View.GONE
        bottomBarTitle.visibility = View.GONE
        showNoContactsWarning(iconNoContactWarning, noContactsWarning)
        recyclerViewAdapter.notifyDataSetChanged()
    }

    fun showNoContactsWarning(iconNoContactWarning: ImageView, noContactsWarning: TextView) {
        if (ContactList.listSize() == 0) {
            iconNoContactWarning.visibility = View.VISIBLE
            noContactsWarning.visibility = View.VISIBLE
            noContactsWarning.text = "Você não possui contatos.\nClique aqui para adicionar!"
        } else {
            iconNoContactWarning.visibility = View.GONE
            noContactsWarning.visibility = View.GONE
        }
    }

    fun selectAllItems(selectAll: ImageView, allSelected: ImageView, recyclerViewAdapter: RecyclerViewAdapter) {
        ContactList.selectAll()
        selectAll.visibility = View.GONE
        allSelected.visibility = View.VISIBLE
        recyclerViewAdapter.notifyDataSetChanged()
    }

    fun allItemsSelected(selectAll: ImageView, allSelected: ImageView, recyclerViewAdapter: RecyclerViewAdapter) {
        ContactList.clearCheckSelection()
        selectAll.visibility = View.VISIBLE
        allSelected.visibility = View.GONE
        recyclerViewAdapter.notifyDataSetChanged()
    }

    fun getContactNumber(position: Int) = ContactList.getContact(position).numberContact

    fun getSelectItemsCount() : Int = ContactList.getList().filter { it.isChecked }.size

    fun getAlphabeticalSortedList() = ContactList.getList().sortedBy { it.nameContact.lowercase() }.toCollection(ArrayList())

    fun deleteListItem(recyclerViewAdapter: RecyclerViewAdapter) {
        ContactList.removeContact()
        recyclerViewAdapter.notifyDataSetChanged()
    }

    fun filter(query: String?, recyclerViewAdapter: RecyclerViewAdapter, iconNoContactWarning: ImageView, noContactsWarning: TextView, recyclerView: RecyclerView) {
        val filteredlist: ArrayList<Contacts> = ArrayList()

        ContactList.getList().forEach { item ->
            if (item.nameContact.contains(query!!, ignoreCase = true) || item.numberContact.contains(query, ignoreCase = true)) {
                filteredlist.add(item)
            }
        }

        if(query?.isEmpty()!!) noSearch(noContactsWarning, iconNoContactWarning, recyclerView)
        else if(filteredlist.isEmpty() && !query?.isEmpty()!!) noResultOnSearch(noContactsWarning, recyclerView)
        else {
            recyclerViewAdapter.filterList(filteredlist.sortedBy { it.nameContact.lowercase() }.toCollection(ArrayList()))
            resultOnSearch(noContactsWarning, recyclerView)
        }
    }
}