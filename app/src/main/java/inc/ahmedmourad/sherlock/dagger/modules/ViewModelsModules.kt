package inc.ahmedmourad.sherlock.dagger.modules

import androidx.lifecycle.ViewModelProvider
import dagger.Lazy
import dagger.Module
import dagger.Provides
import dagger.Reusable
import inc.ahmedmourad.sherlock.dagger.modules.factories.*
import inc.ahmedmourad.sherlock.dagger.modules.qualifiers.AddChildViewModelQualifier
import inc.ahmedmourad.sherlock.dagger.modules.qualifiers.FindChildrenViewModelQualifier
import inc.ahmedmourad.sherlock.dagger.modules.qualifiers.MainActivityViewModelQualifier
import inc.ahmedmourad.sherlock.domain.dagger.modules.factories.*

@Module
internal object MainActivityViewModelModule {
    @Provides
    @Reusable
    @MainActivityViewModelQualifier
    @JvmStatic
    fun provideMainActivityViewModel(
            observeInternetConnectivityInteractor: ObserveInternetConnectivityInteractorAbstractFactory
    ): ViewModelProvider.NewInstanceFactory = MainActivityViewModelFactory(observeInternetConnectivityInteractor)
}

@Module
internal object AddChildViewModelModule {
    @Provides
    @Reusable
    @AddChildViewModelQualifier
    @JvmStatic
    fun provideAddChildViewModel(
            serviceFactory: Lazy<SherlockServiceAbstractFactory>,
            observeInternetConnectivityInteractor: ObserveInternetConnectivityInteractorAbstractFactory,
            checkInternetConnectivityInteractor: CheckInternetConnectivityInteractorAbstractFactory,
            observePublishingStateInteractor: ObservePublishingStateInteractorAbstractFactory,
            checkPublishingStateInteractor: CheckPublishingStateInteractorAbstractFactory
    ): ViewModelProvider.NewInstanceFactory = AddChildViewModelFactory(
            serviceFactory,
            observeInternetConnectivityInteractor,
            checkInternetConnectivityInteractor,
            observePublishingStateInteractor,
            checkPublishingStateInteractor
    )
}

@Module
internal object FindChildrenViewModelModule {
    @Provides
    @Reusable
    @FindChildrenViewModelQualifier
    @JvmStatic
    fun provideFindChildrenViewModel(
            observeInternetConnectivityInteractor: ObserveInternetConnectivityInteractorAbstractFactory
    ): ViewModelProvider.NewInstanceFactory = FindChildrenViewModelFactory(
            observeInternetConnectivityInteractor
    )
}

@Module
internal object SearchResultsViewModelModule {
    @Provides
    @Reusable
    @JvmStatic
    fun provideSearchResultViewModel(
            interactor: FindChildrenInteractorAbstractFactory,
            filterFactory: FilterAbstractFactory
    ): SearchResultsViewModelFactoryAbstractFactory = SearchResultsViewModelFactoryFactory(interactor, filterFactory)
}

@Module
internal object DisplayChildViewModelModule {
    @Provides
    @Reusable
    @JvmStatic
    fun provideDisplayChildViewModel(interactor: FindChildInteractorAbstractFactory): DisplayChildViewModelFactoryAbstractFactory = DisplayChildViewModelFactoryFactory(interactor)
}
