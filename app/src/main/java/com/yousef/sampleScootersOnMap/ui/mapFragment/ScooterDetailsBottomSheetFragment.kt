package com.yousef.sampleScootersOnMap.ui.mapFragment

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.graphics.ColorFilter
import android.graphics.LightingColorFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.yousef.sampleScootersOnMap.R
import com.yousef.sampleScootersOnMap.data.model.ScootersPOJO
import com.yousef.sampleScootersOnMap.ui.mainActivity.MainActivity
import org.junit.rules.Timeout.seconds
import java.util.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ScooterDetailsBottomSheetFragment  : BottomSheetDialogFragment() , LifecycleObserver {

    companion object {
        const val TAG = "CustomBottomSheetDialogFragment"
    }

    var tvBatteryLevel: TextView? = null
    var tvMaxSpeed: TextView? = null
    var ivRedMarker: ImageView? = null
    private val argItem = "recording_item"
    private var item: ScootersPOJO.Scooter? = null

    fun newInstance(item: ScootersPOJO.Scooter): ScooterDetailsBottomSheetFragment? {
        val fragment = ScooterDetailsBottomSheetFragment()
        val bundle = Bundle()
        bundle.putParcelable(argItem, item)
        fragment.arguments = bundle
        return fragment
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        item = requireArguments().getParcelable(argItem)
        activity?.lifecycle?.removeObserver(this)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity?.lifecycle?.addObserver(this)
    }

    @SuppressLint("LongLogTag")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(
            R.layout.fragment_scooter_details_bottom_sheet,
            container,
            false
        )
        return inflater.inflate(R.layout.fragment_scooter_details_bottom_sheet, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tvBatteryLevel = requireView().findViewById(R.id.tvBatteryLevel) as TextView
        tvMaxSpeed = requireView().findViewById(R.id.tvMaxSpeed) as TextView
        ivRedMarker = requireView().findViewById(R.id.ivRedMarker) as ImageView

        tvBatteryLevel!!.text = item!!.attributes!!.batteryLevel.toString() + " %"
        tvMaxSpeed!!.text = item!!.attributes!!.maxSpeed.toString()
        ivRedMarker!!.visibility = if(item!!.attributes!!.hasHelmetBox!!) View.INVISIBLE else View.VISIBLE
    }
}
