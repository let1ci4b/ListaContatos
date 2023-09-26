package com.example.recyclerlistacontatos.main

import android.R
import android.app.Activity
import android.view.WindowManager

class Utill {
    //this will toggle or action bar color
    fun toggleStatusBarColor(activity: Activity, color: Int) {
        val window = activity.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.statusBarColor = activity.resources.getColor(R.color.holo_green_dark)
    }
}