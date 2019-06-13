package inc.ahmedmourad.sherlock.dagger.modules.app

import dagger.Module
import dagger.Provides
import dagger.Reusable
import inc.ahmedmourad.sherlock.dagger.modules.app.factories.AddChildViewModelFactory
import inc.ahmedmourad.sherlock.dagger.modules.app.factories.FindChildrenViewModelFactory
import inc.ahmedmourad.sherlock.dagger.modules.app.factories.SearchResultsViewModelFactoryFactory
import inc.ahmedmourad.sherlock.dagger.modules.domain.factories.FindChildrenInteractorAbstractFactory

@Module
class AddChildViewModelModule {
    @Provides
    @Reusable
    fun provideAddChildViewModel() = AddChildViewModelFactory()
}

@Module
class FindChildrenViewModelModule {
    @Provides
    @Reusable
    fun provideFindChildrenViewModel() = FindChildrenViewModelFactory()
}

@Module
class SearchResultsViewModelModule {
    @Provides
    @Reusable
    fun provideSearchResultViewModel(interactor: FindChildrenInteractorAbstractFactory) = SearchResultsViewModelFactoryFactory(interactor)
}
