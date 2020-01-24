package inc.ahmedmourad.sherlock.viewmodel.controllers.children

import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import arrow.core.Tuple2
import arrow.core.toT
import inc.ahmedmourad.sherlock.dagger.modules.factories.SherlockServiceIntentFactory
import inc.ahmedmourad.sherlock.domain.constants.Gender
import inc.ahmedmourad.sherlock.domain.constants.Hair
import inc.ahmedmourad.sherlock.domain.constants.PublishingState
import inc.ahmedmourad.sherlock.domain.constants.Skin
import inc.ahmedmourad.sherlock.domain.interactors.core.CheckChildPublishingStateInteractor
import inc.ahmedmourad.sherlock.domain.interactors.core.CheckInternetConnectivityInteractor
import inc.ahmedmourad.sherlock.domain.interactors.core.ObserveChildPublishingStateInteractor
import inc.ahmedmourad.sherlock.domain.interactors.core.ObserveInternetConnectivityInteractor
import inc.ahmedmourad.sherlock.model.children.*
import inc.ahmedmourad.sherlock.viewmodel.model.DefaultLiveData
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import splitties.init.appCtx

internal class AddChildViewModel(
        private val serviceFactory: SherlockServiceIntentFactory,
        observeInternetConnectivityInteractor: ObserveInternetConnectivityInteractor,
        checkInternetConnectivityInteractor: CheckInternetConnectivityInteractor,
        observeChildPublishingStateInteractor: ObserveChildPublishingStateInteractor,
        checkChildPublishingStateInteractor: CheckChildPublishingStateInteractor
) : ViewModel() {

    val firstName by lazy { DefaultLiveData("") }
    val lastName by lazy { DefaultLiveData("") }

    val skin by lazy { DefaultLiveData(Skin.WHITE) }
    val hair by lazy { DefaultLiveData(Hair.BLONDE) }

    val gender by lazy { DefaultLiveData(Gender.MALE) }

    val location by lazy { MutableLiveData<AppLocation>() }

    val startAge by lazy { DefaultLiveData(0) }
    val endAge by lazy { DefaultLiveData(30) }
    val startHeight by lazy { DefaultLiveData(40) }
    val endHeight by lazy { DefaultLiveData(200) }

    val notes by lazy { DefaultLiveData("") }

    val picturePath by lazy { DefaultLiveData("") }

    val internetConnectivityFlowable: Flowable<Tuple2<Boolean, PublishingState?>> =
            observeInternetConnectivityInteractor()
                    .retry()
                    .observeOn(AndroidSchedulers.mainThread())
                    .flatMapSingle { isConnected ->
                        checkChildPublishingStateInteractor()
                                .observeOn(AndroidSchedulers.mainThread())
                                .map { isConnected toT it.orNull() }
                    }

    val internetConnectivitySingle: Single<Boolean> = checkInternetConnectivityInteractor()
            .observeOn(AndroidSchedulers.mainThread())

    val publishingStateFlowable: Flowable<PublishingState> = observeChildPublishingStateInteractor()
            .retry()
            .observeOn(AndroidSchedulers.mainThread())

    fun onPublish() {
        ContextCompat.startForegroundService(appCtx, serviceFactory(toAppPublishedChild()))
    }

    fun take(child: AppPublishedChild) {
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

    private fun toAppPublishedChild() = AppPublishedChild(
            name = AppName(firstName.value.trim(), lastName.value.trim()),
            notes = notes.value.trim(),
            location = location.value,
            appearance = AppEstimatedAppearance(
                    gender.value,
                    skin.value,
                    hair.value,
                    AppRange(startAge.value, endAge.value),
                    AppRange(startHeight.value, endHeight.value)
            ), picturePath = picturePath.value
    )
}
