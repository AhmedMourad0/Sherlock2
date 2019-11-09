package inc.ahmedmourad.sherlock.utils.pickers.images

import android.app.Activity
import android.content.Intent

internal typealias OnSuccess = (String) -> Unit
internal typealias OnError = (Throwable) -> Unit

internal interface ImagePicker {

    fun start(activity: Activity, onError: OnError = { })

    fun handleActivityResult(requestCode: Int, data: Intent, onSuccess: OnSuccess, onError: OnError)
}
