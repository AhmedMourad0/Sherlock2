package inc.ahmedmourad.sherlock.dagger.modules

import dagger.Module
import dagger.Provides
import dagger.Reusable
import inc.ahmedmourad.sherlock.dagger.modules.factories.ResultsRemoteViewsFactoryAbstractFactory
import inc.ahmedmourad.sherlock.dagger.modules.factories.ResultsRemoteViewsFactoryFactory
import inc.ahmedmourad.sherlock.dagger.modules.factories.ResultsRemoteViewsServiceAbstractFactory
import inc.ahmedmourad.sherlock.dagger.modules.factories.ResultsRemoteViewsServiceFactory

@Module
internal object ResultsRemoteViewsServiceModule {
    @Provides
    @Reusable
    @JvmStatic
    fun provideResultsRemoteViewsService(): ResultsRemoteViewsServiceAbstractFactory = ResultsRemoteViewsServiceFactory()
}

@Module
internal object ResultsRemoteViewsFactoryModule {
    @Provides
    @Reusable
    @JvmStatic
    fun provideResultsRemoteViewsFactory(): ResultsRemoteViewsFactoryAbstractFactory = ResultsRemoteViewsFactoryFactory()
}
