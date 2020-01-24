package inc.ahmedmourad.sherlock.utils.pickers.places

import android.app.Activity
import android.content.Intent
import inc.ahmedmourad.sherlock.model.children.AppLocation

internal typealias OnError = (Throwable) -> Unit
internal typealias OnSuccess = (AppLocation?) -> Unit

internal interface PlacePicker {

    fun start(activity: Activity, onError: OnError = { })

    fun handleActivityResult(requestCode: Int, data: Intent, onSuccess: OnSuccess)
}
