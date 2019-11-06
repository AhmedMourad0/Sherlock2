package inc.ahmedmourad.sherlock.domain.dagger.modules

import dagger.Lazy
import dagger.Module
import dagger.Provides
import dagger.Reusable
import inc.ahmedmourad.sherlock.domain.bus.Bus
import inc.ahmedmourad.sherlock.domain.dagger.modules.factories.*
import inc.ahmedmourad.sherlock.domain.data.ChildrenRepository
import inc.ahmedmourad.sherlock.domain.platform.ConnectivityManager

@Module
internal object AddChildInteractorModule {
    @Provides
    @Reusable
    @JvmStatic
    fun provideAddChildInteractor(childrenRepository: Lazy<ChildrenRepository>): AddChildInteractorAbstractFactory = AddChildInteractorFactory(childrenRepository)
}

@Module
internal object FindChildrenInteractorModule {
    @Provides
    @Reusable
    @JvmStatic
    fun provideFindChildrenInteractor(childrenRepository: Lazy<ChildrenRepository>): FindChildrenInteractorAbstractFactory = FindChildrenInteractorFactory(childrenRepository)
}

@Module
internal object FindChildInteractorModule {
    @Provides
    @Reusable
    @JvmStatic
    fun provideFindChildInteractor(childrenRepository: Lazy<ChildrenRepository>): FindChildInteractorAbstractFactory = FindChildInteractorFactory(childrenRepository)
}

@Module
internal object FindLastSearchResultsInteractorModule {
    @Provides
    @Reusable
    @JvmStatic
    fun provideFindLastSearchResultsInteractor(childrenRepository: Lazy<ChildrenRepository>): FindLastSearchResultsInteractorAbstractFactory = FindLastSearchResultsInteractorFactory(childrenRepository)
}

@Module
internal object ObserveInternetConnectivityInteractorModule {
    @Provides
    @Reusable
    @JvmStatic
    fun provideObserveInternetConnectivityInteractor(connectivityManager: Lazy<ConnectivityManager>): ObserveInternetConnectivityInteractorAbstractFactory = ObserveInternetConnectivityInteractorFactory(connectivityManager)
}

@Module
internal object CheckInternetConnectivityInteractorModule {
    @Provides
    @Reusable
    @JvmStatic
    fun provideCheckInternetConnectivityInteractor(connectivityManager: Lazy<ConnectivityManager>): CheckInternetConnectivityInteractorAbstractFactory = CheckInternetConnectivityInteractorFactory(connectivityManager)
}

@Module
internal object ObservePublishingStateInteractorModule {
    @Provides
    @Reusable
    @JvmStatic
    fun provideObservePublishingStateInteractor(bus: Lazy<Bus>): ObservePublishingStateInteractorAbstractFactory = ObservePublishingStateInteractorFactory(bus)
}

@Module
internal object CheckPublishingStateInteractorModule {
    @Provides
    @Reusable
    @JvmStatic
    fun provideCheckPublishingStateInteractor(bus: Lazy<Bus>): CheckPublishingStateInteractorAbstractFactory = CheckPublishingStateInteractorFactory(bus)
}
