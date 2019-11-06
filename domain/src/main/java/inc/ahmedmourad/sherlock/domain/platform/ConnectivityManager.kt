package inc.ahmedmourad.sherlock.domain.platform

import inc.ahmedmourad.sherlock.domain.model.Either
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

interface ConnectivityManager {

    fun observeInternetConnectivity(): Flowable<Boolean>

    fun isInternetConnected(): Single<Boolean>

    interface ConnectivityEnforcer {

        fun requireInternetConnected(): Completable

        fun requireInternetConnectedEither(): Single<Either<Nothing?, Throwable>>
    }
}
