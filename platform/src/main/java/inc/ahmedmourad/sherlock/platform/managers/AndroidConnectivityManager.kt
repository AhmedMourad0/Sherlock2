package inc.ahmedmourad.sherlock.platform.managers

import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork
import inc.ahmedmourad.sherlock.domain.exceptions.NoInternetConnectionException
import inc.ahmedmourad.sherlock.domain.model.Either
import inc.ahmedmourad.sherlock.domain.model.asEither
import inc.ahmedmourad.sherlock.domain.platform.ConnectivityManager
import io.reactivex.BackpressureStrategy
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import splitties.init.appCtx

internal class AndroidConnectivityManager : ConnectivityManager {

    override fun observeInternetConnectivity(): Flowable<Boolean> {
        return ReactiveNetwork.observeNetworkConnectivity(appCtx)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .flatMapSingle { ReactiveNetwork.checkInternetConnectivity() }
                .toFlowable(BackpressureStrategy.LATEST)
    }

    override fun isInternetConnected(): Single<Boolean> {
        return ReactiveNetwork.checkInternetConnectivity()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
    }

    internal class AndroidConnectivityEnforcer : ConnectivityManager.ConnectivityEnforcer {

        override fun requireInternetConnected(): Completable {
            return ReactiveNetwork.checkInternetConnectivity()
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.io())
                    .flatMapCompletable { isConnected ->
                        Completable.create { emitter ->
                            if (isConnected)
                                emitter.onComplete()
                            else
                                emitter.onError(NoInternetConnectionException())
                        }
                    }
        }

        override fun requireInternetConnectedEither(): Single<Either<Nothing?, Throwable>> {
            return ReactiveNetwork.checkInternetConnectivity()
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.io())
                    .map {
                        if (it)
                            Either.NULL
                        else
                            NoInternetConnectionException().asEither()
                    }
        }
    }
}
