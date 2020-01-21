package inc.ahmedmourad.sherlock.viewmodel.controllers.auth

import androidx.lifecycle.ViewModel
import arrow.core.Either
import arrow.core.right
import inc.ahmedmourad.sherlock.domain.interactors.auth.SignInWithFacebookInteractor
import inc.ahmedmourad.sherlock.domain.interactors.auth.SignInWithGoogleInteractor
import inc.ahmedmourad.sherlock.domain.interactors.auth.SignInWithTwitterInteractor
import inc.ahmedmourad.sherlock.domain.interactors.auth.SignUpInteractor
import inc.ahmedmourad.sherlock.domain.model.auth.DomainIncompleteUser
import inc.ahmedmourad.sherlock.domain.model.auth.DomainSignedInUser
import inc.ahmedmourad.sherlock.mapper.toAppIncompleteUser
import inc.ahmedmourad.sherlock.mapper.toAppSignedInUser
import inc.ahmedmourad.sherlock.model.auth.AppIncompleteUser
import inc.ahmedmourad.sherlock.model.auth.AppSignUpUser
import inc.ahmedmourad.sherlock.model.auth.AppSignedInUser
import inc.ahmedmourad.sherlock.viewmodel.model.DefaultLiveData
import io.reactivex.android.schedulers.AndroidSchedulers

internal class SignUpViewModel(
        private val signUpInteractor: SignUpInteractor,
        private val signUpWithGoogleInteractor: SignInWithGoogleInteractor,
        private val signUpWithFacebookInteractor: SignInWithFacebookInteractor,
        private val signUpWithTwitterInteractor: SignInWithTwitterInteractor
) : ViewModel() {

    val password by lazy { DefaultLiveData("") }
    val passwordConfirmation by lazy { DefaultLiveData("") }
    val email by lazy { DefaultLiveData("") }
    val username by lazy { DefaultLiveData("") }
    val phoneNumber by lazy { DefaultLiveData("") }
    val picturePath by lazy { DefaultLiveData("") }

    fun onSignUp() = signUpInteractor(toAppSignUpUser().toDomainSignUpUser())
            .map { it.map(DomainSignedInUser::right) }
            .map(::toAppUserEither)
            .observeOn(AndroidSchedulers.mainThread())

    fun onSignUpWithGoogle() = signUpWithGoogleInteractor()
            .map(::toAppUserEither)
            .observeOn(AndroidSchedulers.mainThread())

    fun onSignUpWithFacebook() = signUpWithFacebookInteractor()
            .map(::toAppUserEither)
            .observeOn(AndroidSchedulers.mainThread())

    fun onSignUpWithTwitter() = signUpWithTwitterInteractor()
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

    private fun toAppSignUpUser() = AppSignUpUser(
            password.value,
            email.value.trim(),
            username.value.trim(),
            phoneNumber.value.trim(),
            picturePath.value
    )
}
