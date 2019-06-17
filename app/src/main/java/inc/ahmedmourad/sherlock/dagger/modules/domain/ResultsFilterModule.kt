package inc.ahmedmourad.sherlock.dagger.modules.domain

import dagger.Module
import dagger.Provides
import dagger.Reusable
import inc.ahmedmourad.sherlock.dagger.modules.domain.factories.CriteriaAbstractFactory
import inc.ahmedmourad.sherlock.dagger.modules.domain.factories.FilterAbstractFactory
import inc.ahmedmourad.sherlock.dagger.modules.domain.factories.ResultsFilterFactory

@Module
class ResultsFilterModule {
    @Provides
    @Reusable
    fun provideResultsFilter(criteriaFactory: CriteriaAbstractFactory): FilterAbstractFactory = ResultsFilterFactory(criteriaFactory)
}
