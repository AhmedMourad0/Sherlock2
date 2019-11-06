package inc.ahmedmourad.sherlock.domain.interactors

import dagger.Lazy
import inc.ahmedmourad.sherlock.domain.bus.Bus
import inc.ahmedmourad.sherlock.domain.model.Optional
import inc.ahmedmourad.sherlock.domain.model.asOptional
import io.reactivex.Single

internal class CheckPublishingStateInteractor(private val bus: Lazy<Bus>) : Interactor<Single<Optional<Bus.PublishingState<*>>>> {
    override fun execute(): Single<Optional<Bus.PublishingState<*>>> {
        return bus.get()
                .childPublishingState
                .get()
                .map { it.asOptional() }
                .last(null.asOptional())
    }
}
