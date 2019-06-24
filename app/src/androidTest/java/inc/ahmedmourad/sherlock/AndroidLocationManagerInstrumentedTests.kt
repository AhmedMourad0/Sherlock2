package inc.ahmedmourad.sherlock

import androidx.test.ext.junit.runners.AndroidJUnit4
import inc.ahmedmourad.sherlock.device.AndroidLocationManager
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

import org.junit.runner.RunWith
import timber.log.Timber

@RunWith(AndroidJUnit4::class)
class AndroidLocationManagerInstrumentedTests {

    private lateinit var manager: AndroidLocationManager

    @Before
    fun setupTimber() {
        Timber.plant(Timber.DebugTree())
    }

    @Before
    fun setupManager() {
        manager = AndroidLocationManager()
    }

    @Test
    fun distanceBetween_shouldReturnTheDistanceInMetersBetweenTheTwoCoordinates() {

        mapOf(18504L to ((30.7895331 to 30.9902482) to (30.8254819 to 30.8014204)),
                4076L to ((30.7895331 to 30.9902482) to (30.8261675 to 30.9938675)),
                18412L to ((30.8254819 to 30.8014204) to (30.8261675 to 30.9938675))
        ).map { (systemDistance, coordinates) ->
            systemDistance to manager.distanceBetween(coordinates.first.first,
                    coordinates.first.second,
                    coordinates.second.first,
                    coordinates.second.second
            )
        }.forEach { (systemDistance, managerDistance) ->
            Timber.e("$systemDistance  -  $managerDistance")
            assertEquals(systemDistance, managerDistance)
        }
    }
}
