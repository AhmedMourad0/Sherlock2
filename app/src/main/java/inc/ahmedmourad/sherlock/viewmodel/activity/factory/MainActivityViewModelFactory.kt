package inc.ahmedmourad.sherlock.viewmodel.activity.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import inc.ahmedmourad.sherlock.domain.interactors.auth.CheckIsUserSignedInInteractor
import inc.ahmedmourad.sherlock.domain.interactors.auth.FindSignedInUserInteractor
import inc.ahmedmourad.sherlock.domain.interactors.auth.SignOutInteractor
import inc.ahmedmourad.sherlock.domain.interactors.core.ObserveInternetConnectivityInteractor
import inc.ahmedmourad.sherlock.viewmodel.activity.MainActivityViewModel

internal class MainActivityViewModelFactory(
        private val observeInternetConnectivityInteractor: ObserveInternetConnectivityInteractor,
        private val checkIsUserSignedInInteractor: CheckIsUserSignedInInteractor,
        private val findSignedInUserInteractor: FindSignedInUserInteractor,
        private val signOutInteractor: SignOutInteractor
) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>) = MainActivityViewModel(
            observeInternetConnectivityInteractor,
            checkIsUserSignedInInteractor,
            findSignedInUserInteractor,
            signOutInteractor
    ) as T
}
