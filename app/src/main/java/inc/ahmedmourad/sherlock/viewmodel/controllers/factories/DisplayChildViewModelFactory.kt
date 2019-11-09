package inc.ahmedmourad.sherlock.viewmodel.controllers.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import inc.ahmedmourad.sherlock.domain.interactors.FindChildInteractor
import inc.ahmedmourad.sherlock.model.AppSimpleRetrievedChild

import inc.ahmedmourad.sherlock.viewmodel.controllers.DisplayChildViewModel

internal class DisplayChildViewModelFactory(private val child: AppSimpleRetrievedChild, private val interactor: FindChildInteractor) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>) = DisplayChildViewModel(child, interactor) as T
}
