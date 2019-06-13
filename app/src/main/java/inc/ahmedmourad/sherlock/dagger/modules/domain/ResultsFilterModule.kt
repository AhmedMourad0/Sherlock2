package inc.ahmedmourad.sherlock.dagger.modules.domain

import dagger.Module
import dagger.Provides
import dagger.Reusable
import inc.ahmedmourad.sherlock.dagger.modules.domain.factories.CriteriaFactory
import inc.ahmedmourad.sherlock.dagger.modules.domain.factories.FilterFactory
import inc.ahmedmourad.sherlock.dagger.modules.domain.factories.ResultsFilterFactory

@Module
class ResultsFilterModule {
    @Provides
    @Reusable
    fun provideResultsFilter(criteriaFactory: CriteriaFactory): FilterFactory = ResultsFilterFactory(criteriaFactory)
}
