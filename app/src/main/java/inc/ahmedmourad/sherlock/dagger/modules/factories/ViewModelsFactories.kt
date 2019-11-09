package inc.ahmedmourad.sherlock.dagger.modules.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Lazy
import inc.ahmedmourad.sherlock.domain.dagger.modules.factories.ChildrenFilterFactory
import inc.ahmedmourad.sherlock.domain.interactors.*
import inc.ahmedmourad.sherlock.model.AppChildCriteriaRules
import inc.ahmedmourad.sherlock.model.AppSimpleRetrievedChild
import inc.ahmedmourad.sherlock.viewmodel.activity.MainActivityViewModel
import inc.ahmedmourad.sherlock.viewmodel.controllers.AddChildViewModel
import inc.ahmedmourad.sherlock.viewmodel.controllers.FindChildrenViewModel
import inc.ahmedmourad.sherlock.viewmodel.controllers.factories.DisplayChildViewModelFactory
import inc.ahmedmourad.sherlock.viewmodel.controllers.factories.SearchResultsViewModelFactory

internal class MainActivityViewModelFactory(
        private val observeInternetConnectivityInteractor: ObserveInternetConnectivityInteractor
) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>) = MainActivityViewModel(
            observeInternetConnectivityInteractor
    ) as T
}

internal class AddChildViewModelFactory(
        private val serviceFactory: Lazy<SherlockServiceIntentFactory>,
        private val observeInternetConnectivityInteractor: ObserveInternetConnectivityInteractor,
        private val checkInternetConnectivityInteractor: CheckInternetConnectivityInteractor,
        private val observePublishingStateInteractor: ObservePublishingStateInteractor,
        private val checkPublishingStateInteractor: CheckPublishingStateInteractor
) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>) = AddChildViewModel(
            serviceFactory,
            observeInternetConnectivityInteractor,
            checkInternetConnectivityInteractor,
            observePublishingStateInteractor,
            checkPublishingStateInteractor
    ) as T
}

internal class FindChildrenViewModelFactory(
        private val observeInternetConnectivityInteractor: ObserveInternetConnectivityInteractor
) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>) = FindChildrenViewModel(
            observeInternetConnectivityInteractor
    ) as T
}

internal typealias SearchResultsViewModelFactoryFactory =
        (@JvmSuppressWildcards AppChildCriteriaRules) -> @JvmSuppressWildcards ViewModelProvider.NewInstanceFactory

internal fun searchResultsViewModelFactoryFactory(
        interactor: FindChildrenInteractor,
        filterFactory: ChildrenFilterFactory,
        criteria: AppChildCriteriaRules
): SearchResultsViewModelFactory {
    return SearchResultsViewModelFactory(interactor, filterFactory, criteria)
}

internal typealias DisplayChildViewModelFactoryFactory =
        (@JvmSuppressWildcards AppSimpleRetrievedChild) -> @JvmSuppressWildcards ViewModelProvider.NewInstanceFactory

internal fun displayChildViewModelFactoryFactory(
        interactor: FindChildInteractor,
        child: AppSimpleRetrievedChild
): DisplayChildViewModelFactory {
    return DisplayChildViewModelFactory(child, interactor)
}
