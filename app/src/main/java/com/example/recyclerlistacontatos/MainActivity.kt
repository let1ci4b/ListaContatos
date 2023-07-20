package com.example.recyclerlistacontatos

import android.os.Bundle
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MainLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

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

        newRecyclerView = binding.recyclerView
        newRecyclerView.layoutManager = LinearLayoutManager(this)
        newRecyclerView.setHasFixedSize(true)

        newArrayList = arrayListOf<Contacts>()
        getUserdata()
    }

    private fun getUserdata() {
        for(i in imageId.indices){
           val contact = Contacts(imageId[i], heading[i])
            newArrayList.add(contact)
        }

        newRecyclerView.adapter = Adapter(newArrayList)
    }
}