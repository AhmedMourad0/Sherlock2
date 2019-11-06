package inc.ahmedmourad.sherlock.domain.interactors

import dagger.Lazy
import inc.ahmedmourad.sherlock.domain.platform.ConnectivityManager
import io.reactivex.Flowable

internal class ObserveInternetConnectivityInteractor(private val connectivityManager: Lazy<ConnectivityManager>) : Interactor<Flowable<Boolean>> {
    override fun execute(): Flowable<Boolean> {
        return connectivityManager.get().observeInternetConnectivity()
    }
}
