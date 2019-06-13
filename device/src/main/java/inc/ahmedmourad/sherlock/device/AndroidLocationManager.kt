package inc.ahmedmourad.sherlock.device

import android.location.Location
import inc.ahmedmourad.sherlock.domain.device.LocationManager

class AndroidLocationManager : LocationManager {
    override fun distanceBetween(startLatitude: Double, startLongitude: Double, endLatitude: Double, endLongitude: Double): Long {

        val distance = FloatArray(1)

        Location.distanceBetween(startLatitude,
                startLongitude,
                endLatitude,
                endLongitude,
                distance
        )

        return distance[0].toLong()
    }
}
