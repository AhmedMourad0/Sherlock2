package inc.ahmedmourad.sherlock.dagger.modules.app

import com.bluelinelabs.conductor.Controller
import dagger.Module
import dagger.Provides
import dagger.Reusable
import inc.ahmedmourad.sherlock.dagger.modules.app.factories.DisplayChildControllerAbstractFactory
import inc.ahmedmourad.sherlock.dagger.modules.app.factories.DisplayChildControllerFactory
import inc.ahmedmourad.sherlock.dagger.modules.app.factories.SearchResultsControllerAbstractFactory
import inc.ahmedmourad.sherlock.dagger.modules.app.factories.SearchResultsControllerFactory
import inc.ahmedmourad.sherlock.dagger.modules.app.qualifiers.AddChildControllerQualifier
import inc.ahmedmourad.sherlock.dagger.modules.app.qualifiers.FindChildrenControllerQualifier
import inc.ahmedmourad.sherlock.dagger.modules.app.qualifiers.HomeControllerQualifier
import inc.ahmedmourad.sherlock.view.controllers.AddChildController
import inc.ahmedmourad.sherlock.view.controllers.FindChildrenController
import inc.ahmedmourad.sherlock.view.controllers.HomeController

@Module
class AddChildControllerModule {
    @Provides
    @AddChildControllerQualifier
    fun provideAddChildController(): Controller = AddChildController.newInstance()
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
    fun provideFindChildrenController(): Controller = FindChildrenController.newInstance()
}

@Module
class HomeControllerModule {
    @Provides
    @HomeControllerQualifier
    fun provideHomeController(): Controller = HomeController.newInstance()
}

@Module
class SearchResultsControllerModule {
    @Provides
    @Reusable
    fun provideSearchResultsController(): SearchResultsControllerAbstractFactory = SearchResultsControllerFactory()
}
