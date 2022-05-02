package com.yousef.sampleScootersOnMap.utils

import android.annotation.SuppressLint
import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.yousef.sampleScootersOnMap.R


object MyToast {
    @SuppressLint("InflateParams")
    fun show(context: Context?, text: String?, isLong: Boolean) {
        val inflater = LayoutInflater.from(context)
        val layout: View = inflater.inflate(R.layout.custom_toast, null)
        val textV = layout.findViewById(R.id.toast_text) as TextView
        textV.text = text
        val toast = Toast(context)
        toast.setGravity(Gravity.CENTER, 0, 0)
        toast.duration = if (isLong) Toast.LENGTH_LONG else Toast.LENGTH_SHORT
        toast.view = layout
        toast.show()
    }
}