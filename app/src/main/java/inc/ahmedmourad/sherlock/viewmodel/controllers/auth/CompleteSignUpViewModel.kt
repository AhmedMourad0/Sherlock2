package inc.ahmedmourad.sherlock.viewmodel.controllers.auth

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import inc.ahmedmourad.sherlock.domain.interactors.auth.CompleteSignUpInteractor
import inc.ahmedmourad.sherlock.domain.model.auth.DomainSignedInUser
import inc.ahmedmourad.sherlock.domain.model.children.PicturePath
import inc.ahmedmourad.sherlock.mapper.toAppSignedInUser
import inc.ahmedmourad.sherlock.model.auth.AppCompletedUser
import inc.ahmedmourad.sherlock.model.auth.AppIncompleteUser
import io.reactivex.android.schedulers.AndroidSchedulers

internal class CompleteSignUpViewModel(
        private val incompleteUser: AppIncompleteUser,
        private val completeSignUpInteractor: CompleteSignUpInteractor
) : ViewModel() {

    val email by lazy { MutableLiveData(incompleteUser.email ?: "") }
    val username by lazy { MutableLiveData(incompleteUser.name ?: "") }
    val phoneNumber by lazy { MutableLiveData(incompleteUser.phoneNumber ?: "") }
    val picturePath by lazy { MutableLiveData<PicturePath?>() }

    fun onCompleteSignUp() = completeSignUpInteractor(toAppCompletedUser().toDomainCompletedUser())
            .map { it.map(DomainSignedInUser::toAppSignedInUser) }
            .observeOn(AndroidSchedulers.mainThread())

    private fun toAppCompletedUser() = AppCompletedUser(
            incompleteUser.id,
            email.value!!.trim(),
            username.value!!.trim(),
            phoneNumber.value!!.trim(),
            picturePath.value!!.value
    )
}
