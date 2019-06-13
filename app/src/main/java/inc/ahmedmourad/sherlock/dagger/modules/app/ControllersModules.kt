package inc.ahmedmourad.sherlock.dagger.modules.app

import dagger.Module
import dagger.Provides
import dagger.Reusable
import inc.ahmedmourad.sherlock.dagger.modules.app.factories.DisplayChildControllerFactory
import inc.ahmedmourad.sherlock.dagger.modules.app.factories.SearchResultsControllerFactory
import inc.ahmedmourad.sherlock.view.controllers.AddChildController
import inc.ahmedmourad.sherlock.view.controllers.FindChildrenController
import inc.ahmedmourad.sherlock.view.controllers.HomeController

@Module
class AddChildControllerModule {
    @Provides
    fun provideAddChildController() = AddChildController.newInstance()
}

@Module
class DisplayChildControllerModule {
    @Provides
    @Reusable
    fun provideDisplayChildController() = DisplayChildControllerFactory()
}

@Module
class FindChildrenControllerModule {
    @Provides
    fun provideFindChildrenController() = FindChildrenController.newInstance()
}

@Module
class HomeControllerModule {
    @Provides
    fun provideHomeController() = HomeController.newInstance()
}

@Module
class SearchResultsControllerModule {
    @Provides
    @Reusable
    fun provideSearchResultsController() = SearchResultsControllerFactory()
}
