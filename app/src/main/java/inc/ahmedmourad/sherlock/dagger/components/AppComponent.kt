package inc.ahmedmourad.sherlock.dagger.components

import dagger.Component
import inc.ahmedmourad.sherlock.dagger.modules.app.*
import inc.ahmedmourad.sherlock.dagger.modules.data.*
import inc.ahmedmourad.sherlock.dagger.modules.device.AndroidDateManagerModule
import inc.ahmedmourad.sherlock.dagger.modules.device.AndroidLocationManagerModule
import inc.ahmedmourad.sherlock.dagger.modules.domain.*
import javax.inject.Singleton

@Component(modules = [
    AddChildInteractorModule::class,
    FindChildrenInteractorModule::class,
    FindChildInteractorModule::class,
    GetLastSearchResultsInteractorModule::class,
    AndroidDateManagerModule::class,
    AndroidLocationManagerModule::class,
    TextFormatterModule::class,
    FirebaseFirestoreCloudRepositoryModule::class,
    RoomLocalRepositoryModule::class,
    SherlockRepositoryModule::class,
    SherlockDatabaseModule::class,
    FirebaseFirestoreModule::class,
    FirebaseDatabaseModule::class,
    FirebaseStorageModule::class,
    RxBusModule::class,
    ResultsRecyclerAdapterModule::class,
    SectionsRecyclerAdapterModule::class,
    SherlockIntentServiceModule::class,
    AddChildControllerModule::class,
    DisplayChildControllerModule::class,
    SearchResultsControllerModule::class,
    ResultsRemoteViewsServiceModule::class,
    ResultsRemoteViewsFactoryModule::class,
    AddChildViewModelModule::class,
    FindChildrenViewModelModule::class,
    SearchResultsViewModelModule::class,
    DisplayChildViewModelModule::class,
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

    fun plusSherlockIntentServiceComponent(): SherlockIntentServiceComponent

    fun plusResultsRemoteViewsFactoryComponent(): ResultsRemoteViewsFactoryComponent

    fun plusResultsRemoteViewsServiceComponent(): ResultsRemoteViewsServiceComponent

    fun plusAppWidgetComponent(): AppWidgetComponent
}
