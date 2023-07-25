package com.example.recyclerlistacontatos

import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.example.recyclerlistacontatos.databinding.EditContactBinding

class editContactActivity : AppCompatActivity() {
    private lateinit var binding: EditContactBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = EditContactBinding.inflate(layoutInflater)
        this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        supportActionBar?.hide()
        setContentView(binding.root)

        binding.buttonCancel.setOnClickListener{
            finish()
        }
    }
}