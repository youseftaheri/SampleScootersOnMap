package com.yousef.sampleScootersOnMap.utils

import android.annotation.SuppressLint
import android.content.Context
import android.view.ViewGroup
import android.widget.ImageView
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer
import com.google.maps.android.ui.IconGenerator
import com.yousef.sampleScootersOnMap.R
import com.yousef.sampleScootersOnMap.data.model.ScootersPOJO

class MarkerClusterRenderer @SuppressLint("UseCompatLoadingForDrawables") constructor(
    context: Context,
    map: GoogleMap?,
    clusterManager: ClusterManager<ScootersPOJO.Scooter?>?
) : DefaultClusterRenderer<ScootersPOJO.Scooter?>(context, map, clusterManager) {
    private val iconGenerator: IconGenerator = IconGenerator(context)
    private val markerImageView: ImageView = ImageView(context)
    override fun onBeforeClusterItemRendered(
        item: ScootersPOJO.Scooter,
        markerOptions: MarkerOptions
    ) {
        markerImageView.setImageResource(R.drawable.scooter_marker_icon)
        markerImageView.setBackgroundResource(R.drawable.rectangle_white)
        val icon = iconGenerator.makeIcon()
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon))
    }

    companion object {
        private const val MARKER_DIMENSION = 48
    }

    init {
        markerImageView.layoutParams = ViewGroup.LayoutParams(
            MARKER_DIMENSION,
            MARKER_DIMENSION
        )
        iconGenerator.setContentView(markerImageView)
    }
}