package inc.ahmedmourad.sherlock.viewmodel.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import inc.ahmedmourad.sherlock.dagger.modules.domain.factories.FindChildInteractorAbstractFactory

import inc.ahmedmourad.sherlock.viewmodel.model.DisplayChildViewModel

class DisplayChildViewModelFactory(private val childId: String, private val interactor: FindChildInteractorAbstractFactory) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>) = DisplayChildViewModel(childId, interactor) as T
}
