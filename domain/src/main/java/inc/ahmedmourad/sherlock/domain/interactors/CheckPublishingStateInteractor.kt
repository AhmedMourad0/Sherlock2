package inc.ahmedmourad.sherlock.domain.interactors

import dagger.Lazy
import inc.ahmedmourad.sherlock.domain.bus.Bus
import inc.ahmedmourad.sherlock.domain.model.Optional
import inc.ahmedmourad.sherlock.domain.model.asOptional
import io.reactivex.Single

typealias CheckPublishingStateInteractor = () -> @JvmSuppressWildcards Single<Optional<Bus.PublishingState<*>>>

internal fun checkPublishingState(bus: Lazy<Bus>): Single<Optional<Bus.PublishingState<*>>> {
    return bus.get()
            .childPublishingState
            .get()
            .map { it.asOptional() }
            .last(null.asOptional())
}
