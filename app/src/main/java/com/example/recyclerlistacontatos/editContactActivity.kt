package com.example.recyclerlistacontatos

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.recyclerlistacontatos.databinding.EditContactBinding

class editContactActivity : AppCompatActivity() {
    private lateinit var binding: EditContactBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = EditContactBinding.inflate(layoutInflater)
        supportActionBar?.hide()
        setContentView(binding.root)

        binding.buttonCancel.setOnClickListener{
            finish()
        }
    }
}