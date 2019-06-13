package inc.ahmedmourad.sherlock.viewmodel.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import inc.ahmedmourad.sherlock.dagger.modules.domain.factories.FindChildrenInteractorAbstractFactory
import inc.ahmedmourad.sherlock.model.AppChildCriteriaRules

import inc.ahmedmourad.sherlock.viewmodel.SearchResultsViewModel

class SearchResultsViewModelFactory(private val criteria: AppChildCriteriaRules, private val interactor: FindChildrenInteractorAbstractFactory) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>) = SearchResultsViewModel(criteria, interactor) as T
}
