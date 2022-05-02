package com.yousef.sampleScootersOnMap.ui.mapFragment

import android.Manifest
import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.maps.android.clustering.ClusterManager
import com.yousef.sampleScootersOnMap.BR
import com.yousef.sampleScootersOnMap.R
import com.yousef.sampleScootersOnMap.data.model.ScootersPOJO
import com.yousef.sampleScootersOnMap.databinding.FragmentMapBinding
import com.yousef.sampleScootersOnMap.ui.base.BaseFragment
import com.yousef.sampleScootersOnMap.utils.ConnectivityReceiver
import com.yousef.sampleScootersOnMap.utils.Const.DEFAULT_ZOOM
import com.yousef.sampleScootersOnMap.utils.Const.ERROR_DIALOG_REQUEST
import com.yousef.sampleScootersOnMap.utils.Const.LOCATION_PERMISSION_REQUEST_CODE
import com.yousef.sampleScootersOnMap.utils.Const.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
import com.yousef.sampleScootersOnMap.utils.LocationProviderChangedReceiver
import com.yousef.sampleScootersOnMap.utils.MarkerClusterRenderer
import com.yousef.sampleScootersOnMap.utils.MyToast
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MapFragment : BaseFragment<FragmentMapBinding, MapViewModel>(), MapNavigator,
    OnMapReadyCallback, GoogleMap.OnCameraIdleListener,
    ConnectivityReceiver.ConnectivityReceiverListener,
    LocationProviderChangedReceiver.LocationReceiverListener {

    private var locationPermissionGranted: Boolean = false
    private lateinit var map: GoogleMap
    private var isFirstLoading = true
    private var currentLatLng: LatLng? = null
    private var nearestIsDisplayed: Boolean = false
    private var allScooters: List<ScootersPOJO.Scooter>? = null
    private var mSnackBar: Snackbar? = null

    private val fusedLocationClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(requireActivity().applicationContext)
    }

    private var cancellationTokenSource = CancellationTokenSource()

    private val callback = OnMapReadyCallback { googleMap ->
        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        val sydney = LatLng(-34.0, 151.0)
        googleMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }

    private var mFragmentMapBinding: FragmentMapBinding? = null

    override val bindingVariable: Int
        get() = BR.viewModel

    override val layoutId: Int
        get() = R.layout.fragment_map

    override fun getViewModelClass() = MapViewModel::class.java

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.setNavigator(this)
        requireActivity().registerReceiver(ConnectivityReceiver(),
            IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        )
        val br: BroadcastReceiver = LocationProviderChangedReceiver()
        val filter = IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION)
        requireContext().registerReceiver(br, filter)
    }

    private fun requestDeviceLocationSettings() {
        val locationRequest = LocationRequest.create().apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)

        val client: SettingsClient = LocationServices.getSettingsClient(requireActivity())
        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())

        task.addOnSuccessListener { locationSettingsResponse ->
            val state = locationSettingsResponse.locationSettingsStates
        }

        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    exception.startResolutionForResult(
                        requireActivity(),
                        100
                    )
                } catch (sendEx: IntentSender.SendIntentException) {
                    // Ignore the error.
                }
            }
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mFragmentMapBinding = viewDataBinding
        initMap()
    }

    private fun clusterItemClickListener(marker: ScootersPOJO.Scooter?): Boolean{
        marker.let {
            val locationData = it
            try {
                val scooterDetailsBottomSheetFragment: ScooterDetailsBottomSheetFragment? =
                    ScooterDetailsBottomSheetFragment().newInstance(locationData!!)
                val transaction: FragmentTransaction = (requireActivity() as FragmentActivity)
                    .supportFragmentManager
                    .beginTransaction()
                scooterDetailsBottomSheetFragment!!.show(transaction, "dialog_scooter_bottom_sheet")
                moveCamera(LatLng(marker!!.attributes!!.latitude!!,
                    marker.attributes!!.longitude!!), DEFAULT_ZOOM, "")
            } catch (e: Exception) {
                Log.e(TAG, "exception", e)
            }
        }
        return true
    }

    private fun setUpClusterManager(googleMap: GoogleMap, scooters: List<ScootersPOJO.Scooter>?) {
        val clusterManager: ClusterManager<ScootersPOJO.Scooter?> = ClusterManager<ScootersPOJO.Scooter?>(requireContext(), googleMap)
        clusterManager.renderer = MarkerClusterRenderer(requireContext(), googleMap, clusterManager)
        googleMap.setOnCameraIdleListener(clusterManager)
        googleMap.setOnMarkerClickListener(clusterManager)
        clusterManager.setOnClusterItemClickListener{markerItem ->  clusterItemClickListener(markerItem) }
        val items: List<ScootersPOJO.Scooter?> = getItems(scooters)
        clusterManager.addItems(items)
        clusterManager.cluster()
    }

    private fun getItems(scooters: List<ScootersPOJO.Scooter>?): List<ScootersPOJO.Scooter?> {
        val items: MutableList<ScootersPOJO.Scooter> = mutableListOf()
        for (i in scooters!!.indices) {
            val offsetItem = ScootersPOJO.Scooter("", LatLng(scooters[i].attributes!!.latitude!!,
                scooters[i].attributes!!.longitude!!), scooters[i].attributes!!)
            items.add(offsetItem)
        }
        return items
    }
    override fun setMarkers(scooters: List<ScootersPOJO.Scooter>?) {
        allScooters = scooters
        if (scooters!!.isNotEmpty()) {
            map.clear()
            setUpClusterManager(map, scooters)
            if(currentLatLng != null && !nearestIsDisplayed)
                nearestScooterDetails(currentLatLng!!, allScooters!!)
        } else if (scooters.isNullOrEmpty()) {
            Log.d(TAG, "An empty or null list was returned.")
        }
        isFirstLoading = false
    }

    private fun nearestScooterDetails(myLocation: LatLng, locations: List<ScootersPOJO.Scooter>){
        var smallestDistance = -1f
        var closestLocation:ScootersPOJO.Scooter = locations[0]
        val results = FloatArray(5)
        for (location in locations) {
            Location.distanceBetween(
                myLocation.latitude,
                myLocation.longitude,
                location.attributes!!.latitude!!,
                location.attributes!!.longitude!!,
                results)
            if (smallestDistance == -1f || results[0] < smallestDistance) {
                closestLocation = location
                smallestDistance = results[0]
            }
        }
        nearestIsDisplayed = true
        clusterItemClickListener(closestLocation)
    }

    override fun onCameraIdle() {
        if(!isFirstLoading) {
            val currentScreen: LatLngBounds = map.projection.visibleRegion.latLngBounds
//            If you want to find scooters dynamically, this function should be used,
//            but in this sample project we have two static parameters.
//            viewModel.searchByBounds()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        viewModel.findScooters()
        if (isPermissionGranted()) {
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            map.isMyLocationEnabled = true
            getDeviceLocation()
            setListeners()
            map.apply {
                uiSettings.isMyLocationButtonEnabled = true
            }
        }
    }

    /**
     *  Gets the SupportMapFragment and request notification when map is ready to be used
     */
    private fun initMap() {
        Log.d(TAG, "initMap: called")
        val mapFragment: SupportMapFragment? = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)

    }

    /**
     *  Gets the current device location
     */
    private fun getDeviceLocation() {
        Log.d(TAG, "getDeviceLocation: getting current location")
        try {
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
                val currentLocationTask: Task<Location> = fusedLocationClient.getCurrentLocation(
                    PRIORITY_HIGH_ACCURACY,
                    cancellationTokenSource.token
                )

                currentLocationTask.addOnCompleteListener { task: Task<Location> ->
                    val result = if (task.isSuccessful) {
                        val result: Location = task.result
                        currentLatLng = LatLng(result.latitude, result.longitude)
                        if(allScooters != null && !nearestIsDisplayed)
                            nearestScooterDetails(currentLatLng!!, allScooters!!)
                        moveCamera(currentLatLng!!, DEFAULT_ZOOM, getString(R.string.my_location))
                        "Location (success): ${result.latitude}, ${result.longitude}"
                    } else {
                        val exception = task.exception
                        "Location (failure): $exception"
                    }
                    Log.d(TAG, "getCurrentLocation() result: $result")
                }
            }
        } catch (e: SecurityException) {
            Log.e(TAG, "getDeviceLocation: SecurityException: ${e.message!!}")
        }
    }

    /**
     *  Moves the camera to the current location
     */
    private fun moveCamera(latLng: LatLng, zoom: Float, title: String) {
        Log.d(TAG, "moveCamera: moving camera to: lat: ${latLng.latitude} , long: ${latLng.longitude}"
        )
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom))
    }

    private fun checkMap(): Boolean {
        if (isServicesOK()) {
            if (isGpsEnabled()) {
                return true
            }
        }
        return false
    }

    private fun showNetworkMessage(isConnected: Boolean) {
        if (!isConnected) {
            mSnackBar = view?.let { Snackbar.make(it, R.string.internetError, Snackbar.LENGTH_LONG) }
            mSnackBar?.duration = Snackbar.LENGTH_INDEFINITE
            mSnackBar?.show()
        } else {
            mSnackBar?.dismiss()
            viewModel.findScooters()
        }
    }

    private fun showLocationMessage(isEnabled: Boolean) {
        if (!isEnabled) {
            mSnackBar = view?.let { Snackbar.make(it, R.string.locationError, Snackbar.LENGTH_LONG) }
            mSnackBar?.duration = Snackbar.LENGTH_INDEFINITE
            mSnackBar?.show()
        } else {
            mSnackBar?.dismiss()
            viewModel.findScooters()
        }
    }

    override fun onResume() {
        super.onResume()
        ConnectivityReceiver.connectivityReceiverListener = this
        LocationProviderChangedReceiver.locationReceiverListener = this
        if(isFirstLoading)
            if (checkMap()) {
                if (!locationPermissionGranted) {
                    getLocationPermission()
                }
            }
    }

    /**
     * Callback will be called when there is change
     */
    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        nearestIsDisplayed = false
        showNetworkMessage(isConnected)
    }

    override fun onLocationStatusChanged(isEnabled: Boolean) {
        nearestIsDisplayed = false
        showLocationMessage(isEnabled)
    }

    private fun setListeners() {
        map.setOnCameraIdleListener(this)
    }

    // PERMISSIONS
    /**
     *  Makes sure that google services is installed on the device
     */
    private fun isServicesOK(): Boolean {

        Log.d(TAG, "isServicesOK: checking google services version")

        val available: Int =
            GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(requireContext())

        when {
            available == ConnectionResult.SUCCESS -> {
                Log.d(TAG, "isServicesOK: Google Play Services is working")
                return true
            }
            GoogleApiAvailability.getInstance().isUserResolvableError(available) -> {
                Log.d(TAG, "isServicesOK: an error occurred")

                val dialog: Dialog? = GoogleApiAvailability.getInstance()
                    .getErrorDialog(requireActivity(), available, ERROR_DIALOG_REQUEST)
                dialog!!.show()
            }
            else -> {
                MyToast.show(activity, "You can't make map requests",false)
            }
        }
        return false
    }

    /**
     *  Location permission for getting the location of the device.
     */
    private fun getLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext().applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            == PackageManager.PERMISSION_GRANTED
        ) {
            locationPermissionGranted = true
            initMap()
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
            )
        }
    }

    /**
     *  Returns true if permission is granted
     */
    private fun isPermissionGranted(): Boolean {
        Log.d(TAG, "isPermissionGranted: called")
        return locationPermissionGranted
    }

    /**
     *  Returns true is GPS is enabled.
     */
    private fun isGpsEnabled(): Boolean {
        Log.d(TAG, "checking gps")
        val manager = requireActivity().applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            requestDeviceLocationSettings()

            Log.d(TAG, "gps is not enabled")
            return false
        }
        Log.d(TAG, "gps is enabled")
        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        Log.d(TAG, "onRequestPermissionsResult: called")
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults.contains(PackageManager.PERMISSION_GRANTED)) {
                initMap()
            }
        }
    }

    companion object {
        private const val TAG = "MapFragment"
        fun newInstance(): MapFragment {
            val args = Bundle()
            val fragment = MapFragment()
            fragment.arguments = args
            return fragment
        }
    }
}

