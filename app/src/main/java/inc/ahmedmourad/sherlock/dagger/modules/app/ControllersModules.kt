package inc.ahmedmourad.sherlock.dagger.modules.app

import dagger.Module
import dagger.Provides
import dagger.Reusable
import inc.ahmedmourad.sherlock.dagger.modules.app.factories.*
import inc.ahmedmourad.sherlock.dagger.modules.app.qualifiers.FindChildrenControllerQualifier
import inc.ahmedmourad.sherlock.dagger.modules.app.qualifiers.HomeControllerQualifier
import inc.ahmedmourad.sherlock.view.controllers.FindChildrenController
import inc.ahmedmourad.sherlock.view.controllers.HomeController
import inc.ahmedmourad.sherlock.view.model.TaggedController

@Module
class AddChildControllerModule {
    @Provides
    @Reusable
    fun provideAddChildController(): AddChildControllerAbstractFactory = AddChildControllerFactory()
}

@Module
class DisplayChildControllerModule {
    @Provides
    @Reusable
    fun provideDisplayChildController(): DisplayChildControllerAbstractFactory = DisplayChildControllerFactory()
}

@Module
class FindChildrenControllerModule {
    @Provides
    @FindChildrenControllerQualifier
    fun provideFindChildrenController(): TaggedController = FindChildrenController.newInstance()
}

@Module
class HomeControllerModule {
    @Provides
    @HomeControllerQualifier
    fun provideHomeController(): TaggedController = HomeController.newInstance()
}

@Module
class SearchResultsControllerModule {
    @Provides
    @Reusable
    fun provideSearchResultsController(): SearchResultsControllerAbstractFactory = SearchResultsControllerFactory()
}
