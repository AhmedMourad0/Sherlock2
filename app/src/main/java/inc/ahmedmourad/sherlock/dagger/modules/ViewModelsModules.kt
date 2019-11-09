package inc.ahmedmourad.sherlock.dagger.modules

import androidx.lifecycle.ViewModelProvider
import arrow.syntax.function.curried
import arrow.syntax.function.partially1
import dagger.Lazy
import dagger.Module
import dagger.Provides
import dagger.Reusable
import inc.ahmedmourad.sherlock.dagger.modules.factories.*
import inc.ahmedmourad.sherlock.dagger.modules.qualifiers.AddChildViewModelQualifier
import inc.ahmedmourad.sherlock.dagger.modules.qualifiers.FindChildrenViewModelQualifier
import inc.ahmedmourad.sherlock.dagger.modules.qualifiers.MainActivityViewModelQualifier
import inc.ahmedmourad.sherlock.dagger.modules.qualifiers.SherlockServiceIntentQualifier
import inc.ahmedmourad.sherlock.domain.dagger.modules.factories.ChildrenFilterFactory
import inc.ahmedmourad.sherlock.domain.interactors.*

@Module
internal object MainActivityViewModelModule {
    @Provides
    @Reusable
    @MainActivityViewModelQualifier
    @JvmStatic
    fun provideMainActivityViewModel(
            observeInternetConnectivityInteractor: ObserveInternetConnectivityInteractor
    ): ViewModelProvider.NewInstanceFactory {
        return MainActivityViewModelFactory(observeInternetConnectivityInteractor)
    }
}

@Module
internal object AddChildViewModelModule {
    @Provides
    @Reusable
    @AddChildViewModelQualifier
    @JvmStatic
    fun provideAddChildViewModel(
            @SherlockServiceIntentQualifier serviceFactory: Lazy<SherlockServiceIntentFactory>,
            observeInternetConnectivityInteractor: ObserveInternetConnectivityInteractor,
            checkInternetConnectivityInteractor: CheckInternetConnectivityInteractor,
            observePublishingStateInteractor: ObservePublishingStateInteractor,
            checkPublishingStateInteractor: CheckPublishingStateInteractor
    ): ViewModelProvider.NewInstanceFactory {
        return AddChildViewModelFactory(
                serviceFactory,
                observeInternetConnectivityInteractor,
                checkInternetConnectivityInteractor,
                observePublishingStateInteractor,
                checkPublishingStateInteractor
        )
    }
}

@Module
internal object FindChildrenViewModelModule {
    @Provides
    @Reusable
    @FindChildrenViewModelQualifier
    @JvmStatic
    fun provideFindChildrenViewModel(
            observeInternetConnectivityInteractor: ObserveInternetConnectivityInteractor
    ): ViewModelProvider.NewInstanceFactory {
        return FindChildrenViewModelFactory(
                observeInternetConnectivityInteractor
        )
    }
}

@Module
internal object SearchResultsViewModelModule {
    @Provides
    @Reusable
    @JvmStatic
    fun provideSearchResultViewModel(
            interactor: FindChildrenInteractor,
            filterFactory: ChildrenFilterFactory
    ): SearchResultsViewModelFactoryFactory {
        return ::searchResultsViewModelFactoryFactory.curried()(interactor)(filterFactory)
    }
}

@Module
internal object DisplayChildViewModelModule {
    @Provides
    @Reusable
    @JvmStatic
    fun provideDisplayChildViewModel(interactor: FindChildInteractor): DisplayChildViewModelFactoryFactory {
        return ::displayChildViewModelFactoryFactory.partially1(interactor)
    }
}
