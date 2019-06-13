package inc.ahmedmourad.sherlock.dagger.modules.app

import dagger.Module
import dagger.Provides
import dagger.Reusable
import inc.ahmedmourad.sherlock.dagger.modules.app.factories.ResultsRecyclerAdapterFactory
import inc.ahmedmourad.sherlock.dagger.modules.app.factories.SectionsRecyclerAdapterFactory

@Module
class ResultsRecyclerAdapterModule {
    @Provides
    @Reusable
    fun provideResultsRecyclerAdapter() = ResultsRecyclerAdapterFactory()
}

@Module
class SectionsRecyclerAdapterModule {
    @Provides
    @Reusable
    fun provideSectionsRecyclerAdapter() = SectionsRecyclerAdapterFactory()
}
