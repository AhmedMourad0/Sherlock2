package inc.ahmedmourad.sherlock.dagger.modules

import com.bluelinelabs.conductor.Controller
import dagger.Module
import dagger.Provides
import dagger.Reusable
import inc.ahmedmourad.sherlock.dagger.modules.factories.*
import inc.ahmedmourad.sherlock.dagger.modules.qualifiers.FindChildrenControllerQualifier
import inc.ahmedmourad.sherlock.dagger.modules.qualifiers.HomeControllerQualifier
import inc.ahmedmourad.sherlock.view.controllers.FindChildrenController
import inc.ahmedmourad.sherlock.view.controllers.HomeController
import inc.ahmedmourad.sherlock.view.model.TaggedController

@Module
internal object AddChildControllerModule {
    @Provides
    @Reusable
    @JvmStatic
    fun provideAddChildController(activityFactory: MainActivityAbstractFactory): AddChildControllerAbstractFactory = AddChildControllerFactory(activityFactory)
}

@Module
internal object DisplayChildControllerModule {
    @Provides
    @Reusable
    @JvmStatic
    fun provideDisplayChildController(activityFactory: MainActivityAbstractFactory): DisplayChildControllerAbstractFactory = DisplayChildControllerFactory(activityFactory)
}

@Module
internal object FindChildrenControllerModule {
    @Provides
    @FindChildrenControllerQualifier
    @JvmStatic
    fun provideFindChildrenController(): TaggedController<Controller> = FindChildrenController.newInstance()
}

@Module
internal object HomeControllerModule {
    @Provides
    @HomeControllerQualifier
    @JvmStatic
    fun provideHomeController(): TaggedController<Controller> = HomeController.newInstance()
}

@Module
internal object SearchResultsControllerModule {
    @Provides
    @Reusable
    @JvmStatic
    fun provideSearchResultsController(): SearchResultsControllerAbstractFactory = SearchResultsControllerFactory()
}
