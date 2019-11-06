package inc.ahmedmourad.sherlock.dagger

import inc.ahmedmourad.sherlock.dagger.components.ApplicationComponent
import inc.ahmedmourad.sherlock.dagger.components.DaggerApplicationComponent

internal object SherlockComponent {

    private val applicationComponent: ApplicationComponent = DaggerApplicationComponent.create()

    object Activities {
        val mainComponent = ComponentProvider(applicationComponent::plusMainActivityComponent)
    }

    object Controllers {
        val homeComponent = ComponentProvider(applicationComponent::plusHomeControllerComponent)
        val addChildComponent = ComponentProvider(applicationComponent::plusAddChildControllerComponent)
        val displayChildComponent = ComponentProvider(applicationComponent::plusDisplayChildControllerComponent)
        val findChildrenComponent = ComponentProvider(applicationComponent::plusFindChildrenControllerComponent)
        val searchResultsComponent = ComponentProvider(applicationComponent::plusSearchResultsControllerComponent)
    }

    object Services {
        val sherlockServiceComponent = ComponentProvider(applicationComponent::plusSherlockIntentServiceComponent)
    }

    object Widget {
        val resultsRemoteViewsFactoryComponent = ComponentProvider(applicationComponent::plusResultsRemoteViewsFactoryComponent)
        val resultsRemoteViewsServiceComponent = ComponentProvider(applicationComponent::plusResultsRemoteViewsServiceComponent)
        val appWidgetComponent = ComponentProvider(applicationComponent::plusAppWidgetComponent)
    }

    object Test {
        val testComponent = ComponentProvider(applicationComponent::plusTestComponent)
    }
}

internal interface Provider<T> {
    fun get(): T
    fun release()
}

internal class ComponentProvider<T>(private val createComponent: () -> T) : Provider<T> {

    private var component: T? = null

    override fun get() = component ?: createComponent().also { component = it }

    override fun release() {
        component = null
    }
}
