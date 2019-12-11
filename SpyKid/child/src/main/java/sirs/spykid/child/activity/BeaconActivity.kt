package sirs.spykid.child.activity

import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import sirs.spykid.child.R
import sirs.spykid.util.EncryptionAlgorithm
import sirs.spykid.util.Location
import sirs.spykid.util.updateChildLocation
import java.time.Instant
import java.time.LocalDateTime
import java.util.*
import java.util.concurrent.locks.ReentrantReadWriteLock
import java.util.function.Consumer
import kotlin.concurrent.read
import kotlin.concurrent.write

@RequiresApi(Build.VERSION_CODES.O)
class BeaconActivity : AppCompatActivity() {

    private val lastLocationLock = ReentrantReadWriteLock()
    private var lastLocation: Location? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_beacon)
        findViewById<Button>(R.id.button_sos).setOnClickListener {
            lastLocationLock.read {
                lastLocation?.let {
                    updateChildLocation(it.copy(sos = true), Consumer { })
                }
            }
        }
        findViewById<Button>(R.id.beacon_delete_key).setOnClickListener {
            EncryptionAlgorithm.deleteKey(EncryptionAlgorithm.KeyStores.SharedSecret)
            finish()
        }

        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        val locationRequest = LocationRequest.create()?.apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }!!

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                for (location in locationResult.locations.sortedBy { it.time }) {
                    val l = Location(
                        location.latitude, location.longitude, LocalDateTime.ofInstant(
                            Instant.ofEpochMilli(location.time),
                            TimeZone.getDefault().toZoneId()
                        )
                    )
                    updateChildLocation(l, Consumer { Log.d("INFO", "Location '$l' sent") })
                    lastLocationLock.write {
                        lastLocation = l
                    }
                }
            }
        }
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }
}
