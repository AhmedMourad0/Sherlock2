package inc.ahmedmourad.sherlock.dagger.modules

import dagger.Lazy
import dagger.Module
import dagger.Provides
import dagger.Reusable
import inc.ahmedmourad.sherlock.dagger.modules.factories.ResultsRecyclerAdapterAbstractFactory
import inc.ahmedmourad.sherlock.dagger.modules.factories.ResultsRecyclerAdapterFactory
import inc.ahmedmourad.sherlock.dagger.modules.factories.SectionsRecyclerAdapterAbstractFactory
import inc.ahmedmourad.sherlock.dagger.modules.factories.SectionsRecyclerAdapterFactory
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
    ): ResultsRecyclerAdapterAbstractFactory = ResultsRecyclerAdapterFactory(dateManager, formatter)
}

@Module
internal object SectionsRecyclerAdapterModule {
    @Provides
    @Reusable
    @JvmStatic
    fun provideSectionsRecyclerAdapter(): SectionsRecyclerAdapterAbstractFactory = SectionsRecyclerAdapterFactory()
}
