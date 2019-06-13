package inc.ahmedmourad.sherlock.application

import androidx.multidex.MultiDexApplication
import timber.log.Timber

class SherlockApplication : MultiDexApplication() {
    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }
}
