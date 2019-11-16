package inc.ahmedmourad.sherlock.utils.pickers.places

import android.app.Activity
import android.content.Intent
import arrow.core.toOption
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import inc.ahmedmourad.sherlock.model.AppCoordinates
import inc.ahmedmourad.sherlock.model.AppLocation
import splitties.init.appCtx
import com.google.android.gms.location.places.ui.PlacePicker as DelegatePlacePicker

private const val PLACE_PICKER_REQUEST = 7424

@Suppress("TYPEALIAS_EXPANSION_DEPRECATION")
internal class GooglePlacePicker : PlacePicker {

    override fun start(activity: Activity, onError: OnError) {

        try {
            activity.startActivityForResult(DelegatePlacePicker.IntentBuilder().build(activity), PLACE_PICKER_REQUEST)
        } catch (e: GooglePlayServicesRepairableException) {
            onError(e)
        } catch (e: GooglePlayServicesNotAvailableException) {
            onError(e)
        }
    }

    override fun handleActivityResult(requestCode: Int, data: Intent, onSuccess: OnSuccess) {
        if (requestCode == PLACE_PICKER_REQUEST) {
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
