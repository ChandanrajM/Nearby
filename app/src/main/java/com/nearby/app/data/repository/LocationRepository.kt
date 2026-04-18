package com.nearby.app.data.repository

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Build
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

data class LocationData(
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val address: String = "Detecting location...",
    val city: String = "",
    val area: String = "",
)

@Singleton
class LocationRepository @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val fusedClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    private val _location = MutableStateFlow(LocationData())
    val location: StateFlow<LocationData> = _location.asStateFlow()

    fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    suspend fun fetchCurrentLocation() {
        if (!hasLocationPermission()) {
            _location.value = LocationData(address = "Location permission required")
            return
        }
        try {
            val loc = suspendCancellableCoroutine { cont ->
                val cts = CancellationTokenSource()
                fusedClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cts.token)
                    .addOnSuccessListener { location ->
                        cont.resume(location)
                    }
                    .addOnFailureListener {
                        cont.resume(null)
                    }
                cont.invokeOnCancellation { cts.cancel() }
            }
            if (loc != null) {
                val geocoder = Geocoder(context, Locale.getDefault())
                val addressLine: String
                val city: String
                val area: String
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    val addresses = suspendCancellableCoroutine { cont ->
                        geocoder.getFromLocation(loc.latitude, loc.longitude, 1) { list ->
                            cont.resume(list)
                        }
                    }
                    val addr = addresses.firstOrNull()
                    addressLine = addr?.getAddressLine(0) ?: "Unknown"
                    city = addr?.locality ?: addr?.subAdminArea ?: ""
                    area = addr?.subLocality ?: addr?.thoroughfare ?: ""
                } else {
                    @Suppress("DEPRECATION")
                    val addresses = geocoder.getFromLocation(loc.latitude, loc.longitude, 1)
                    val addr = addresses?.firstOrNull()
                    addressLine = addr?.getAddressLine(0) ?: "Unknown"
                    city = addr?.locality ?: addr?.subAdminArea ?: ""
                    area = addr?.subLocality ?: addr?.thoroughfare ?: ""
                }
                _location.value = LocationData(
                    latitude = loc.latitude,
                    longitude = loc.longitude,
                    address = addressLine,
                    city = city,
                    area = area,
                )
            } else {
                _location.value = LocationData(address = "Could not detect location")
            }
        } catch (e: SecurityException) {
            _location.value = LocationData(address = "Location permission required")
        } catch (e: Exception) {
            _location.value = LocationData(address = "Location unavailable")
        }
    }
}
