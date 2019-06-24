package inc.ahmedmourad.sherlock

import androidx.test.ext.junit.runners.AndroidJUnit4
import inc.ahmedmourad.sherlock.device.AndroidTextManager
import inc.ahmedmourad.sherlock.device.R
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

import org.junit.runner.RunWith
import splitties.init.appCtx
import timber.log.Timber

@RunWith(AndroidJUnit4::class)
class AndroidTextManagerInstrumentedTests {

    private lateinit var manager: AndroidTextManager

    @Before
    fun setupTimber() {
        Timber.plant(Timber.DebugTree())
    }

    @Before
    fun setupManager() {
        manager = AndroidTextManager()
    }

    @Test
    fun allMethods_shouldReturnTheSameStringAsTheSystemCall() {

        mapOf(appCtx.getString(R.string.publishing) to manager.publishing(),
                appCtx.getString(R.string.something_went_wrong) to manager.somethingWentWrong(),
                appCtx.getString(R.string.published_successfully) to manager.publishedSuccessfully()
        ).forEach { (systemValue, managerValue) ->
            Timber.e("$systemValue  -  $managerValue")
            assertEquals(systemValue, managerValue)
        }
    }
}
