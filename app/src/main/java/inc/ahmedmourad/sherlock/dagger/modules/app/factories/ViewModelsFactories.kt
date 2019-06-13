package inc.ahmedmourad.sherlock.dagger.modules.app.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import inc.ahmedmourad.sherlock.dagger.modules.domain.factories.FindChildrenInteractorAbstractFactory
import inc.ahmedmourad.sherlock.model.AppChildCriteriaRules
import inc.ahmedmourad.sherlock.viewmodel.AddChildViewModel
import inc.ahmedmourad.sherlock.viewmodel.FindChildrenViewModel

import inc.ahmedmourad.sherlock.viewmodel.factories.SearchResultsViewModelFactory

class AddChildViewModelFactory : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>) = AddChildViewModel() as T
}

class FindChildrenViewModelFactory : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>) = FindChildrenViewModel() as T
}

class SearchResultsViewModelFactoryFactory(private val interactor: FindChildrenInteractorAbstractFactory) {
    fun create(criteria: AppChildCriteriaRules) = SearchResultsViewModelFactory(criteria, interactor)
}
