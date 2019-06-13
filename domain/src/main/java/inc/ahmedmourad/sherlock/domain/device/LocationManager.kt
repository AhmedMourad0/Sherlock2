package inc.ahmedmourad.sherlock.domain.device

interface LocationManager {
    fun distanceBetween(startLatitude: Double, startLongitude: Double, endLatitude: Double, endLongitude: Double): Long
}
