
package com.example.monitoreosilos.util

import android.content.Context
import android.location.Geocoder
import android.os.Build
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.util.Locale
import kotlin.coroutines.resume

object CityHelper {

    suspend fun getCityName(
        context: Context,
        latitude: Double,
        longitude: Double
    ): String? {

        if (!Geocoder.isPresent()) {
            return null
        }

        val geocoder = Geocoder(
            context.applicationContext,
            Locale("es", "CL")
        )

        return if (Build.VERSION.SDK_INT >= 33) {

            suspendCancellableCoroutine { continuation ->

                geocoder.getFromLocation(
                    latitude,
                    longitude,
                    1,
                    object : Geocoder.GeocodeListener {

                        override fun onGeocode(
                            addresses: MutableList<android.location.Address>
                        ) {
                            val address = addresses.firstOrNull()

                            val city = address?.locality
                                ?: address?.subAdminArea
                                ?: address?.adminArea

                            if (continuation.isActive) {
                                continuation.resume(city)
                            }
                        }

                        override fun onError(errorMessage: String?) {
                            if (continuation.isActive) {
                                continuation.resume(null)
                            }
                        }
                    }
                )
            }

        } else {

            withContext(Dispatchers.IO) {
                try {
                    @Suppress("DEPRECATION")
                    val addresses = geocoder.getFromLocation(
                        latitude,
                        longitude,
                        1
                    )

                    val address = addresses?.firstOrNull()

                    address?.locality
                        ?: address?.subAdminArea
                        ?: address?.adminArea

                } catch (e: Exception) {
                    null
                }
            }
        }
    }
}
