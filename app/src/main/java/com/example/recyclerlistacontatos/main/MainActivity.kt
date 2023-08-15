package com.example.recyclerlistacontatos.main

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.view.Window
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.recyclerlistacontatos.addcontacts.AddContactActivity
import com.example.recyclerlistacontatos.R
import com.example.recyclerlistacontatos.databinding.MainLayoutBinding
import com.example.recyclerlistacontatos.models.ContactList

class MainActivity : AppCompatActivity() {
    private lateinit var binding: MainLayoutBinding
    private lateinit var newRecyclerView: RecyclerView
    private lateinit var recyclerViewAdapter: RecyclerViewAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MainLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Todo Change programmatically toolbar
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setIcon(R.drawable.ic_main)

        setupRecyclerView()
        setupListeners()
        showNoContactsWarning()
    }

    private fun setupRecyclerView() {
        newRecyclerView = binding.recyclerView
        newRecyclerView.layoutManager = LinearLayoutManager(this)
        recyclerViewAdapter = RecyclerViewAdapter(ContactList.getList())
        newRecyclerView.adapter = recyclerViewAdapter
        newRecyclerView.setHasFixedSize(true)
    }

    private fun showNoContactsWarning() {
        with(binding){
            if (ContactList.listSize() == 0) {
                iconNoContactWarning.visibility = View.VISIBLE
                noContactsWarning.visibility = View.VISIBLE
            } else {
                iconNoContactWarning.visibility = View.GONE
                noContactsWarning.visibility = View.GONE
            }
        }
    }

    override fun onResume() {
        super.onResume()
        showNoContactsWarning()
        /// Todo Add submit list to reload recycler
//        recyclerViewAdapter.notifyDataSetChanged()
    }

    private fun setupListeners() {
        with(binding) {
            buttonAddContact.setOnClickListener {
                val intent = Intent(this@MainActivity, AddContactActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun printTextOnScreen(warning: String){
        Toast.makeText(this@MainActivity, warning, Toast.LENGTH_SHORT).show()
    }

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
}