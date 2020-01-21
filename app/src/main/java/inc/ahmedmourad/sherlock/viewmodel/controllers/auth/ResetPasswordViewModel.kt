package inc.ahmedmourad.sherlock.viewmodel.controllers.auth

import androidx.lifecycle.ViewModel
import inc.ahmedmourad.sherlock.domain.interactors.auth.SendPasswordResetEmailInteractor
import inc.ahmedmourad.sherlock.viewmodel.model.DefaultLiveData
import io.reactivex.android.schedulers.AndroidSchedulers

internal class ResetPasswordViewModel(
        private val sendPasswordResetEmailInteractor: SendPasswordResetEmailInteractor
) : ViewModel() {

    val email by lazy { DefaultLiveData("") }

    fun onCompleteSignUp() = sendPasswordResetEmailInteractor(email.value.trim())
            .observeOn(AndroidSchedulers.mainThread())
}
