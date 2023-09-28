package com.example.recyclerlistacontatos.main

import android.Manifest
import android.annotation.SuppressLint
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Canvas
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.recyclerlistacontatos.R
import com.example.recyclerlistacontatos.addcontacts.AddContactActivity
import com.example.recyclerlistacontatos.databinding.MainLayoutBinding
import com.example.recyclerlistacontatos.models.ContactList
import com.example.recyclerlistacontatos.models.Contacts
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator


@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity(), RecyclerViewAdapter.OnItemClick {
    private lateinit var binding: MainLayoutBinding
    private lateinit var recyclerViewAdapter: RecyclerViewAdapter
    private lateinit var searchView: SearchView
    private lateinit var actionMode: ActionMode
    private var searchItem: MenuItem? = null
    private var deleteItem: MenuItem? = null
    private var itemViewPosition: Int = 0
    var REQUEST_PHONE_CALL = 1
    var REQUEST_SEND_SMS = 2

    private var showDeleteActionToolBar : Boolean = false
        set(value) {
            if(value) changeToolBarToDeleteAction()
            else resetDefaultToolbar()
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

    private fun changeToolBarToDeleteAction() {
        searchView.visibility = View.GONE
        searchItem?.isVisible = false
        deleteItem?.isVisible = true
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.green_icons_background)))
    }

    private fun resetDefaultToolbar() {
        setSupportActionBar(binding.mainToolbar.mainToolbar)
        supportActionBar?.setBackgroundDrawable(resources.getDrawable(R.drawable.toolbar_background))
    }

    override fun onPause() {
        super.onPause()
        searchItem?.collapseActionView()
        searchView.isIconified = true
    }

    private fun setupRecyclerView() {
        recyclerViewAdapter = RecyclerViewAdapter(ContactList.getList(), binding.recyclerView, this)

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

            /// todo adjust cardview border on swipe
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
                    .addSwipeRightBackgroundColor(ContextCompat.getColor(this@MainActivity, com.example.recyclerlistacontatos.R.color.message_background))
                    .addSwipeRightActionIcon(com.example.recyclerlistacontatos.R.drawable.ic_message)
                    .addSwipeRightLabel(getString(R.string.message_label))
                    .setSwipeRightLabelColor(ContextCompat.getColor(this@MainActivity, com.example.recyclerlistacontatos.R.color.light_card_background))
                    .addCornerRadius(1,32)
                    .addSwipeLeftBackgroundColor(ContextCompat.getColor(this@MainActivity, com.example.recyclerlistacontatos.R.color.call_background))
                    .addSwipeLeftActionIcon(com.example.recyclerlistacontatos.R.drawable.ic_call)
                    .addSwipeLeftLabel(getString(R.string.call_label))
                    .setSwipeLeftLabelColor(ContextCompat.getColor(this@MainActivity, com.example.recyclerlistacontatos.R.color.light_card_background))
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
            else Toast.makeText(this@MainActivity, getString(R.string.permission_denied), Toast.LENGTH_LONG).show()
            2 -> if(grantResults[0]== PackageManager.PERMISSION_GRANTED) sendIntent(Intent.ACTION_SENDTO, "smsto: ${ContactList.getContact(itemViewPosition).numberContact}")
            else Toast.makeText(this@MainActivity, getString(R.string.permission_denied), Toast.LENGTH_LONG).show()
        }
    }

    /// TODO insert vector on no contacts warning
    private fun showNoContactsWarning() {
        with(binding) {
            if (ContactList.listSize() == 0) {
                iconNoContactWarning.visibility = View.VISIBLE
                noContactsWarning.visibility = View.VISIBLE
            } else {
                iconNoContactWarning.visibility = View.GONE
                noContactsWarning.visibility = View.GONE
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onResume() {
        super.onResume()
        showNoContactsWarning()
        recyclerViewAdapter.filterList(ContactList.getList())
        recyclerViewAdapter.notifyDataSetChanged()
        showDeleteActionToolBar = false
    }

    private fun setupListeners() {
        with(binding) {
            buttonAddContact.setOnClickListener {
                val intent = Intent(this@MainActivity, AddContactActivity::class.java)
                startActivity(intent)
            }
        }
    }

    // todo implement search bar using AutoCompletTextView
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)

        val manager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        searchItem = menu?.findItem(R.id.actionSearch)
        deleteItem = menu?.findItem(R.id.delteItem)
        searchView = searchItem?.actionView as SearchView

        searchView.queryHint = getString(R.string.search_view_hint)

        searchView.setSearchableInfo(manager.getSearchableInfo(componentName))

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchView.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filter(newText)
                return false
            }
        })

        return true
    }

    private fun filter(query: String?) {
        val filteredlist: ArrayList<Contacts> = ArrayList()

        for (item in ContactList.getList()) {
            if (item.nameContact.contains(query!!, ignoreCase = true) || item.numberContact.contains(query!!, ignoreCase = true)) {
                filteredlist.add(item)
            }
        }
        if (filteredlist.isEmpty()) {
            Toast.makeText(this, getString(R.string.no_result_on_search), Toast.LENGTH_SHORT).show()
        } else recyclerViewAdapter.filterList(filteredlist)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.delteItem) {
            ContactList.removeContact()
            recyclerViewAdapter.notifyDataSetChanged()
            ContactList.clearCheckSelection()
        }
        return super.onContextItemSelected(item)
    }

    override fun onItemClick(position: Int, action: Int) {
        itemViewPosition = position
        when(action) {
            1 -> checkPermissionAndSendIntent(Manifest.permission.CALL_PHONE, REQUEST_PHONE_CALL)
            2 -> checkPermissionAndSendIntent(Manifest.permission.SEND_SMS, REQUEST_SEND_SMS)
        }
    }

    /// todo add snackbar for undo remove option
    override fun onLongPress(view: View, contact: Contacts, position: Int) {
        //recyclerViewAdapter.toggleIcon(binding, position)
        showDeleteActionToolBar = true
        Toast.makeText(this, "clicked "+ contact.nameContact, Toast.LENGTH_SHORT).show()
    }
}