package com.yousef.sampleScootersOnMap.data.model

import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.google.maps.android.clustering.ClusterItem
import kotlinx.parcelize.Parcelize

@Parcelize
class ScootersPOJO : Parcelable {
    @JvmField
    @Expose
    @SerializedName("data")
    var data: List<Scooter>? = null

    @Parcelize
    class Scooter() : Parcelable, ClusterItem {
        @JvmField
        @Expose
        @SerializedName("type")
        var type: String? = null

        @JvmField
        @Expose
        @SerializedName("id")
        var id: String? = null

        @JvmField
        @Expose
        @SerializedName("attributes")
        var attributes: Attributes? = null

        private var title: String? = null
        private var position: LatLng? = null

        constructor(title: String = "",
                    latLng: LatLng = LatLng(0.0,0.0),
                    attributes: Attributes) : this() {
            this.title = title
            this.position = latLng
            this.attributes = attributes
        }

        override fun getPosition(): LatLng {
            return position!!
        }

        override fun getTitle(): String {
            return title!!
        }

        override fun getSnippet(): String {
            return ""
        }
    }

    @Parcelize
    class Attributes : Parcelable {
        @JvmField
        @Expose
        @SerializedName("batteryLevel")
        var batteryLevel: Int? = null

        @JvmField
        @Expose
        @SerializedName("lat")
        var latitude: Double? = null

        @JvmField
        @Expose
        @SerializedName("lng")
        var longitude: Double? = null

        @JvmField
        @Expose
        @SerializedName("maxSpeed")
        var maxSpeed: Int? = null

        @JvmField
        @Expose
        @SerializedName("vehicleType")
        var vehicleType: String? = null

        @JvmField
        @Expose
        @SerializedName("hasHelmetBox")
        var hasHelmetBox: Boolean? = null
    }

}