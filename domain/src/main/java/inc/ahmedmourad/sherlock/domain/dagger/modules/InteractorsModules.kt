package inc.ahmedmourad.sherlock.domain.dagger.modules

import arrow.syntax.function.partially1
import dagger.Lazy
import dagger.Module
import dagger.Provides
import dagger.Reusable
import inc.ahmedmourad.sherlock.domain.bus.Bus
import inc.ahmedmourad.sherlock.domain.data.ChildrenRepository
import inc.ahmedmourad.sherlock.domain.interactors.*
import inc.ahmedmourad.sherlock.domain.interactors.addChild
import inc.ahmedmourad.sherlock.domain.interactors.findChildren
import inc.ahmedmourad.sherlock.domain.platform.ConnectivityManager

@Module
internal object AddChildInteractorModule {
    @Provides
    @Reusable
    @JvmStatic
    fun provideAddChildInteractor(childrenRepository: Lazy<ChildrenRepository>): AddChildInteractor {
        return ::addChild.partially1(childrenRepository)
    }
}

@Module
internal object FindChildrenInteractorModule {
    @Provides
    @Reusable
    @JvmStatic
    fun provideFindChildrenInteractor(childrenRepository: Lazy<ChildrenRepository>): FindChildrenInteractor {
        return ::findChildren.partially1(childrenRepository)
    }
}

@Module
internal object FindChildInteractorModule {
    @Provides
    @Reusable
    @JvmStatic
    fun provideFindChildInteractor(childrenRepository: Lazy<ChildrenRepository>): FindChildInteractor {
        return ::findChild.partially1(childrenRepository)
    }
}

@Module
internal object FindLastSearchResultsInteractorModule {
    @Provides
    @Reusable
    @JvmStatic
    fun provideFindLastSearchResultsInteractor(childrenRepository: Lazy<ChildrenRepository>): FindLastSearchResultsInteractor {
        return ::findLastSearchResults.partially1(childrenRepository)
    }
}

@Module
internal object ObserveInternetConnectivityInteractorModule {
    @Provides
    @Reusable
    @JvmStatic
    fun provideObserveInternetConnectivityInteractor(connectivityManager: Lazy<ConnectivityManager>): ObserveInternetConnectivityInteractor {
        return ::observeInternetConnectivity.partially1(connectivityManager)
    }
}

@Module
internal object CheckInternetConnectivityInteractorModule {
    @Provides
    @Reusable
    @JvmStatic
    fun provideCheckInternetConnectivityInteractor(connectivityManager: Lazy<ConnectivityManager>): CheckInternetConnectivityInteractor {
        return ::checkInternetConnectivity.partially1(connectivityManager)
    }
}

@Module
internal object ObservePublishingStateInteractorModule {
    @Provides
    @Reusable
    @JvmStatic
    fun provideObservePublishingStateInteractor(bus: Lazy<Bus>): ObservePublishingStateInteractor {
        return ::observePublishingState.partially1(bus)
    }
}

@Module
internal object CheckPublishingStateInteractorModule {
    @Provides
    @Reusable
    @JvmStatic
    fun provideCheckPublishingStateInteractor(bus: Lazy<Bus>): CheckPublishingStateInteractor {
        return ::checkPublishingState.partially1(bus)
    }
}
