package inc.ahmedmourad.sherlock.viewmodel.controllers.auth

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import arrow.core.Either
import arrow.core.right
import inc.ahmedmourad.sherlock.domain.interactors.auth.SignInWithFacebookInteractor
import inc.ahmedmourad.sherlock.domain.interactors.auth.SignInWithGoogleInteractor
import inc.ahmedmourad.sherlock.domain.interactors.auth.SignInWithTwitterInteractor
import inc.ahmedmourad.sherlock.domain.interactors.auth.SignUpInteractor
import inc.ahmedmourad.sherlock.domain.model.auth.IncompleteUser
import inc.ahmedmourad.sherlock.domain.model.auth.SignedInUser
import inc.ahmedmourad.sherlock.domain.model.children.submodel.PicturePath
import inc.ahmedmourad.sherlock.mapper.toAppIncompleteUser
import inc.ahmedmourad.sherlock.mapper.toAppSignedInUser
import inc.ahmedmourad.sherlock.model.auth.AppIncompleteUser
import inc.ahmedmourad.sherlock.model.auth.AppSignUpUser
import inc.ahmedmourad.sherlock.model.auth.AppSignedInUser
import io.reactivex.android.schedulers.AndroidSchedulers

internal class SignUpViewModel(
        private val signUpInteractor: SignUpInteractor,
        private val signUpWithGoogleInteractor: SignInWithGoogleInteractor,
        private val signUpWithFacebookInteractor: SignInWithFacebookInteractor,
        private val signUpWithTwitterInteractor: SignInWithTwitterInteractor
) : ViewModel() {

    val password by lazy { MutableLiveData("") }
    val passwordConfirmation by lazy { MutableLiveData("") }
    val email by lazy { MutableLiveData("") }
    val username by lazy { MutableLiveData("") }
    val phoneNumber by lazy { MutableLiveData("") }
    val picturePath by lazy { MutableLiveData<PicturePath?>() }

    fun onSignUp() = signUpInteractor(toAppSignUpUser().toDomainSignUpUser())
            .map { it.map(SignedInUser::right) }
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
            resultEither: Either<Throwable, Either<IncompleteUser, SignedInUser>>
    ): Either<Throwable, Either<AppIncompleteUser, AppSignedInUser>> {
        return resultEither.map { either ->
            either.bimap(
                    leftOperation = IncompleteUser::toAppIncompleteUser,
                    rightOperation = SignedInUser::toAppSignedInUser
            )
        }
    }

    private fun toAppSignUpUser() = AppSignUpUser(
            password.value!!,
            email.value!!.trim(),
            username.value!!.trim(),
            phoneNumber.value!!.trim(),
            picturePath.value!!.value
    )
}
