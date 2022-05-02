package com.yousef.sampleScootersOnMap.utils

import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import androidx.appcompat.app.AlertDialog
import com.yousef.sampleScootersOnMap.R
import java.util.*

object CommonUtils {
    @JvmStatic
    fun showLoadingDialog(context: Context?): ProgressDialog {
        val progressDialog = ProgressDialog(context)
        progressDialog.show()
        if (progressDialog.window != null) {
            progressDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val wlmp = Objects.requireNonNull(progressDialog.window)?.attributes
            if (wlmp != null) {
                wlmp.gravity = Gravity.CENTER_HORIZONTAL
            }
            progressDialog.window!!.attributes = wlmp
        }
        progressDialog.setContentView(R.layout.progress_spinner)
        progressDialog.isIndeterminate = true
        progressDialog.setCancelable(false)
        progressDialog.setCanceledOnTouchOutside(false)
        return progressDialog
    }

}