package inc.ahmedmourad.sherlock.domain.interactors

import dagger.Lazy
import inc.ahmedmourad.sherlock.domain.platform.ConnectivityManager
import io.reactivex.Single

internal class CheckInternetConnectivityInteractor(private val connectivityManager: Lazy<ConnectivityManager>) : Interactor<Single<Boolean>> {
    override fun execute(): Single<Boolean> {
        return connectivityManager.get().isInternetConnected()
    }
}
