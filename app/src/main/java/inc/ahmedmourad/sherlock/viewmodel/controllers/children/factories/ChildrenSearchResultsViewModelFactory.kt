package inc.ahmedmourad.sherlock.viewmodel.controllers.children.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import inc.ahmedmourad.sherlock.domain.dagger.modules.factories.ChildrenFilterFactory
import inc.ahmedmourad.sherlock.domain.interactors.children.FindChildrenInteractor
import inc.ahmedmourad.sherlock.model.children.AppChildCriteriaRules

import inc.ahmedmourad.sherlock.viewmodel.controllers.children.ChildrenSearchResultsViewModel

internal typealias ChildrenSearchResultsViewModelFactoryFactory =
        (@JvmSuppressWildcards AppChildCriteriaRules) -> @JvmSuppressWildcards ViewModelProvider.NewInstanceFactory

internal fun childrenSearchResultsViewModelFactoryFactory(
        interactor: FindChildrenInteractor,
        filterFactory: ChildrenFilterFactory,
        criteria: AppChildCriteriaRules
): ChildrenSearchResultsViewModelFactory {
    return ChildrenSearchResultsViewModelFactory(interactor, filterFactory, criteria)
}

internal class ChildrenSearchResultsViewModelFactory(
        private val interactor: FindChildrenInteractor,
        private val filterFactory: ChildrenFilterFactory,
        private val criteria: AppChildCriteriaRules
) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ChildrenSearchResultsViewModel(interactor, filterFactory, criteria) as T
    }
}
