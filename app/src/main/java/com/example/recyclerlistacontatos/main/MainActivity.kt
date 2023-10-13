package com.example.recyclerlistacontatos.main

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Canvas
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.recyclerlistacontatos.R
import com.example.recyclerlistacontatos.addcontacts.AddContactActivity
import com.example.recyclerlistacontatos.databinding.MainLayoutBinding
import com.example.recyclerlistacontatos.contactsList.ContactList
import com.example.recyclerlistacontatos.models.ConfirmationDialog
import com.example.recyclerlistacontatos.models.Contacts
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator
import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil


@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity(), RecyclerViewAdapter.OnItemClick, ConfirmationDialog.ConfirmationDialogListener {
    private lateinit var binding: MainLayoutBinding
    private lateinit var recyclerViewAdapter: RecyclerViewAdapter
    private var itemViewPosition: Int = 0
    var REQUEST_PHONE_CALL = 1
    var REQUEST_SEND_SMS = 2
    enum class SearchMode { NOSEARCH, NORESULT, RESULT, ONSEARCH, ONEXIT }

    /// TODO replace project to MVVM architecture

    private var isDeleteModeOn : Boolean = false
        set(value) {
            if(value) setupDeleteMode()
            else resetDefaultMode()
            field = value
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MainLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.mainToolbar.mainToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        setupRecyclerView()
        setupListeners()
        showNoContactsWarning()
    }

    private fun setupDeleteMode() {
        with(binding) {
            selectionItems.visibility = View.VISIBLE
            bottomBar.visibility = View.VISIBLE
            bottomBarTitle.visibility = View.VISIBLE
            bottomBarTitle.text = ContactList.selectedItemsCount().toString().plus(getString(R.string.bottom_bar_text))
            if (ContactList.selectedItemsCount() == ContactList.listSize()) {
                selectAll.visibility = View.GONE
                allSelected.visibility = View.VISIBLE
            } else {
                selectAll.visibility = View.VISIBLE
                allSelected.visibility = View.GONE
            }
        }
    }

    private fun resetDefaultMode() {
        with(binding) {
            ContactList.isOnDeleteMode = false
            ContactList.clearCheckSelection()
            selectionItems.visibility = View.GONE
            bottomBar.visibility = View.GONE
            bottomBarTitle.visibility = View.GONE
            showNoContactsWarning()
        }
    }

    override fun onPause() {
        super.onPause()
        setupOnSearchModeBackground(SearchMode.ONEXIT)
    }

    private fun setupRecyclerView() {
        recyclerViewAdapter = RecyclerViewAdapter(ContactList.getList().sortedBy { it.nameContact }.toCollection(ArrayList<Contacts>()),
                                binding.recyclerView, this)

        binding.recyclerView.apply {
            setHasFixedSize(false)
            adapter = recyclerViewAdapter
            layoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.VERTICAL, false)
        }
        setupTouchOnSwiped()
    }

    private fun setupTouchOnSwiped() {
        val simpleCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = false

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {

                RecyclerViewSwipeDecorator.Builder(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
                    .addSwipeRightBackgroundColor(ContextCompat.getColor(this@MainActivity, R.color.message_background))
                    .addSwipeRightActionIcon(R.drawable.ic_message)
                    .addSwipeRightLabel(getString(R.string.message_label))
                    .setSwipeRightLabelColor(ContextCompat.getColor(this@MainActivity, R.color.white))
                    .addCornerRadius(1,32)
                    .addSwipeLeftBackgroundColor(ContextCompat.getColor(this@MainActivity, R.color.call_background))
                    .addSwipeLeftActionIcon(R.drawable.ic_call)
                    .addSwipeLeftLabel(getString(R.string.call_label))
                    .setSwipeLeftLabelColor(ContextCompat.getColor(this@MainActivity, R.color.white))
                    .create()
                    .decorate()

                super.onChildDraw(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                when (direction) {
                    ItemTouchHelper.LEFT -> {
                        itemViewPosition = position
                        checkPermissionAndSendIntent(Manifest.permission.CALL_PHONE, REQUEST_PHONE_CALL)
                    }
                    ItemTouchHelper.RIGHT -> {
                        itemViewPosition = position
                        checkPermissionAndSendIntent(Manifest.permission.SEND_SMS, REQUEST_SEND_SMS)
                    }
                }
            }
        }
        ItemTouchHelper(simpleCallback).attachToRecyclerView(binding.recyclerView)
    }

    private fun sendIntent(action: String, uriString: String){
        val phoneIntent = Intent(action)
        phoneIntent.data = Uri.parse(uriString)
        startActivity(phoneIntent)
    }

    private fun checkPermissionAndSendIntent(permission: String, requestCode: Int) {
        if (ActivityCompat.checkSelfPermission(this@MainActivity, permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this@MainActivity, arrayOf(permission), requestCode)
        } else {
            when(requestCode) {
                1 -> sendIntent(Intent.ACTION_CALL, "tel: ${ContactList.getContact(itemViewPosition).numberContact}")
                2 -> sendIntent(Intent.ACTION_SENDTO, "smsto: ${ContactList.getContact(itemViewPosition).numberContact}")
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode) {
            1 -> if(grantResults[0]== PackageManager.PERMISSION_GRANTED) sendIntent(Intent.ACTION_CALL, "tel: ${ContactList.getContact(itemViewPosition).numberContact}")
            else printPermissionDeniedTextOnScreen()
            2 -> if(grantResults[0]== PackageManager.PERMISSION_GRANTED) sendIntent(Intent.ACTION_SENDTO, "smsto: ${ContactList.getContact(itemViewPosition).numberContact}")
            else printPermissionDeniedTextOnScreen()
        }
    }

    private fun printPermissionDeniedTextOnScreen() {
        Toast.makeText(this@MainActivity, getString(R.string.permission_denied), Toast.LENGTH_SHORT).show()
    }

    private fun showNoContactsWarning() {
        with(binding) {
            if (ContactList.listSize() == 0) {
                iconNoContactWarning.visibility = View.VISIBLE
                noContactsWarning.visibility = View.VISIBLE
                noContactsWarning.text = resources.getString(R.string.no_contact_warning)
            } else {
                iconNoContactWarning.visibility = View.GONE
                noContactsWarning.visibility = View.GONE
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onResume() {
        super.onResume()
        recyclerViewAdapter.filterList(ContactList.getList().sortedBy { it.nameContact }.toCollection(ArrayList<Contacts>()))
        recyclerViewAdapter.notifyDataSetChanged()
        isDeleteModeOn = false
    }

    private fun setupListeners() {
        with(binding) {
            buttonAddContact.setOnClickListener {
                val intent = Intent(this@MainActivity, AddContactActivity::class.java)
                startActivity(intent)
            }
            iconCloseSearchView.setOnClickListener {
                setupOnSearchModeBackground(SearchMode.ONEXIT)
            }
            iconBackSearchView.setOnClickListener {
                if (searchViewQuery.text.isNullOrEmpty()) {
                    setupOnSearchModeBackground(SearchMode.ONEXIT)
                } else searchViewQuery.text = null
            }
            bottomBar.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.deleteItem -> {
                        if(ContactList.selectedItemsCount() > 0 ) showCancelDialog()
                        true
                    }
                    R.id.exitItem -> {
                        isDeleteModeOn = false
                        recyclerViewAdapter.notifyDataSetChanged()
                        true
                    }
                    else -> false
                }
            }
            searchViewQuery.doOnTextChanged { text, start, before, count ->
                filter(searchViewQuery.text.toString())
            }
            selectAll.setOnClickListener {
                ContactList.selectAll()
                selectAll.visibility = View.GONE
                allSelected.visibility = View.VISIBLE
                isDeleteModeOn = true
                recyclerViewAdapter.notifyDataSetChanged()
            }
            allSelected.setOnClickListener {
                ContactList.clearCheckSelection()
                selectAll.visibility = View.VISIBLE
                allSelected.visibility = View.GONE
                isDeleteModeOn = true
                recyclerViewAdapter.notifyDataSetChanged()
            }
            searchIcon.setOnClickListener {
                setupOnSearchModeBackground(SearchMode.ONSEARCH)
                setupOnSearchModeBackground(SearchMode.NOSEARCH)
                searchViewQuery.requestFocus()
                UIUtil.showKeyboard(this@MainActivity, searchViewQuery)
            }
        }
    }

    private fun deleteContact() {
        ContactList.removeContact()
        isDeleteModeOn = false
        recyclerViewAdapter.notifyDataSetChanged()
        setupOnSearchModeBackground(SearchMode.ONEXIT)
    }

    private fun filter(query: String?) {
        val filteredlist: ArrayList<Contacts> = ArrayList()

        for (item in ContactList.getList()) {
            if (item.nameContact.contains(query!!, ignoreCase = true) || item.numberContact.contains(query, ignoreCase = true)) {
                filteredlist.add(item)
            }
        }

        if(query?.isEmpty()!!) setupOnSearchModeBackground(SearchMode.NOSEARCH)
        else if(filteredlist.isEmpty() && !query?.isEmpty()!!) setupOnSearchModeBackground(SearchMode.NORESULT)
        else {
            recyclerViewAdapter.filterList(filteredlist.sortedBy { it.nameContact }.toCollection(ArrayList<Contacts>()))
            setupOnSearchModeBackground(SearchMode.RESULT)
        }
    }

    private fun setupOnSearchModeBackground(searchModeAction: SearchMode) {
        with(binding) {
            when (searchModeAction) {
                SearchMode.NORESULT -> {
                    noContactsWarning.visibility = View.VISIBLE
                    noContactsWarning.text = getString(R.string.no_result_on_search)
                    recyclerView.visibility = View.GONE
                }
                SearchMode.RESULT -> {
                    noContactsWarning.visibility = View.GONE
                    recyclerView.visibility = View.VISIBLE
                }
                SearchMode.NOSEARCH -> {
                    noContactsWarning.visibility = View.VISIBLE
                    iconNoContactWarning.visibility = View.GONE
                    noContactsWarning.text = getString(R.string.search_view_default_warning)
                    recyclerView.visibility = View.GONE
                }
                SearchMode.ONSEARCH -> {
                    searchBar.visibility = View.VISIBLE
                    searchIcon.visibility = View.GONE
                    title = null
                    searchViewQuery.text = null
                }
                SearchMode.ONEXIT -> {
                    title = getString(R.string.app_name)
                    searchIcon.visibility = View.VISIBLE
                    searchBar.visibility = View.GONE
                    noContactsWarning.visibility = View.GONE
                    recyclerView.visibility = View.VISIBLE
                    recyclerViewAdapter.filterList(ContactList.getList().sortedBy { it.nameContact }.toCollection(ArrayList<Contacts>()))
                    showNoContactsWarning()
                    UIUtil.hideKeyboard(this@MainActivity, searchViewQuery)
                }
            }
        }
    }

    override fun onItemClick(position: Int, action: Int) {
        itemViewPosition = position
        when(action) {
            1 -> checkPermissionAndSendIntent(Manifest.permission.CALL_PHONE, REQUEST_PHONE_CALL)
            2 -> checkPermissionAndSendIntent(Manifest.permission.SEND_SMS, REQUEST_SEND_SMS)
            3 -> isDeleteModeOn = true
        }
    }

    override fun onLongPress(view: View, contact: Contacts, position: Int) {
        isDeleteModeOn = true
    }

    private fun showCancelDialog() {
        val dialog = ConfirmationDialog.newInstance(R.string.remove_items_dialog_title, R.string.remove_items_dialog_message, R.string.button_delete, R.string.button_cancel)
        dialog.show(supportFragmentManager, ConfirmationDialog.TAG)
    }

    override fun onPositiveButtonClicked() {
        deleteContact()
    }

    override fun onNegativeButtonClicked() {}

    override fun onNeutralButtonClicked() {}
}

