package inc.ahmedmourad.sherlock.viewmodel.controllers

import androidx.lifecycle.ViewModel
import arrow.core.getOrElse
import arrow.core.none
import inc.ahmedmourad.sherlock.domain.constants.Gender
import inc.ahmedmourad.sherlock.domain.constants.Hair
import inc.ahmedmourad.sherlock.domain.constants.Skin
import inc.ahmedmourad.sherlock.domain.interactors.ObserveInternetConnectivityInteractor
import inc.ahmedmourad.sherlock.model.AppChildCriteriaRules
import inc.ahmedmourad.sherlock.model.AppExactAppearance
import inc.ahmedmourad.sherlock.model.AppLocation
import inc.ahmedmourad.sherlock.model.AppName
import inc.ahmedmourad.sherlock.viewmodel.model.DefaultLiveData
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers

internal class FindChildrenViewModel(
        observeInternetConnectivityInteractor: ObserveInternetConnectivityInteractor
) : ViewModel() {

    val firstName by lazy { DefaultLiveData("") }
    val lastName by lazy { DefaultLiveData("") }

    val skin by lazy { DefaultLiveData(Skin.WHITE) }
    val hair by lazy { DefaultLiveData(Hair.BLONDE) }

    val gender by lazy { DefaultLiveData(Gender.MALE) }

    val location by lazy { DefaultLiveData(none<AppLocation>()) }

    val age by lazy { DefaultLiveData(15) }
    val height by lazy { DefaultLiveData(120) }

    val internetConnectivityObserver: Flowable<Boolean> = observeInternetConnectivityInteractor()
            .retry()
            .observeOn(AndroidSchedulers.mainThread())

    fun toAppChildCriteriaRules() = AppChildCriteriaRules(
            AppName(firstName.value.trim(), lastName.value.trim()),
            location.value.getOrElse { AppLocation.invalid() },
            AppExactAppearance(
                    gender.value,
                    skin.value,
                    hair.value,
                    age.value,
                    height.value
            )
    )
}
