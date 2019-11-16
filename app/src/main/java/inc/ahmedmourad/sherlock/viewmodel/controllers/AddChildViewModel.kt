package inc.ahmedmourad.sherlock.viewmodel.controllers

import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import arrow.core.*
import dagger.Lazy
import inc.ahmedmourad.sherlock.dagger.modules.factories.SherlockServiceIntentFactory
import inc.ahmedmourad.sherlock.domain.bus.Bus
import inc.ahmedmourad.sherlock.domain.constants.Gender
import inc.ahmedmourad.sherlock.domain.constants.Hair
import inc.ahmedmourad.sherlock.domain.constants.Skin
import inc.ahmedmourad.sherlock.domain.interactors.CheckInternetConnectivityInteractor
import inc.ahmedmourad.sherlock.domain.interactors.CheckPublishingStateInteractor
import inc.ahmedmourad.sherlock.domain.interactors.ObserveInternetConnectivityInteractor
import inc.ahmedmourad.sherlock.domain.interactors.ObservePublishingStateInteractor
import inc.ahmedmourad.sherlock.model.*
import inc.ahmedmourad.sherlock.viewmodel.model.DefaultLiveData
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers

import splitties.init.appCtx

internal class AddChildViewModel(
        private val serviceFactory: Lazy<SherlockServiceIntentFactory>,
        observeInternetConnectivityInteractor: ObserveInternetConnectivityInteractor,
        checkInternetConnectivityInteractor: CheckInternetConnectivityInteractor,
        observePublishingStateInteractor: ObservePublishingStateInteractor,
        checkPublishingStateInteractor: CheckPublishingStateInteractor
) : ViewModel() {

    val firstName by lazy { DefaultLiveData("") }
    val lastName by lazy { DefaultLiveData("") }

    val skin by lazy { DefaultLiveData(Skin.WHITE) }
    val hair by lazy { DefaultLiveData(Hair.BLONDE) }

    val gender by lazy { DefaultLiveData(Gender.MALE) }

    val location by lazy { DefaultLiveData(none<AppLocation>()) }

    val startAge by lazy { DefaultLiveData(0) }
    val endAge by lazy { DefaultLiveData(30) }
    val startHeight by lazy { DefaultLiveData(40) }
    val endHeight by lazy { DefaultLiveData(200) }

    val notes by lazy { DefaultLiveData("") }

    val picturePath by lazy { DefaultLiveData("") }

    val internetConnectivityObserver: Flowable<Tuple2<Boolean, Option<Bus.PublishingState<*>>>> =
            observeInternetConnectivityInteractor()
                    .retry()
                    .observeOn(AndroidSchedulers.mainThread())
                    .flatMapSingle { isConnected ->
                        checkPublishingStateInteractor()
                                .observeOn(AndroidSchedulers.mainThread())
                                .map { isConnected toT it }
                    }

    val internetConnectivitySingle: Single<Boolean> = checkInternetConnectivityInteractor()
            .observeOn(AndroidSchedulers.mainThread())

    val publishingStateObserver: Flowable<Bus.PublishingState<*>> = observePublishingStateInteractor()
            .retry()
            .observeOn(AndroidSchedulers.mainThread())

    fun onPublish() {
        ContextCompat.startForegroundService(appCtx, serviceFactory.get()(toAppPublishedChild()))
    }

    fun take(child: AppPublishedChild) {
        firstName.value = child.name.first
        lastName.value = child.name.last
        skin.value = child.appearance.skin
        hair.value = child.appearance.hair
        gender.value = child.appearance.gender
        location.value = child.location.toOption()
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
            location = location.value.getOrElse { AppLocation.invalid() },
            appearance = AppEstimatedAppearance(
                    gender.value,
                    skin.value,
                    hair.value,
                    AppRange(startAge.value, endAge.value),
                    AppRange(startHeight.value, endHeight.value)
            ), picturePath = picturePath.value
    )
}
