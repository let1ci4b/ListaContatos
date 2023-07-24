package com.example.recyclerlistacontatos

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.recyclerlistacontatos.databinding.AddContactBinding

class addContactActivity : AppCompatActivity() {
    private lateinit var binding: AddContactBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AddContactBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}