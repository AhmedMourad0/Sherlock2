package inc.ahmedmourad.sherlock.viewmodel.activity

import androidx.lifecycle.ViewModel
import inc.ahmedmourad.sherlock.domain.interactors.auth.CheckIsUserSignedInInteractor
import inc.ahmedmourad.sherlock.domain.interactors.auth.FindSignedInUserInteractor
import inc.ahmedmourad.sherlock.domain.interactors.auth.SignOutInteractor
import inc.ahmedmourad.sherlock.domain.interactors.core.ObserveInternetConnectivityInteractor
import inc.ahmedmourad.sherlock.domain.model.auth.DomainIncompleteUser
import inc.ahmedmourad.sherlock.domain.model.auth.DomainSignedInUser
import inc.ahmedmourad.sherlock.mapper.toAppIncompleteUser
import inc.ahmedmourad.sherlock.mapper.toAppSignedInUser
import inc.ahmedmourad.sherlock.model.core.Connectivity
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers

internal class MainActivityViewModel(
        observeInternetConnectivityInteractor: ObserveInternetConnectivityInteractor,
        checkIsUserSignedInInteractor: CheckIsUserSignedInInteractor,
        findSignedInUserInteractor: FindSignedInUserInteractor,
        signOutInteractor: SignOutInteractor
) : ViewModel() {

    val internetConnectivityFlowable: Flowable<Connectivity> = observeInternetConnectivityInteractor()
            .map(this::getConnectivity)
            .retry()
            .observeOn(AndroidSchedulers.mainThread())

    val isUserSignedInSingle = checkIsUserSignedInInteractor()
            .observeOn(AndroidSchedulers.mainThread())

    val findSignedInUserSingle = findSignedInUserInteractor()
            .map { resultEither ->
                resultEither.map {
                    it.bimap(DomainIncompleteUser::toAppIncompleteUser, DomainSignedInUser::toAppSignedInUser)
                }
            }.observeOn(AndroidSchedulers.mainThread())

    val signOutSingle = signOutInteractor()
            .observeOn(AndroidSchedulers.mainThread())

    private fun getConnectivity(isConnected: Boolean): Connectivity {
        return if (isConnected) Connectivity.CONNECTED else Connectivity.DISCONNECTED
    }
}
