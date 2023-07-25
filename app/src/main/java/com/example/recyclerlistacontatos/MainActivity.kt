package com.example.recyclerlistacontatos

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.Window
import android.view.WindowManager
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.recyclerlistacontatos.databinding.MainLayoutBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: MainLayoutBinding
    private lateinit var newRecyclerView: RecyclerView
    private lateinit var newArrayList : ArrayList<Contacts>
    lateinit var imageId : Array<Int>
    lateinit var heading : Array<String>
    lateinit var contactNumber: Array<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN)
        binding = MainLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonAddContact.setOnClickListener{
            val intent = Intent(this@MainActivity, addContactActivity::class.java)
            startActivity(intent)
        }

        imageId = arrayOf(
            R.drawable.dog1,
            R.drawable.dog2,
            R.drawable.dog3
        )

        heading = arrayOf(
            "contato 1",
            "contato 2",
            "contato 3"
        )

        contactNumber = arrayOf(
            "(48) 99686-2072",
            "(48) 99670-2009",
            "(48) 98872-1076"
        )

        newRecyclerView = binding.recyclerView
        newRecyclerView.layoutManager = LinearLayoutManager(this)
        newRecyclerView.setHasFixedSize(true)

        newArrayList = arrayListOf<Contacts>()
        getUserdata()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)

        //val manager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchItem = menu?.findItem(R.id.actionSearch)
        val searchView = searchItem?.actionView as SearchView
        //searchView.setSearchableInfo(manager.getSearchableInfo(componentName))
        searchView.queryHint = "Pesquisar"

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
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
        })
        return true
    }

    private fun getUserdata() {
        for(i in imageId.indices){
           val contact = Contacts(imageId[i], heading[i], contactNumber[i])
            newArrayList.add(contact)
        }

        newRecyclerView.adapter = Adapter(newArrayList)
    }
}