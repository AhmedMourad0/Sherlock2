package inc.ahmedmourad.sherlock.idling

import androidx.test.espresso.IdlingResource
import androidx.test.espresso.IdlingResource.ResourceCallback
import inc.ahmedmourad.sherlock.domain.bus.Bus
import inc.ahmedmourad.sherlock.domain.bus.Event
import inc.ahmedmourad.sherlock.domain.model.disposable
import timber.log.Timber

abstract class EventIdlingResource<T>(
        event: Event<T>,
        private val name: String) : IdlingResource {

    @Volatile
    private var resourceCallback: ResourceCallback? = null

    private var isIdle = true

    private var eventDisposable by disposable()

    init {
        eventDisposable = event.get().subscribe({
            isIdle = isIdleEvent(it)
        }, {
            Timber.e(it)
            isIdle = true
        })
    }


    abstract fun isIdleEvent(item: T): Boolean

    override fun getName(): String {
        return "${BackgroundEventIdlingResource::class.java.name} - $name"
    }

    override fun isIdleNow(): Boolean {
        return isIdle.also {
            if (it)
                resourceCallback?.onTransitionToIdle()
        }
    }

    override fun registerIdleTransitionCallback(resourceCallback: ResourceCallback) {
        this.resourceCallback = resourceCallback
    }

    fun dispose() {
        isIdle = true
        eventDisposable?.dispose()
    }
}

class BackgroundEventIdlingResource(event: Event<Bus.BackgroundState>, name: String) : EventIdlingResource<Bus.BackgroundState>(event, name) {
    override fun isIdleEvent(item: Bus.BackgroundState) = item != Bus.BackgroundState.ONGOING
}

class PublishingEventIdlingResource(event: Event<Bus.PublishingState<*>>, name: String) : EventIdlingResource<Bus.PublishingState<*>>(event, name) {
    override fun isIdleEvent(item: Bus.PublishingState<*>) = item !is Bus.PublishingState.Ongoing
}

fun Event<Bus.BackgroundState>.toIdlingResource(name: String): BackgroundEventIdlingResource {
    return BackgroundEventIdlingResource(this, name)
}

fun Event<Bus.PublishingState<*>>.toIdlingResource(name: String): PublishingEventIdlingResource {
    return PublishingEventIdlingResource(this, name)
}
