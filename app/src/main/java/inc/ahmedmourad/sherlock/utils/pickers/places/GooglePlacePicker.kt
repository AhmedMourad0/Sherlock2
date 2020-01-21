package inc.ahmedmourad.sherlock.utils.pickers.places

import android.app.Activity
import android.content.Intent
import arrow.core.toOption
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import inc.ahmedmourad.sherlock.model.children.AppCoordinates
import inc.ahmedmourad.sherlock.model.children.AppLocation
import splitties.init.appCtx
import com.google.android.gms.location.places.ui.PlacePicker as DelegatePlacePicker

internal class GooglePlacePicker : PlacePicker {

    private val requestCode = (0..Int.MAX_VALUE).random()

    override fun start(activity: Activity, onError: OnError) {

        try {
            activity.startActivityForResult(DelegatePlacePicker.IntentBuilder().build(activity), requestCode)
        } catch (e: GooglePlayServicesRepairableException) {
            onError(e)
        } catch (e: GooglePlayServicesNotAvailableException) {
            onError(e)
        }
    }

    override fun handleActivityResult(requestCode: Int, data: Intent, onSuccess: OnSuccess) {
        if (requestCode == this.requestCode) {
            onSuccess(
                    DelegatePlacePicker.getPlace(appCtx, data)?.run {
                        AppLocation(this.id,
                                this.name.toString(),
                                this.address.toString(),
                                AppCoordinates(this.latLng.latitude, this.latLng.longitude)
                        )
                    }.toOption()
            )
        }
    }
}
