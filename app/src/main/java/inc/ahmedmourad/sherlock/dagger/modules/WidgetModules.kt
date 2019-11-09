package inc.ahmedmourad.sherlock.dagger.modules

import dagger.Module
import dagger.Provides
import dagger.Reusable
import inc.ahmedmourad.sherlock.dagger.modules.factories.ResultsRemoteViewsFactoryFactory
import inc.ahmedmourad.sherlock.dagger.modules.factories.ResultsRemoteViewsServiceIntentFactory
import inc.ahmedmourad.sherlock.dagger.modules.factories.resultsRemoteViewsFactoryFactory
import inc.ahmedmourad.sherlock.dagger.modules.factories.resultsRemoteViewsServiceIntentFactory

@Module
internal object ResultsRemoteViewsServiceModule {
    @Provides
    @Reusable
    @JvmStatic
    fun provideResultsRemoteViewsService(): ResultsRemoteViewsServiceIntentFactory {
        return ::resultsRemoteViewsServiceIntentFactory
    }
}

@Module
internal object ResultsRemoteViewsFactoryModule {
    @Provides
    @Reusable
    @JvmStatic
    fun provideResultsRemoteViewsFactory(): ResultsRemoteViewsFactoryFactory {
        return ::resultsRemoteViewsFactoryFactory
    }
}
