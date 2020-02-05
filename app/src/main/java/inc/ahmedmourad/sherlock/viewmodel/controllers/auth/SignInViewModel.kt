package inc.ahmedmourad.sherlock.viewmodel.controllers.auth

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import arrow.core.Either
import inc.ahmedmourad.sherlock.domain.interactors.auth.SignInInteractor
import inc.ahmedmourad.sherlock.domain.interactors.auth.SignInWithFacebookInteractor
import inc.ahmedmourad.sherlock.domain.interactors.auth.SignInWithGoogleInteractor
import inc.ahmedmourad.sherlock.domain.interactors.auth.SignInWithTwitterInteractor
import inc.ahmedmourad.sherlock.domain.model.auth.DomainIncompleteUser
import inc.ahmedmourad.sherlock.domain.model.auth.DomainSignedInUser
import inc.ahmedmourad.sherlock.mapper.toAppIncompleteUser
import inc.ahmedmourad.sherlock.mapper.toAppSignedInUser
import inc.ahmedmourad.sherlock.model.auth.AppIncompleteUser
import inc.ahmedmourad.sherlock.model.auth.AppSignedInUser
import io.reactivex.android.schedulers.AndroidSchedulers

internal class SignInViewModel(
        private val signInInteractor: SignInInteractor,
        private val signInWithGoogleInteractor: SignInWithGoogleInteractor,
        private val signInWithFacebookInteractor: SignInWithFacebookInteractor,
        private val signInWithTwitterInteractor: SignInWithTwitterInteractor
) : ViewModel() {

    val email by lazy { MutableLiveData("") }
    val password by lazy { MutableLiveData("") }

    fun onSignIn() = signInInteractor(email.value!!.trim(), password.value!!.trim())
            .map(::toAppUserEither)
            .observeOn(AndroidSchedulers.mainThread())

    fun onSignInWithGoogle() = signInWithGoogleInteractor()
            .map(::toAppUserEither)
            .observeOn(AndroidSchedulers.mainThread())

    fun onSignInWithFacebook() = signInWithFacebookInteractor()
            .map(::toAppUserEither)
            .observeOn(AndroidSchedulers.mainThread())

    fun onSignInWithTwitter() = signInWithTwitterInteractor()
            .map(::toAppUserEither)
            .observeOn(AndroidSchedulers.mainThread())

    private fun toAppUserEither(
            resultEither: Either<Throwable, Either<DomainIncompleteUser, DomainSignedInUser>>
    ): Either<Throwable, Either<AppIncompleteUser, AppSignedInUser>> {
        return resultEither.map { either ->
            either.bimap(
                    leftOperation = DomainIncompleteUser::toAppIncompleteUser,
                    rightOperation = DomainSignedInUser::toAppSignedInUser
            )
        }
    }
}
