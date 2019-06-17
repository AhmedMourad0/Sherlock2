package inc.ahmedmourad.sherlock.dagger.modules.app

import androidx.lifecycle.ViewModelProvider
import dagger.Module
import dagger.Provides
import dagger.Reusable
import inc.ahmedmourad.sherlock.dagger.modules.app.factories.AddChildViewModelFactory
import inc.ahmedmourad.sherlock.dagger.modules.app.factories.FindChildrenViewModelFactory
import inc.ahmedmourad.sherlock.dagger.modules.app.factories.SearchResultsViewModelFactoryAbstractFactory
import inc.ahmedmourad.sherlock.dagger.modules.app.factories.SearchResultsViewModelFactoryFactory
import inc.ahmedmourad.sherlock.dagger.modules.app.qualifiers.AddChildViewModelQualifier
import inc.ahmedmourad.sherlock.dagger.modules.app.qualifiers.FindChildrenViewModelQualifier
import inc.ahmedmourad.sherlock.dagger.modules.domain.factories.FindChildrenInteractorAbstractFactory

@Module
class AddChildViewModelModule {
    @Provides
    @Reusable
    @AddChildViewModelQualifier
    fun provideAddChildViewModel(): ViewModelProvider.NewInstanceFactory = AddChildViewModelFactory()
}

@Module
class FindChildrenViewModelModule {
    @Provides
    @Reusable
    @FindChildrenViewModelQualifier
    fun provideFindChildrenViewModel(): ViewModelProvider.NewInstanceFactory = FindChildrenViewModelFactory()
}

@Module
class SearchResultsViewModelModule {
    @Provides
    @Reusable
    fun provideSearchResultViewModel(interactor: FindChildrenInteractorAbstractFactory): SearchResultsViewModelFactoryAbstractFactory = SearchResultsViewModelFactoryFactory(interactor)
}
