package inc.ahmedmourad.sherlock.dagger.modules

import arrow.syntax.function.curried
import dagger.Lazy
import dagger.Module
import dagger.Provides
import dagger.Reusable
import inc.ahmedmourad.sherlock.dagger.modules.factories.ResultsRecyclerAdapterFactory
import inc.ahmedmourad.sherlock.dagger.modules.factories.SectionsRecyclerAdapterFactory
import inc.ahmedmourad.sherlock.dagger.modules.factories.resultsRecyclerAdapterFactory
import inc.ahmedmourad.sherlock.dagger.modules.factories.sectionsRecyclerAdapterFactory
import inc.ahmedmourad.sherlock.domain.platform.DateManager
import inc.ahmedmourad.sherlock.formatter.Formatter

@Module
internal object ResultsRecyclerAdapterModule {
    @Provides
    @Reusable
    @JvmStatic
    fun provideResultsRecyclerAdapter(
            dateManager: Lazy<DateManager>,
            formatter: Lazy<Formatter>
    ): ResultsRecyclerAdapterFactory {
        return ::resultsRecyclerAdapterFactory.curried()(dateManager)(formatter)
    }
}

@Module
internal object SectionsRecyclerAdapterModule {
    @Provides
    @Reusable
    @JvmStatic
    fun provideSectionsRecyclerAdapter(): SectionsRecyclerAdapterFactory {
        return ::sectionsRecyclerAdapterFactory
    }
}
