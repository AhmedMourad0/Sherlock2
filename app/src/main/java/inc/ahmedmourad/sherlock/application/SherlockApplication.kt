package inc.ahmedmourad.sherlock.application

import androidx.multidex.MultiDexApplication
import timber.log.Timber

@Suppress("unused")
internal class SherlockApplication : MultiDexApplication() {
    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }
}

/*
*
* Work on the ui (the 3 retry options too, the (AddChildController, DisplayChildController, SearchResultsController) progress, no items in lists, place picker, maybe separate it into separate object as well), coordinator layout for the connection indicator
*
* Use Timber to its full potential (Crashlytics)
*
* SavedStateViewModel and DataBinding
*
* * * Store picture url, username and any other possible data ONLY at FirebaseAuth
*
* Dagger encapsulation with qualifiers
*
* * Package per feature
*
* map firebase exceptions to domain ones and get rid of IsUserSignedIn
*
* DDD
*
*  / ** Done ** /
*
* BigQuery
*
* ConstraintLayout
*
* Kotlin Flows and Coroutines
*
* Kotlin Multiplatform
*
* */
