package inc.ahmedmourad.sherlock.dagger

import inc.ahmedmourad.sherlock.dagger.components.AppComponent
import inc.ahmedmourad.sherlock.dagger.components.DaggerAppComponent

object SherlockComponent {

    private val appComponent: AppComponent = DaggerAppComponent.create()

    object Activities {
        val mainComponent = ComponentProvider(appComponent::plusMainActivityComponent)
    }

    object Controllers {
        val homeComponent = ComponentProvider(appComponent::plusHomeControllerComponent)
        val addChildComponent = ComponentProvider(appComponent::plusAddChildControllerComponent)
        val displayChildComponent = ComponentProvider(appComponent::plusDisplayChildControllerComponent)
        val findChildrenComponent = ComponentProvider(appComponent::plusFindChildrenControllerComponent)
        val searchResultsComponent = ComponentProvider(appComponent::plusSearchResultsControllerComponent)
    }

    object Services {
        val sherlockServiceComponent = ComponentProvider(appComponent::plusSherlockIntentServiceComponent)
    }

    object Widget {
        val resultsRemoteViewsFactoryComponent = ComponentProvider(appComponent::plusResultsRemoteViewsFactoryComponent)
        val resultsRemoteViewsServiceComponent = ComponentProvider(appComponent::plusResultsRemoteViewsServiceComponent)
        val appWidgetComponent = ComponentProvider(appComponent::plusAppWidgetComponent)
    }
}

interface Provider<T> {
    fun get(): T
    fun release()
}

class ComponentProvider<T>(private val createComponent: () -> T) : Provider<T> {

    private var component: T? = null

    override fun get() = component ?: createComponent().also { component = it }

    override fun release() {
        component = null
    }
}
