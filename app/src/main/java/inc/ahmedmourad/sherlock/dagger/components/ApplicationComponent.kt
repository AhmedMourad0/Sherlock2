package inc.ahmedmourad.sherlock.dagger.components

import dagger.Component
import inc.ahmedmourad.sherlock.auth.dagger.AuthModule
import inc.ahmedmourad.sherlock.children.dagger.ChildrenModule
import inc.ahmedmourad.sherlock.dagger.AppModule
import inc.ahmedmourad.sherlock.domain.dagger.DomainModule
import inc.ahmedmourad.sherlock.platform.dagger.PlatformModule
import javax.inject.Singleton

@Component(modules = [
    AppModule::class,
    DomainModule::class,
    PlatformModule::class,
    ChildrenModule::class,
    AuthModule::class
])
@Singleton
internal interface ApplicationComponent {

    fun plusMainActivityComponent(): MainActivityComponent

    fun plusHomeControllerComponent(): HomeComponent

    fun plusAddChildControllerComponent(): AddChildComponent

    fun plusDisplayChildControllerComponent(): DisplayChildComponent

    fun plusFindChildrenControllerComponent(): FindChildrenComponent

    fun plusSearchResultsControllerComponent(): SearchResultsComponent

    fun plusSherlockIntentServiceComponent(): SherlockIntentServiceComponent

    fun plusResultsRemoteViewsFactoryComponent(): ResultsRemoteViewsFactoryComponent

    fun plusResultsRemoteViewsServiceComponent(): ResultsRemoteViewsServiceComponent

    fun plusAppWidgetComponent(): AppWidgetComponent

    fun plusTestComponent(): TestComponent
}
