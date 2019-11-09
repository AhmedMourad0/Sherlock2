package inc.ahmedmourad.sherlock.dagger.modules

import arrow.syntax.function.partially1
import dagger.Module
import dagger.Provides
import dagger.Reusable
import inc.ahmedmourad.sherlock.dagger.modules.factories.*
import inc.ahmedmourad.sherlock.dagger.modules.qualifiers.AddChildControllerIntentQualifier
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
    fun provideAddChildController(): AddChildControllerFactory {
        return ::addChildControllerFactory
    }

    @Provides
    @Reusable
    @AddChildControllerIntentQualifier
    @JvmStatic
    fun provideAddChildControllerIntent(activityFactory: MainActivityIntentFactory): AddChildControllerIntentFactory {
        return ::addChildControllerIntentFactory.partially1(activityFactory)
    }
}

@Module
internal object DisplayChildControllerModule {

    @Provides
    @Reusable
    @JvmStatic
    fun provideDisplayChildController(): DisplayChildControllerFactory {
        return ::displayChildControllerFactory
    }

    @Provides
    @Reusable
    @JvmStatic
    fun provideDisplayChildControllerIntent(activityFactory: MainActivityIntentFactory): DisplayChildControllerIntentFactory {
        return ::displayChildControllerIntentFactory.partially1(activityFactory)
    }
}

@Module
internal object FindChildrenControllerModule {
    @Provides
    @FindChildrenControllerQualifier
    @JvmStatic
    fun provideFindChildrenController(): TaggedController {
        return FindChildrenController.newInstance()
    }
}

@Module
internal object HomeControllerModule {
    @Provides
    @HomeControllerQualifier
    @JvmStatic
    fun provideHomeController(): TaggedController {
        return HomeController.newInstance()
    }
}

@Module
internal object SearchResultsControllerModule {
    @Provides
    @Reusable
    @JvmStatic
    fun provideSearchResultsController(): SearchResultsControllerFactory {
        return ::searchResultsControllerFactory
    }
}
