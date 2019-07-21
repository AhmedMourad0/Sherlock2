package inc.ahmedmourad.sherlock.domain.framework

interface LocationManager {
    fun distanceBetween(startLatitude: Double, startLongitude: Double, endLatitude: Double, endLongitude: Double): Long
}
