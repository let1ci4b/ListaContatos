package com.example.recyclerlistacontatos.main

import android.Manifest
import android.annotation.SuppressLint
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Canvas
import android.net.Uri
import android.os.Bundle
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
    lateinit var searchView: SearchView
    private var searchItem: MenuItem? = null
    private var selectAllItem: MenuItem? = null
    private var allSelectedItem: MenuItem? = null
    private var itemViewPosition: Int = 0
    var REQUEST_PHONE_CALL = 1
    var REQUEST_SEND_SMS = 2
    enum class SearchMode { NORESULT, RESULT}

    /// TODO implement app default text font
    /// TODO adjust cardview border on swipe
    /// TODO order list by alphabetic order
    /// TODO forbid user to give enters (name input text)
    /// TODO fix error when try to delete after realize a search
    /// TODO refactor xml layouts with styles
    /// TODO fix toolbar menu item that appears on delete mode after close the search bar

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
            if (ContactList.selectedItemsCount() == ContactList.listSize()) {
                selectAllItem?.isVisible = false
                allSelectedItem?.isVisible = true
            } else {
                selectAllItem?.isVisible = true
                allSelectedItem?.isVisible = false
            }
            bottomBar.visibility = View.VISIBLE
            bottomBarTitle.visibility = View.VISIBLE
            bottomBarTitle.text = ContactList.selectedItemsCount().toString().plus(getString(R.string.bottom_bar_text))
        }
    }

    private fun resetDefaultMode() {
        with(binding) {
            ContactList.isOnDeleteMode = false
            ContactList.clearCheckSelection()
            selectAllItem?.isVisible = false
            allSelectedItem?.isVisible = false
            bottomBar.visibility = View.GONE
            bottomBarTitle.visibility = View.GONE
            showNoContactsWarning()
        }
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
        isDeleteModeOn = false
    }

    private fun setupListeners() {
        with(binding) {
            buttonAddContact.setOnClickListener {
                val intent = Intent(this@MainActivity, AddContactActivity::class.java)
                startActivity(intent)
            }
            bottomBar.setOnMenuItemClickListener {
                when(it.itemId) {
                    R.id.deleteItem -> {
                        ContactList.removeContact()
                        isDeleteModeOn = false
                        recyclerViewAdapter.notifyDataSetChanged()
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
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        searchItem = menu?.findItem(R.id.actionSearch)
        selectAllItem = menu?.findItem(R.id.selectAll)
        allSelectedItem = menu?.findItem(R.id.allSelected)
        searchView = searchItem?.actionView as SearchView
        searchView.queryHint = getString(R.string.search_view_hint)

        val manager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
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
            if (item.nameContact.contains(query!!, ignoreCase = true) || item.numberContact.contains(query, ignoreCase = true)) {
                filteredlist.add(item)
            }
        }

        if(filteredlist.isEmpty() && !query?.isEmpty()!!) setupOnSearchModeBackground(SearchMode.NORESULT)
        else {
            recyclerViewAdapter.filterList(filteredlist)
            setupOnSearchModeBackground(SearchMode.RESULT)
        }
    }

    private fun setupOnSearchModeBackground(searchModeAction : SearchMode) {
        with(binding) {
            if(ContactList.listSize() == 0) showNoContactsWarning()
            else {
                when(searchModeAction) {
                    SearchMode.NORESULT -> {
                        noContactsWarning.visibility = View.VISIBLE
                        iconNoContactWarning.visibility = View.GONE
                        noContactsWarning.text = "Sem resultados."
                        recyclerView.visibility = View.GONE
                    }
                    SearchMode.RESULT -> {
                        iconNoContactWarning.visibility = View.GONE
                        noContactsWarning.visibility = View.GONE
                        recyclerView.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.selectAll -> {
                ContactList.selectAll()
                selectAllItem?.isVisible = false
                allSelectedItem?.isVisible = true
                isDeleteModeOn = true
            }
            R.id.allSelected -> {
                ContactList.clearCheckSelection()
                selectAllItem?.isVisible = true
                allSelectedItem?.isVisible = false
                isDeleteModeOn = true
            }
        }
        recyclerViewAdapter.notifyDataSetChanged()
        return super.onOptionsItemSelected(item)
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
}