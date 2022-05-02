package com.yousef.sampleScootersOnMap.utils

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

class ConnectivityReceiver : BroadcastReceiver() {
    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    override fun onReceive(context: Context, arg1: Intent) {
        if (connectivityReceiverListener != null) {
            connectivityReceiverListener!!.onNetworkConnectionChanged(isConnectedOrConnecting(context))
        }
    }
    interface ConnectivityReceiverListener {
        fun onNetworkConnectionChanged(isConnected: Boolean)
    }
    private fun isConnectedOrConnecting(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNet = connectivityManager.activeNetwork ?: return false
        val netCap = connectivityManager.getNetworkCapabilities(activeNet) ?: return false
        return when {
            netCap.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            netCap.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            else -> false
        }
    }
    companion object {
        var connectivityReceiverListener: ConnectivityReceiverListener? = null
    }
}