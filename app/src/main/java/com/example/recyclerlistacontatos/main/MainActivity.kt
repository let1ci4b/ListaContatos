package com.example.recyclerlistacontatos.main

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Canvas
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.recyclerlistacontatos.addcontacts.AddContactActivity
import com.example.recyclerlistacontatos.databinding.MainLayoutBinding
import com.example.recyclerlistacontatos.models.ContactList
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator


@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity(), RecyclerViewAdapter.OnItemClickListener {
    private lateinit var binding: MainLayoutBinding
    private lateinit var recyclerViewAdapter: RecyclerViewAdapter
    private var itemViewPosition: Int = 0
    var REQUEST_PHONE_CALL = 1
    var REQUEST_SEND_SMS = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MainLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /// todo implement actionBar with binding
        /// todo fix toolbar themes
        setSupportActionBar(findViewById(com.example.recyclerlistacontatos.R.id.mainToolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
//        supportActionBar?.setDisplayShowHomeEnabled(true)
//        supportActionBar?.setIcon(R.drawable.ic_main)

        setupRecyclerView()
        setupListeners()
        showNoContactsWarning()
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
                    .addSwipeRightLabel("Mensagem")
                    .setSwipeRightLabelColor(ContextCompat.getColor(this@MainActivity, com.example.recyclerlistacontatos.R.color.white))
                    .addCornerRadius(1,32)
                    .addSwipeLeftBackgroundColor(ContextCompat.getColor(this@MainActivity, com.example.recyclerlistacontatos.R.color.call_background))
                    .addSwipeLeftActionIcon(com.example.recyclerlistacontatos.R.drawable.ic_call)
                    .addSwipeLeftLabel("Chamar")
                    .setSwipeLeftLabelColor(ContextCompat.getColor(this@MainActivity, com.example.recyclerlistacontatos.R.color.white))
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
                        checkPhonePermissionAndMakeCall()
                    }
                    ItemTouchHelper.RIGHT -> {
                        itemViewPosition = position
                        checkPhonePermissionAndSendSMS()
                    }
                }
            }
        }
        ItemTouchHelper(simpleCallback).attachToRecyclerView(binding.recyclerView)
    }

    private fun sendSms() {
        val phoneIntent = Intent(Intent.ACTION_SENDTO)
        phoneIntent.data = Uri.parse("smsto: ${ContactList.getContact(itemViewPosition).numberContact}")
        startActivity(phoneIntent)
    }

    private fun checkPhonePermissionAndSendSMS() {
        if (ActivityCompat.checkSelfPermission(this@MainActivity, android.Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this@MainActivity, arrayOf(android.Manifest.permission.SEND_SMS), REQUEST_SEND_SMS)
        } else {
            sendSms()
        }
    }
    private fun makePhoneCall(){
            val phoneIntent = Intent(Intent.ACTION_CALL)
            phoneIntent.data = Uri.parse("tel: ${ContactList.getContact(itemViewPosition).numberContact}")
            startActivity(phoneIntent)
    }

    private fun checkPhonePermissionAndMakeCall() {
        if (ActivityCompat.checkSelfPermission(this@MainActivity, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this@MainActivity, arrayOf(android.Manifest.permission.CALL_PHONE), REQUEST_PHONE_CALL)
        } else {
            makePhoneCall()
        }
    }
    /// todo fix onRequestPermissionsResult code
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode) {
            REQUEST_PHONE_CALL -> if(grantResults[0]== PackageManager.PERMISSION_GRANTED) makePhoneCall() else Toast.makeText(this@MainActivity, "Permissão negada.", Toast.LENGTH_LONG).show()
            REQUEST_SEND_SMS -> if(grantResults[0]== PackageManager.PERMISSION_GRANTED) sendSms() else Toast.makeText(this@MainActivity, "Permissão negada.", Toast.LENGTH_LONG).show()
        }
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
        recyclerViewAdapter.notifyDataSetChanged()
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
        menuInflater.inflate(com.example.recyclerlistacontatos.R.menu.main_menu, menu)

        //val manager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchItem = menu?.findItem(com.example.recyclerlistacontatos.R.id.actionSearch)
        val searchView = searchItem?.actionView as SearchView
        //searchView.setSearchableInfo(manager.getSearchableInfo(componentName))
        searchView.queryHint = "Pesquisar"

        /*searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchView.clearFocus()
                searchView.setQuery("", false)
                searchItem.collapseActionView()
                Toast.makeText(this@MainActivity, "Looking for $query", Toast.LENGTH_LONG).show()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                Toast.makeText(this@MainActivity, "Looking for $newText", Toast.LENGTH_LONG).show()
                return false
            }
        })*/
        return true
    }

    override fun onClick(position: Int) {
        Toast.makeText(this, "onClick $position", Toast.LENGTH_LONG).show()
    }

    /// todo add snackbar for undo remove option
    override fun onLongClick(position: Int) {
        Toast.makeText(this, "onLongClick $position", Toast.LENGTH_LONG).show()
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        TODO("Not yet implemented")
    }
}