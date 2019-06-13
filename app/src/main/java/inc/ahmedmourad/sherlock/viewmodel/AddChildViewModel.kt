package inc.ahmedmourad.sherlock.viewmodel

import androidx.lifecycle.ViewModel
import dagger.Lazy
import inc.ahmedmourad.sherlock.dagger.SherlockComponent
import inc.ahmedmourad.sherlock.dagger.modules.app.factories.IntentServiceFactory
import inc.ahmedmourad.sherlock.domain.constants.Gender
import inc.ahmedmourad.sherlock.domain.constants.Hair
import inc.ahmedmourad.sherlock.domain.constants.Skin
import inc.ahmedmourad.sherlock.model.*
import inc.ahmedmourad.sherlock.viewmodel.model.DefaultLiveData

import splitties.init.appCtx
import java.util.*
import javax.inject.Inject

class AddChildViewModel : ViewModel() {

    @Inject
    lateinit var intentServiceFactory: Lazy<IntentServiceFactory>

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

    init {
        SherlockComponent.ViewModels.addChildComponent.get().inject(this)
    }

    fun publish(child: AppPictureChild) {
        appCtx.startService(intentServiceFactory.get().create(child))
    }

    fun toAppPictureChild() = AppPictureChild(
            UUID.randomUUID().toString(),
            System.currentTimeMillis(),
            AppName(firstName.value.trim(), lastName.value.trim()),
            notes.value.trim(),
            location.value,
            AppAppearance(
                    gender.value,
                    skin.value,
                    hair.value,
                    AppRange(startAge.value, endAge.value),
                    AppRange(startHeight.value, endHeight.value)
            ), picturePath.value
    )

    override fun onCleared() {
        SherlockComponent.ViewModels.addChildComponent.release()
        super.onCleared()
    }
}
