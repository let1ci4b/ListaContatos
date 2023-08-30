package com.example.recyclerlistacontatos.main

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.recyclerlistacontatos.addcontacts.AddContactActivity
import com.example.recyclerlistacontatos.R
import com.example.recyclerlistacontatos.databinding.MainLayoutBinding
import com.example.recyclerlistacontatos.models.ContactList

class MainActivity : AppCompatActivity(), RecyclerViewAdapter.OnItemClickListener {
    private lateinit var binding: MainLayoutBinding
    private lateinit var recyclerViewAdapter: RecyclerViewAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MainLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /// todo implement actionBar with binding
        /// todo fix toolbar themes
        setSupportActionBar(findViewById(R.id.mainToolbar))
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
        val simpleCallback = object :
            ItemTouchHelper.SimpleCallback(
                0,
                ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
            ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = false

            /// todo add background on swipe
            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {

                // If you want to add a background, a text, an icon
                //  as the user swipes, this is where to start decorating
                //  I will link you to a library I created for that below

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
                        Toast.makeText(this@MainActivity, "swipe left $position", Toast.LENGTH_LONG).show()
                    }
                    ItemTouchHelper.RIGHT -> {
                        Toast.makeText(this@MainActivity, "swipe right $position", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
        ItemTouchHelper(simpleCallback).attachToRecyclerView(binding.recyclerView)
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
        menuInflater.inflate(R.menu.main_menu, menu)

        //val manager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchItem = menu?.findItem(R.id.actionSearch)
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
}