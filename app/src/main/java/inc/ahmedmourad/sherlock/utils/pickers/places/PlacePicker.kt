package inc.ahmedmourad.sherlock.utils.pickers.places

import android.app.Activity
import android.content.Intent
import arrow.core.Option
import inc.ahmedmourad.sherlock.model.children.AppLocation

internal typealias OnError = (Throwable) -> Unit
internal typealias OnSuccess = (Option<AppLocation>) -> Unit

internal interface PlacePicker {

    fun start(activity: Activity, onError: OnError = { })

    fun handleActivityResult(requestCode: Int, data: Intent, onSuccess: OnSuccess)
}
