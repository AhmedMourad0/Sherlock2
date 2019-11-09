package inc.ahmedmourad.sherlock.viewmodel.activity

import androidx.lifecycle.ViewModel
import inc.ahmedmourad.sherlock.domain.interactors.ObserveInternetConnectivityInteractor
import inc.ahmedmourad.sherlock.model.Connectivity
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers

internal class MainActivityViewModel(
        observeInternetConnectivityInteractor: ObserveInternetConnectivityInteractor
) : ViewModel() {

    val internetConnectivityObserver: Flowable<Connectivity>

    init {
        internetConnectivityObserver = observeInternetConnectivityInteractor()
                .map(this::getConnectivity)
                .retry()
                .observeOn(AndroidSchedulers.mainThread())
    }

    private fun getConnectivity(isConnected: Boolean): Connectivity {
        return if (isConnected) Connectivity.CONNECTED else Connectivity.DISCONNECTED
    }
}