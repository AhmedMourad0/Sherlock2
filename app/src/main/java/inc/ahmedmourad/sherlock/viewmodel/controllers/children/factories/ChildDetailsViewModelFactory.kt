package inc.ahmedmourad.sherlock.viewmodel.controllers.children.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import inc.ahmedmourad.sherlock.domain.interactors.children.FindChildInteractor
import inc.ahmedmourad.sherlock.model.children.AppSimpleRetrievedChild

import inc.ahmedmourad.sherlock.viewmodel.controllers.children.ChildDetailsViewModel

internal typealias ChildDetailsViewModelFactoryFactory =
        (@JvmSuppressWildcards AppSimpleRetrievedChild) -> @JvmSuppressWildcards ViewModelProvider.NewInstanceFactory

internal fun childDetailsViewModelFactoryFactory(
        interactor: FindChildInteractor,
        child: AppSimpleRetrievedChild
): ChildDetailsViewModelFactory {
    return ChildDetailsViewModelFactory(child, interactor)
}

internal class ChildDetailsViewModelFactory(
        private val child: AppSimpleRetrievedChild,
        private val interactor: FindChildInteractor
) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>) = ChildDetailsViewModel(child, interactor) as T
}
