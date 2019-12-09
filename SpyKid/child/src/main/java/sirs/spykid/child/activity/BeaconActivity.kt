package sirs.spykid.child.activity

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import sirs.spykid.util.Location
import sirs.spykid.util.updateChildLocation
import java.time.LocalDateTime
import java.util.*
import java.util.function.Consumer

@RequiresApi(Build.VERSION_CODES.O)
class BeaconActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startLocationBroadcaster()
    }

    private fun startLocationBroadcaster() {
        // Calling an async task inside another async task is a deadlock, so we use a thread
        Thread {
            try {
                while (true) {
                    updateChildLocation(getRandomLocation(), Consumer { r ->
                        r.match(
                            Consumer { ok ->  },
                            Consumer { error ->
                                Toast.makeText(this, "Error sending location", Toast.LENGTH_SHORT).show()
                            }
                        )
                    })
                    Thread.sleep(3000)
                }
            } catch (e: InterruptedException) {
                Toast.makeText(this, "Location loop stopped", Toast.LENGTH_SHORT).show()
                Log.d("ERROR", "Location loop stopped")
            }
        }.start()
    }

    private fun getRandomLocation(): Location {
        val random = Random()
        return Location(random.nextDouble(), random.nextDouble(), LocalDateTime.now())

    }

}
