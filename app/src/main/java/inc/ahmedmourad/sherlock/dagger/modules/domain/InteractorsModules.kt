package inc.ahmedmourad.sherlock.dagger.modules.domain

import dagger.Lazy
import dagger.Module
import dagger.Provides
import dagger.Reusable
import inc.ahmedmourad.sherlock.dagger.modules.domain.factories.*
import inc.ahmedmourad.sherlock.domain.repository.Repository

@Module
class AddChildInteractorModule {
    @Provides
    @Reusable
    fun provideAddChildInteractor(repository: Lazy<Repository>): AddChildInteractorAbstractFactory = AddChildInteractorFactory(repository)
}

@Module
class FindChildrenInteractorModule {
    @Provides
    @Reusable
    fun provideFindChildrenInteractor(repository: Lazy<Repository>): FindChildrenInteractorAbstractFactory = FindChildrenInteractorFactory(repository)
}

@Module
class GetLastSearchResultsInteractorModule {
    @Provides
    @Reusable
    fun provideGetLastSearchResultsInteractor(repository: Lazy<Repository>): GetLastSearchResultsInteractorAbstractFactory = GetLastSearchResultsInteractorFactory(repository)
}
