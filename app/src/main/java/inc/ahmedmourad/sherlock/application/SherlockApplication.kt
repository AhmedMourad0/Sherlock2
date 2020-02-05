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
* Find a way to store phone number at FirebaseAuth
*
* Dagger encapsulation with qualifiers
*
* * Package per feature
*
* replace unnecessary folds with when
*
* search for and replace `criteria` and `rules`
*
* replace start/end for age/height with min/max
*
* replace exceptions with ADTs
*
* replace score with weight
*
* make domain models and their mirrors return Eithers instead of nullables
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
