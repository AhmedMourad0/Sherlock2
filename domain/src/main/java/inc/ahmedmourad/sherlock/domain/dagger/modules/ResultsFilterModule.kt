package inc.ahmedmourad.sherlock.domain.dagger.modules

import dagger.Module
import dagger.Provides
import dagger.Reusable
import inc.ahmedmourad.sherlock.domain.dagger.modules.factories.CriteriaAbstractFactory
import inc.ahmedmourad.sherlock.domain.dagger.modules.factories.FilterAbstractFactory
import inc.ahmedmourad.sherlock.domain.dagger.modules.factories.ResultsFilterFactory

@Module(includes = [LooseCriteriaModule::class])
internal object ResultsFilterModule {
    @Provides
    @Reusable
    @JvmStatic
    fun provideResultsFilter(criteriaFactory: CriteriaAbstractFactory): FilterAbstractFactory = ResultsFilterFactory(criteriaFactory)
}
