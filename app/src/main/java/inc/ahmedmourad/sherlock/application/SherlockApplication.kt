package inc.ahmedmourad.sherlock.application

import androidx.multidex.MultiDexApplication
import timber.log.Timber

//TODO: name source roots kotlin instead of java
//TODO: fix gradle dependencies
@Suppress("unused")
internal class SherlockApplication : MultiDexApplication() {
    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }
}

/*
*
* * * * Write Firestore tests
*
* * * * Make intent service foreground to prevent from dying and to show progress to the user
*
* * * * Make pending intents lead to controllers using intent actions and interfaces implemented by companions
*
* * * * The error management thing (the snack bar and the bus)
*
* * * * Notifications on error for retrying (also missing messages), shows toasts on checks or requires failure
*
* * * * DisplayChildController must keep child's data up-to-date and should only appear after it's published (create the retry method)
*
* * * * Internet connection
*
* * * * Move tests and Dagger modules to proper locations
*
* * * * Fix all database tests (Dagger and VisibleForTesting and new functions)
*
* * * * Fix Access Modifiers (spread internal), generic invariants, inlines, replace conditionals with polymorphism
*
* * * * The Appearance class
*
* * * * the id creation thing
*
* * * * make all dagger modules objects with static methods,
*
* * * * FirebaseChildCriteriaRules,
*
* * * * pass entire child not only id when using find,
*
* * * * replace all mappers with functions and extension functions
*
* * * * maybe publication date should get same treatment as id
*
* * * * inner includes between modules (FirestoreRepository and Firestore for instance)
*
* * * * onPublish setCancelable/setDisposable to all create methods
*
* * * * use DomainSimpleRetrievedChild instead
*
* * * * replace all printStackTrace with Timber.e
*
* * * * Add user authentication system (look Delta app)
*
* Work on the ui (the 3 retry options too, the (AddChildController, DisplayChildController, SearchResultsController) progress, no items in lists, place picker, maybe separate it into separate object as well), coordinator layout for the connection indicator
*
* Use Timber to its full potential (Crashlytics)
*
* SavedStateViewModel
*
* DDD
*
* ServerTimeStamp
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
