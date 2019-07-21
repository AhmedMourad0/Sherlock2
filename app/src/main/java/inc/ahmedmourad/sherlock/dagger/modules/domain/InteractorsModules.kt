package inc.ahmedmourad.sherlock.dagger.modules.domain

import dagger.Lazy
import dagger.Module
import dagger.Provides
import dagger.Reusable
import inc.ahmedmourad.sherlock.dagger.modules.domain.factories.*
import inc.ahmedmourad.sherlock.domain.interactors.GetLastSearchResultsInteractor
import inc.ahmedmourad.sherlock.domain.interactors.Interactor
import inc.ahmedmourad.sherlock.domain.model.DomainUrlChild
import inc.ahmedmourad.sherlock.domain.repository.Repository
import io.reactivex.Flowable

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
class FindChildInteractorModule {
    @Provides
    @Reusable
    fun provideFindChildInteractor(repository: Lazy<Repository>): FindChildInteractorAbstractFactory = FindChildInteractorFactory(repository)
}

@Module
class GetLastSearchResultsInteractorModule {
    @Provides
    @Reusable
    fun provideGetLastSearchResultsInteractor(repository: Lazy<Repository>): Interactor<Flowable<List<Pair<DomainUrlChild, Int>>>> = GetLastSearchResultsInteractor(repository)
}
