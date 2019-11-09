package inc.ahmedmourad.sherlock.utils.pickers.images

import android.app.Activity
import android.content.Intent
import com.esafirm.imagepicker.features.ReturnMode
import inc.ahmedmourad.sherlock.R
import splitties.init.appCtx
import com.esafirm.imagepicker.features.ImagePicker as DelegateImagePicker

private const val IMAGE_PICKER_REQUEST = 4287

internal class EsafirmImagePicker : ImagePicker {

    override fun start(activity: Activity, onError: OnError) {
        try {
            activity.startActivityForResult(DelegateImagePicker.create(activity)
                    .returnMode(ReturnMode.ALL)
                    .folderMode(true)
                    .toolbarFolderTitle(appCtx.getString(R.string.pick_a_folder))
                    .toolbarImageTitle(appCtx.getString(R.string.tap_to_select))
                    .single()
                    .limit(1)
                    .showCamera(true)
                    .imageDirectory(appCtx.getString(R.string.image_directory))
                    .theme(R.style.ImagePickerTheme)
                    .enableLog(true)
                    .getIntent(appCtx), IMAGE_PICKER_REQUEST
            )
        } catch (e: Exception) {
            onError(e)
        }
    }

    override fun handleActivityResult(requestCode: Int, data: Intent, onSuccess: OnSuccess, onError: OnError) {
        if (requestCode == IMAGE_PICKER_REQUEST) {
            DelegateImagePicker.getFirstImageOrNull(data)?.path?.let {
                onSuccess(it)
            } ?: onError(IllegalStateException("No image is selected!"))
        }
    }
}
