package inc.ahmedmourad.sherlock.dagger.components

import dagger.Component
import inc.ahmedmourad.sherlock.dagger.modules.app.*
import inc.ahmedmourad.sherlock.dagger.modules.data.FirebaseCloudRepositoryModule
import inc.ahmedmourad.sherlock.dagger.modules.data.RoomLocalRepositoryModule
import inc.ahmedmourad.sherlock.dagger.modules.data.SherlockDatabaseModule
import inc.ahmedmourad.sherlock.dagger.modules.data.SherlockRepositoryModule
import inc.ahmedmourad.sherlock.dagger.modules.device.AndroidDateManagerModule
import inc.ahmedmourad.sherlock.dagger.modules.device.AndroidLocationManagerModule
import inc.ahmedmourad.sherlock.dagger.modules.device.AndroidTextManagerModule
import inc.ahmedmourad.sherlock.dagger.modules.domain.*
import javax.inject.Singleton

@Component(modules = [AddChildInteractorModule::class,
    FindChildrenInteractorModule::class,
    GetLastSearchResultsInteractorModule::class,
    AndroidDateManagerModule::class,
    AndroidLocationManagerModule::class,
    AndroidTextManagerModule::class,
    TextFormatterModule::class,
    FirebaseCloudRepositoryModule::class,
    RoomLocalRepositoryModule::class,
    SherlockRepositoryModule::class,
    SherlockDatabaseModule::class,
    RxBusModule::class,
    PublishingStateProviderModule::class,
    ResultsRecyclerAdapterModule::class,
    SectionsRecyclerAdapterModule::class,
    SherlockIntentServiceModule::class,
    DisplayChildControllerModule::class,
    SearchResultsControllerModule::class,
    ResultsRemoteViewsServiceModule::class,
    ResultsRemoteViewsFactoryModule::class,
    AddChildViewModelModule::class,
    FindChildrenViewModelModule::class,
    SearchResultsViewModelModule::class,
    CriteriaModule::class,
    ResultsFilterModule::class
])
@Singleton
interface AppComponent {

    fun plusMainActivityComponent(): MainActivityComponent

    fun plusHomeControllerComponent(): HomeComponent

    fun plusAddChildControllerComponent(): AddChildComponent

    fun plusDisplayChildControllerComponent(): DisplayChildComponent

    fun plusFindChildrenControllerComponent(): FindChildrenComponent

    fun plusSearchResultsControllerComponent(): SearchResultsComponent

    fun plusSearchResultsViewModelComponent(): SearchResultsViewModelComponent

    fun plusAddChildViewModelComponent(): AddChildViewModelComponent

    fun plusSherlockIntentServiceComponent(): SherlockIntentServiceComponent

    fun plusResultsRemoteViewsFactoryComponent(): ResultsRemoteViewsFactoryComponent

    fun plusResultsRemoteViewsServiceComponent(): ResultsRemoteViewsServiceComponent

    fun plusAppWidgetComponent(): AppWidgetComponent
}
