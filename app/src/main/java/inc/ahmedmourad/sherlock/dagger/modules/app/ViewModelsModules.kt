package inc.ahmedmourad.sherlock.dagger.modules.app

import androidx.lifecycle.ViewModelProvider
import dagger.Lazy
import dagger.Module
import dagger.Provides
import dagger.Reusable
import inc.ahmedmourad.sherlock.dagger.modules.app.factories.*
import inc.ahmedmourad.sherlock.dagger.modules.app.qualifiers.AddChildViewModelQualifier
import inc.ahmedmourad.sherlock.dagger.modules.app.qualifiers.FindChildrenViewModelQualifier
import inc.ahmedmourad.sherlock.dagger.modules.domain.factories.FilterAbstractFactory
import inc.ahmedmourad.sherlock.dagger.modules.domain.factories.FindChildInteractorAbstractFactory
import inc.ahmedmourad.sherlock.dagger.modules.domain.factories.FindChildrenInteractorAbstractFactory

@Module
class AddChildViewModelModule {
    @Provides
    @Reusable
    @AddChildViewModelQualifier
    fun provideAddChildViewModel(intentServiceFactory: Lazy<SherlockIntentServiceAbstractFactory>): ViewModelProvider.NewInstanceFactory = AddChildViewModelFactory(intentServiceFactory)
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
    fun provideSearchResultViewModel(
            interactor: FindChildrenInteractorAbstractFactory,
            filterFactory: FilterAbstractFactory
    ): SearchResultsViewModelFactoryAbstractFactory = SearchResultsViewModelFactoryFactory(interactor, filterFactory)
}

@Module
class DisplayChildViewModelModule {
    @Provides
    @Reusable
    fun provideDisplayChildViewModel(interactor: FindChildInteractorAbstractFactory): DisplayChildViewModelFactoryAbstractFactory = DisplayChildViewModelFactoryFactory(interactor)
}
