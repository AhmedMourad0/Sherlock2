package inc.ahmedmourad.sherlock.dagger.modules.app

import dagger.Module
import dagger.Provides
import dagger.Reusable
import inc.ahmedmourad.sherlock.dagger.modules.app.factories.ResultsRemoteViewsFactoryAbstractFactory
import inc.ahmedmourad.sherlock.dagger.modules.app.factories.ResultsRemoteViewsFactoryFactory
import inc.ahmedmourad.sherlock.dagger.modules.app.factories.ResultsRemoteViewsServiceAbstractFactory
import inc.ahmedmourad.sherlock.dagger.modules.app.factories.ResultsRemoteViewsServiceFactory

@Module
class ResultsRemoteViewsServiceModule {
    @Provides
    @Reusable
    fun provideResultsRemoteViewsService(): ResultsRemoteViewsServiceAbstractFactory = ResultsRemoteViewsServiceFactory()
}

@Module
class ResultsRemoteViewsFactoryModule {
    @Provides
    @Reusable
    fun provideResultsRemoteViewsFactory(): ResultsRemoteViewsFactoryAbstractFactory = ResultsRemoteViewsFactoryFactory()
}
