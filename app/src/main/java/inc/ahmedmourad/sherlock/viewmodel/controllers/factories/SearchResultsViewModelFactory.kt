package inc.ahmedmourad.sherlock.viewmodel.controllers.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import inc.ahmedmourad.sherlock.domain.dagger.modules.factories.FilterAbstractFactory
import inc.ahmedmourad.sherlock.domain.dagger.modules.factories.FindChildrenInteractorAbstractFactory
import inc.ahmedmourad.sherlock.model.AppChildCriteriaRules

import inc.ahmedmourad.sherlock.viewmodel.controllers.SearchResultsViewModel

internal class SearchResultsViewModelFactory(
        private val criteria: AppChildCriteriaRules,
        private val interactor: FindChildrenInteractorAbstractFactory,
        private val filterFactory: FilterAbstractFactory) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>) = SearchResultsViewModel(criteria, interactor, filterFactory) as T
}
