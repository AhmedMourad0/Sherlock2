package inc.ahmedmourad.sherlock.dagger.modules.app.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Lazy
import inc.ahmedmourad.sherlock.dagger.modules.domain.factories.FilterAbstractFactory
import inc.ahmedmourad.sherlock.dagger.modules.domain.factories.FindChildInteractorAbstractFactory
import inc.ahmedmourad.sherlock.dagger.modules.domain.factories.FindChildrenInteractorAbstractFactory
import inc.ahmedmourad.sherlock.model.AppChildCriteriaRules
import inc.ahmedmourad.sherlock.viewmodel.AddChildViewModel
import inc.ahmedmourad.sherlock.viewmodel.FindChildrenViewModel
import inc.ahmedmourad.sherlock.viewmodel.factories.DisplayChildViewModelFactory

import inc.ahmedmourad.sherlock.viewmodel.factories.SearchResultsViewModelFactory

class AddChildViewModelFactory(private val intentServiceFactory: Lazy<SherlockIntentServiceAbstractFactory>) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>) = AddChildViewModel(intentServiceFactory) as T
}

class FindChildrenViewModelFactory : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>) = FindChildrenViewModel() as T
}

interface SearchResultsViewModelFactoryAbstractFactory {
    fun create(criteria: AppChildCriteriaRules): ViewModelProvider.NewInstanceFactory
}

class SearchResultsViewModelFactoryFactory(
        private val interactor: FindChildrenInteractorAbstractFactory,
        private val filterFactory: FilterAbstractFactory) : SearchResultsViewModelFactoryAbstractFactory {
    override fun create(criteria: AppChildCriteriaRules) = SearchResultsViewModelFactory(criteria, interactor, filterFactory)
}

interface DisplayChildViewModelFactoryAbstractFactory {
    fun create(childId: String): ViewModelProvider.NewInstanceFactory
}

class DisplayChildViewModelFactoryFactory(private val interactor: FindChildInteractorAbstractFactory) : DisplayChildViewModelFactoryAbstractFactory {
    override fun create(childId: String) = DisplayChildViewModelFactory(childId, interactor)
}
