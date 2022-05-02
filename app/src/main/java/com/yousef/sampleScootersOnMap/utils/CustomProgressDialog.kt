package com.yousef.sampleScootersOnMap.utils

import android.app.ProgressDialog
import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.widget.ImageView
import android.widget.TextView
import com.yousef.sampleScootersOnMap.R
import java.util.*
import javax.inject.Inject

/**
 * The type Custom progress dialog.
 */
class CustomProgressDialog @Inject constructor(context: Context) : ProgressDialog(context, R.style.CustomProgressDialog) {
    private val imgSpinner: ImageView
    private val txtMessage: TextView
    override fun show() {
        super.show()
        val anim = RotateAnimation(0.0f, 360.0f, Animation.RELATIVE_TO_SELF, .5f, Animation.RELATIVE_TO_SELF, .5f)
        anim.interpolator = LinearInterpolator()
        anim.repeatCount = Animation.INFINITE
        anim.duration = 1500
        imgSpinner.animation = anim
        imgSpinner.startAnimation(anim)
    }

    /**
     * Show.
     *
     * @param message the message
     */
    private fun show(message: CharSequence) {
        show()
        if (message.toString().isEmpty()) txtMessage.visibility = View.GONE
        txtMessage.text = message
    }

    /**
     * Show.
     *
     * @param messageId the message id
     */
    fun show(messageId: Int) {
        show(context.getString(messageId))
    }

    /**
     * Sets message.
     *
     * @param message the message
     */
    fun setMessage(message: String?) {
        if (this.isShowing) {
            txtMessage.visibility = View.VISIBLE
            txtMessage.text = message
        }
    }

    /**
     * Instantiates a new Custom progress dialog.
     *
     * @param context the context
     */
    init {
        val wlmp = Objects.requireNonNull(window)?.attributes
        wlmp!!.gravity = Gravity.CENTER_HORIZONTAL
        window!!.attributes = wlmp
        setTitle(null)
        setCancelable(false)
        setOnCancelListener(null)
        setContentView(R.layout.progress_spinner)
        imgSpinner = findViewById(R.id.imgSpinner)
        txtMessage = findViewById(R.id.txtMessage)
    }
}