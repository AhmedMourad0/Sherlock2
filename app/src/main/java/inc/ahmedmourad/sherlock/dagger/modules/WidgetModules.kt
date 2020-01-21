package inc.ahmedmourad.sherlock.dagger.modules

import arrow.syntax.function.curried
import arrow.syntax.function.uncurried
import dagger.Lazy
import dagger.Module
import dagger.Provides
import dagger.Reusable
import inc.ahmedmourad.sherlock.dagger.modules.factories.ResultsRemoteViewsFactoryFactory
import inc.ahmedmourad.sherlock.dagger.modules.factories.ResultsRemoteViewsServiceIntentFactory
import inc.ahmedmourad.sherlock.dagger.modules.factories.resultsRemoteViewsFactoryFactory
import inc.ahmedmourad.sherlock.dagger.modules.factories.resultsRemoteViewsServiceIntentFactory
import inc.ahmedmourad.sherlock.domain.platform.DateManager
import inc.ahmedmourad.sherlock.utils.formatter.Formatter

@Module
internal object ResultsRemoteViewsServiceModule {
    @Provides
    @Reusable
    @JvmStatic
    fun provideResultsRemoteViewsService(): ResultsRemoteViewsServiceIntentFactory {
        return ::resultsRemoteViewsServiceIntentFactory
    }
}

@Module(includes = [
    TextFormatterModule::class
])
internal object ResultsRemoteViewsFactoryModule {
    @Provides
    @Reusable
    @JvmStatic
    fun provideResultsRemoteViewsFactory(
            formatter: Lazy<Formatter>,
            dateManager: Lazy<DateManager>
    ): ResultsRemoteViewsFactoryFactory {
        return ::resultsRemoteViewsFactoryFactory.curried()(formatter)(dateManager).uncurried()
    }
}
