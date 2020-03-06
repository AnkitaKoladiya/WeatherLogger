package com.weather.logger.utils

import android.R.string.cancel
import android.app.Service
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.location.*
import android.os.Bundle
import android.os.IBinder
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AlertDialog
import com.weather.logger.R
import java.io.IOException
import java.util.*


class GPSTracker(context: Context) : Service(), LocationListener {
    private val mContext: Context
    // flag for GPS Status
    var isGPSEnabled = false
    // flag for network status
    var isNetworkEnabled = false
    /**
     * GPSTracker isGPSTrackingEnabled getter.
     * Check GPS/wifi is enabled
     */
    // flag for GPS Tracking is enabled
    var isGPSTrackingEnabled = false
    var location: Location? = null
    private var latitude = 0.0
    private var longitude = 0.0
    // How many Geocoder should return our GPSTracker
    var geocoderMaxResults = 1
    // Declaring a Location Manager
    var locationManager: LocationManager? = null
    // Store LocationManager.GPS_PROVIDER or LocationManager.NETWORK_PROVIDER information
    private var provider_info: String? = null

    init {
        mContext = context
        getLocation()
    }

    /**
     * Try to get my current location by GPS or Network Provider
     */
    fun getLocation() {
        try {
            locationManager = mContext.getSystemService(LOCATION_SERVICE) as LocationManager
            //getting GPS status
            isGPSEnabled = locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)
            //getting network status
            isNetworkEnabled = locationManager!!.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
            // Try to get location if you GPS Service is enabled
            if (isGPSEnabled) {
                isGPSTrackingEnabled = true
                Log.d(TAG, "Application use GPS Service")
                /*
                 * This provider determines location using
                 * satellites. Depending on conditions, this provider may take a while to return
                 * a location fix.
                 */provider_info = LocationManager.GPS_PROVIDER
            } else if (isNetworkEnabled) { // Try to get location if you Network Service is enabled
                isGPSTrackingEnabled = true
                Log.d(
                    TAG,
                    "Application use Network State to get GPS coordinates"
                )
                /*
                 * This provider determines location based on
                 * availability of cell tower and WiFi access points. Results are retrieved
                 * by means of a network lookup.
                 */provider_info = LocationManager.NETWORK_PROVIDER
            }
            // Application can use GPS or Network Provider
            if (!provider_info!!.isEmpty()) {
                locationManager!!.requestLocationUpdates(
                    provider_info,
                    MIN_TIME_BW_UPDATES,
                    MIN_DISTANCE_CHANGE_FOR_UPDATES.toFloat(),
                    this
                )
                if (locationManager != null) {
                    location = locationManager!!.getLastKnownLocation(provider_info)
                    updateGPSCoordinates()
                }
            }
        } catch (e: Exception) { //e.printStackTrace();
            Log.e(TAG, "Impossible to connect to LocationManager", e)
        }
    }

    /**
     * Update GPSTracker latitude and longitude
     */
    fun updateGPSCoordinates() {
        if (location != null) {
            latitude = location!!.getLatitude()
            longitude = location!!.getLongitude()
        }
    }

    /**
     * GPSTracker latitude getter and setter
     * @return latitude
     */
    fun getLatitude(): Double {
        if (location != null) {
            latitude = location!!.getLatitude()
        }
        return latitude
    }

    /**
     * GPSTracker longitude getter and setter
     * @return
     */
    fun getLongitude(): Double {
        if (location != null) {
            longitude = location!!.getLongitude()
        }
        return longitude
    }

    fun getIsGPSTrackingEnabled(): Boolean {
        return isGPSTrackingEnabled
    }

    /**
     * Stop using GPS listener
     * Calling this method will stop using GPS in your app
     */
    fun stopUsingGPS() {
        if (locationManager != null) {
            locationManager!!.removeUpdates(this@GPSTracker)
        }
    }

    /**
     * Function to show settings alert dialog
     */
    fun showSettingsAlert() {
        val alertDialog: AlertDialog.Builder = AlertDialog.Builder(mContext)
        //Setting Dialog Title
        alertDialog.setTitle(R.string.GPSAlertDialogTitle)
        //Setting Dialog Message
        alertDialog.setMessage(R.string.GPSAlertDialogMessage)
        //On Pressing Setting button
        alertDialog.setPositiveButton(
            R.string.action_settings,
            DialogInterface.OnClickListener { dialog, which ->
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                mContext.startActivity(intent)
            })
        //On pressing cancel button
        alertDialog.setNegativeButton(cancel,
            DialogInterface.OnClickListener { dialog, which -> dialog.cancel() })
        alertDialog.show()
    }

    /**
     * Get list of address by latitude and longitude
     * @return null or List<Address>
    </Address> */
    fun getGeocoderAddress(context: Context?): List<Address>? {
        if (location != null) {
            val geocoder = Geocoder(context, Locale.ENGLISH)
            try {
                return geocoder.getFromLocation(latitude, longitude, geocoderMaxResults)
            } catch (e: IOException) { //e.printStackTrace();
                Log.e(TAG, "Impossible to connect to Geocoder", e)
            }
        }
        return null
    }

    /**
     * Try to get AddressLine
     * @return null or addressLine
     */
    fun getAddressLine(context: Context?): String? {
        val addresses: List<Address>? = getGeocoderAddress(context)
        return if (addresses != null && addresses.size > 0) {
            val address: Address = addresses[0]
            address.getAddressLine(0)
        } else {
            null
        }
    }

    /**
     * Try to get Locality
     * @return null or locality
     */
    fun getLocality(context: Context?): String? {
        val addresses: List<Address>? = getGeocoderAddress(context)
        return if (addresses != null && addresses.size > 0) {
            val address: Address = addresses[0]
            address.getLocality()
        } else {
            null
        }
    }

    /**
     * Try to get Postal Code
     * @return null or postalCode
     */
    fun getPostalCode(context: Context?): String? {
        val addresses: List<Address>? = getGeocoderAddress(context)
        return if (addresses != null && addresses.size > 0) {
            val address: Address = addresses[0]
            address.getPostalCode()
        } else {
            null
        }
    }

    /**
     * Try to get CountryName
     * @return null or postalCode
     */
    fun getCountryName(context: Context?): String? {
        val addresses: List<Address>? = getGeocoderAddress(context)
        return if (addresses != null && addresses.size > 0) {
            val address: Address = addresses[0]
            address.getCountryName()
        } else {
            null
        }
    }

    override fun onLocationChanged(location: Location?) {}
    override fun onStatusChanged(
        provider: String,
        status: Int,
        extras: Bundle
    ) {
    }

    override fun onProviderEnabled(provider: String) {}
    override fun onProviderDisabled(provider: String) {}
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    companion object {
        // Get Class Name
        private val TAG = GPSTracker::class.java.name
        // The minimum distance to change updates in meters
        private const val MIN_DISTANCE_CHANGE_FOR_UPDATES: Long = 10 // 10 meters
        // The minimum time between updates in milliseconds
        private const val MIN_TIME_BW_UPDATES = 1000 * 60 * 1 // 1 minute
            .toLong()
    }
}