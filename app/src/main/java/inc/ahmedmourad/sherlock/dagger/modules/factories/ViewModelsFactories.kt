package inc.ahmedmourad.sherlock.dagger.modules.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Lazy
import inc.ahmedmourad.sherlock.domain.dagger.modules.factories.*
import inc.ahmedmourad.sherlock.model.AppChildCriteriaRules
import inc.ahmedmourad.sherlock.model.AppSimpleRetrievedChild
import inc.ahmedmourad.sherlock.viewmodel.activity.MainActivityViewModel
import inc.ahmedmourad.sherlock.viewmodel.controllers.AddChildViewModel
import inc.ahmedmourad.sherlock.viewmodel.controllers.FindChildrenViewModel
import inc.ahmedmourad.sherlock.viewmodel.controllers.factories.DisplayChildViewModelFactory

import inc.ahmedmourad.sherlock.viewmodel.controllers.factories.SearchResultsViewModelFactory

internal class MainActivityViewModelFactory(
        private val observeInternetConnectivityInteractor: ObserveInternetConnectivityInteractorAbstractFactory
) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>) = MainActivityViewModel(
            observeInternetConnectivityInteractor
    ) as T
}

internal class AddChildViewModelFactory(
        private val serviceFactory: Lazy<SherlockServiceAbstractFactory>,
        private val observeInternetConnectivityInteractor: ObserveInternetConnectivityInteractorAbstractFactory,
        private val checkInternetConnectivityInteractor: CheckInternetConnectivityInteractorAbstractFactory,
        private val observePublishingStateInteractor: ObservePublishingStateInteractorAbstractFactory,
        private val checkPublishingStateInteractor: CheckPublishingStateInteractorAbstractFactory
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
        private val observeInternetConnectivityInteractor: ObserveInternetConnectivityInteractorAbstractFactory
) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>) = FindChildrenViewModel(
            observeInternetConnectivityInteractor
    ) as T
}

internal interface SearchResultsViewModelFactoryAbstractFactory {
    fun create(criteria: AppChildCriteriaRules): ViewModelProvider.NewInstanceFactory
}

internal class SearchResultsViewModelFactoryFactory(
        private val interactor: FindChildrenInteractorAbstractFactory,
        private val filterFactory: FilterAbstractFactory) : SearchResultsViewModelFactoryAbstractFactory {
    override fun create(criteria: AppChildCriteriaRules) = SearchResultsViewModelFactory(criteria, interactor, filterFactory)
}

internal interface DisplayChildViewModelFactoryAbstractFactory {
    fun create(child: AppSimpleRetrievedChild): ViewModelProvider.NewInstanceFactory
}

internal class DisplayChildViewModelFactoryFactory(private val interactor: FindChildInteractorAbstractFactory) : DisplayChildViewModelFactoryAbstractFactory {
    override fun create(child: AppSimpleRetrievedChild) = DisplayChildViewModelFactory(child, interactor)
}
