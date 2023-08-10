package com.example.recyclerlistacontatos

import android.app.Activity
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
import com.example.recyclerlistacontatos.databinding.MainLayoutBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: MainLayoutBinding
    private lateinit var newRecyclerView: RecyclerView
    val contactsList = arrayListOf<Contacts>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setIcon(R.drawable.ic_main)
        binding = MainLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        newRecyclerView = binding.recyclerView
        newRecyclerView.layoutManager = LinearLayoutManager(this)
        newRecyclerView.setHasFixedSize(true)
        onClickListeners()
        showNoContactsWarning()
    }

    private fun showNoContactsWarning() {
        if (contactsList.isEmpty()) {
            binding.iconNoContactWarning.visibility = View.VISIBLE
            binding.noContactsWarning.visibility = View.VISIBLE
        } else {
            binding.iconNoContactWarning.visibility = View.GONE
            binding.noContactsWarning.visibility = View.GONE
        }
    }

    override fun onResume() {
        super.onResume()
        showNoContactsWarning()
    }
    private fun onClickListeners() {
        with(binding) {
            buttonAddContact.setOnClickListener {
                val intent = Intent(this@MainActivity, AddContactActivity::class.java)
                intent.putExtra("lista", contactsList)
                startActivityForResult(intent, 1)
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {
            when {
                resultCode == Activity.RESULT_OK -> {
                    if (data != null) {
                        val contact: Contacts = data.getSerializableExtra("contact") as Contacts
                        contactsList.add(contact)
                        printTextOnScreen("${contact.nameContact} foi adicionado(a) com sucesso!")
                        newRecyclerView.adapter = Adapter(contactsList)
                        showNoContactsWarning()
                    }
                }

                resultCode == Activity.RESULT_CANCELED ->printTextOnScreen("Contato não salvo!")
            }
        }

        if (requestCode == 2) {
            when {
                resultCode == Activity.RESULT_OK -> {
                    if (data != null) {
                        val contact: Contacts = data.getSerializableExtra("updatedContact") as Contacts
                        val position = intent.extras?.getInt("contactPosition")

                        position?.let {
                            contactsList.set(it, contact)
                            printTextOnScreen("Alterações salvas!") } ?: printTextOnScreen("Erro ao editar contato.")

                        newRecyclerView.adapter = Adapter(contactsList)
                    }
                }

                resultCode == Activity.RESULT_CANCELED -> printTextOnScreen("Alterações descartadas.")
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