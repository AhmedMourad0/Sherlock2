package inc.ahmedmourad.sherlock.application

import androidx.multidex.MultiDexApplication
import timber.log.Timber

//TODO: name source roots kotlin instead of java
class SherlockApplication : MultiDexApplication() {
    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }
}
