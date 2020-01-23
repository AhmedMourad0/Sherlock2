package inc.ahmedmourad.sherlock.viewmodel.controllers.auth

import androidx.lifecycle.ViewModel
import arrow.core.Either
import inc.ahmedmourad.sherlock.domain.interactors.auth.FindSignedInUserInteractor
import inc.ahmedmourad.sherlock.domain.model.auth.DomainIncompleteUser
import inc.ahmedmourad.sherlock.domain.model.auth.DomainSignedInUser
import inc.ahmedmourad.sherlock.mapper.toAppIncompleteUser
import inc.ahmedmourad.sherlock.mapper.toAppSignedInUser
import inc.ahmedmourad.sherlock.model.auth.AppIncompleteUser
import inc.ahmedmourad.sherlock.model.auth.AppSignedInUser
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers

internal class SignedInUserProfileViewModel(interactor: FindSignedInUserInteractor) : ViewModel() {

    val signedInUserSingle: Flowable<Either<Throwable, Either<AppIncompleteUser, AppSignedInUser>>>

    init {
        signedInUserSingle = interactor().map { resultEither ->
            resultEither.map {
                it.bimap(DomainIncompleteUser::toAppIncompleteUser, DomainSignedInUser::toAppSignedInUser)
            }
        }.observeOn(AndroidSchedulers.mainThread())
    }
}
