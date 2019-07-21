package inc.ahmedmourad.sherlock.viewmodel

import android.os.Build
import androidx.lifecycle.ViewModel
import dagger.Lazy
import inc.ahmedmourad.sherlock.dagger.modules.app.factories.SherlockIntentServiceAbstractFactory
import inc.ahmedmourad.sherlock.domain.constants.Gender
import inc.ahmedmourad.sherlock.domain.constants.Hair
import inc.ahmedmourad.sherlock.domain.constants.Skin
import inc.ahmedmourad.sherlock.model.*
import inc.ahmedmourad.sherlock.viewmodel.model.DefaultLiveData

import splitties.init.appCtx

class AddChildViewModel(private val intentServiceFactory: Lazy<SherlockIntentServiceAbstractFactory>) : ViewModel() {

    val firstName by lazy { DefaultLiveData("") }
    val lastName by lazy { DefaultLiveData("") }

    val skin by lazy { DefaultLiveData(Skin.WHITE) }
    val hair by lazy { DefaultLiveData(Hair.BLONDE) }

    val gender by lazy { DefaultLiveData(Gender.MALE) }

    val location by lazy { DefaultLiveData(AppLocation.empty()) }

    val startAge by lazy { DefaultLiveData(0) }
    val endAge by lazy { DefaultLiveData(30) }
    val startHeight by lazy { DefaultLiveData(40) }
    val endHeight by lazy { DefaultLiveData(200) }

    val notes by lazy { DefaultLiveData("") }

    val picturePath by lazy { DefaultLiveData("") }

    fun publish(child: AppPictureChild) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            appCtx.startForegroundService(intentServiceFactory.get().createIntent(child))
        else
            appCtx.startService(intentServiceFactory.get().createIntent(child))
    }

    fun mapFromAppPictureChild(child: AppPictureChild) {
        firstName.value = child.name.first
        lastName.value = child.name.last
        skin.value = child.appearance.skin
        hair.value = child.appearance.hair
        gender.value = child.appearance.gender
        location.value = child.location
        startAge.value = child.appearance.age.from
        endAge.value = child.appearance.age.to
        startHeight.value = child.appearance.height.from
        endHeight.value = child.appearance.height.to
        notes.value = child.notes
        picturePath.value = child.picturePath
    }

    fun toAppPictureChild() = AppPictureChild(
            publicationDate = System.currentTimeMillis(),
            name = AppName(firstName.value.trim(), lastName.value.trim()),
            notes = notes.value.trim(),
            location = location.value,
            appearance = AppAppearance(
                    gender.value,
                    skin.value,
                    hair.value,
                    AppRange(startAge.value, endAge.value),
                    AppRange(startHeight.value, endHeight.value)
            ), picturePath = picturePath.value
    )
}
