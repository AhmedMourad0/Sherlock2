package inc.ahmedmourad.sherlock.domain.interactors

import arrow.core.Option
import arrow.core.none
import arrow.core.some
import dagger.Lazy
import inc.ahmedmourad.sherlock.domain.bus.Bus
import io.reactivex.Single

typealias CheckPublishingStateInteractor = () -> @JvmSuppressWildcards Single<Option<Bus.PublishingState<*>>>

internal fun checkPublishingState(bus: Lazy<Bus>): Single<Option<Bus.PublishingState<*>>> {
    return bus.get()
            .childPublishingState
            .get()
            .map { it.some() }
            .last(none())
}
