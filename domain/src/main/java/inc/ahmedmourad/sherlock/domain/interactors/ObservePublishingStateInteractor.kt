package inc.ahmedmourad.sherlock.domain.interactors

import dagger.Lazy
import inc.ahmedmourad.sherlock.domain.bus.Bus
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable

typealias ObservePublishingStateInteractor = () -> @JvmSuppressWildcards Flowable<Bus.PublishingState<*>>

internal fun observePublishingState(bus: Lazy<Bus>): Flowable<Bus.PublishingState<*>> {
    return bus.get().childPublishingState.get().toFlowable(BackpressureStrategy.LATEST)
}
