package com.example.recyclerlistacontatos

import android.app.Application
import android.content.Context

class ContactsApplication : Application() {

    init {
        instance = this
    }

    companion object {
        private lateinit var instance: ContactsApplication
        fun applicationContext(): Context {
            return instance.applicationContext
        }
    }
}