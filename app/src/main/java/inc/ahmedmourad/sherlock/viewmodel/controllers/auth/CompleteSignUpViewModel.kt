package inc.ahmedmourad.sherlock.viewmodel.controllers.auth

import androidx.lifecycle.ViewModel
import arrow.core.getOrElse
import inc.ahmedmourad.sherlock.domain.interactors.auth.CompleteSignUpInteractor
import inc.ahmedmourad.sherlock.domain.model.auth.DomainSignedInUser
import inc.ahmedmourad.sherlock.mapper.toAppSignedInUser
import inc.ahmedmourad.sherlock.model.auth.AppCompletedUser
import inc.ahmedmourad.sherlock.model.auth.AppIncompleteUser
import inc.ahmedmourad.sherlock.viewmodel.model.DefaultLiveData
import io.reactivex.android.schedulers.AndroidSchedulers

internal class CompleteSignUpViewModel(
        private val incompleteUser: AppIncompleteUser,
        private val completeSignUpInteractor: CompleteSignUpInteractor
) : ViewModel() {

    val email by lazy { DefaultLiveData(incompleteUser.email.getOrElse { "" }) }
    val username by lazy { DefaultLiveData(incompleteUser.name.getOrElse { "" }) }
    val phoneNumber by lazy { DefaultLiveData(incompleteUser.phoneNumber.getOrElse { "" }) }
    val picturePath by lazy { DefaultLiveData(incompleteUser.pictureUrl.getOrElse { "" }) }

    fun onCompleteSignUp() = completeSignUpInteractor(toAppCompletedUser().toDomainCompletedUser())
            .map { it.map(DomainSignedInUser::toAppSignedInUser) }
            .observeOn(AndroidSchedulers.mainThread())

    private fun toAppCompletedUser() = AppCompletedUser(
            incompleteUser.id,
            email.value.trim(),
            username.value.trim(),
            phoneNumber.value.trim(),
            picturePath.value
    )
}
