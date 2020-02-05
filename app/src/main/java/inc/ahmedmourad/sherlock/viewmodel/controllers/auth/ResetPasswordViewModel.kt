package inc.ahmedmourad.sherlock.viewmodel.controllers.auth

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import inc.ahmedmourad.sherlock.domain.interactors.auth.SendPasswordResetEmailInteractor
import io.reactivex.android.schedulers.AndroidSchedulers

internal class ResetPasswordViewModel(
        private val sendPasswordResetEmailInteractor: SendPasswordResetEmailInteractor
) : ViewModel() {

    val email by lazy { MutableLiveData("") }

    fun onCompleteSignUp() = sendPasswordResetEmailInteractor(email.value!!.trim())
            .observeOn(AndroidSchedulers.mainThread())
}
