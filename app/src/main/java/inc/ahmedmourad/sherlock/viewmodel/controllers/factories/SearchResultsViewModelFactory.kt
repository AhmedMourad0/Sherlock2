package inc.ahmedmourad.sherlock.viewmodel.controllers.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import inc.ahmedmourad.sherlock.domain.dagger.modules.factories.ChildrenFilterFactory
import inc.ahmedmourad.sherlock.domain.interactors.FindChildrenInteractor
import inc.ahmedmourad.sherlock.model.AppChildCriteriaRules

import inc.ahmedmourad.sherlock.viewmodel.controllers.SearchResultsViewModel

internal class SearchResultsViewModelFactory(
        private val interactor: FindChildrenInteractor,
        private val filterFactory: ChildrenFilterFactory,
        private val criteria: AppChildCriteriaRules
) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>) = SearchResultsViewModel(interactor, filterFactory, criteria) as T
}
