package inc.ahmedmourad.sherlock.domain.interactors

import dagger.Lazy
import inc.ahmedmourad.sherlock.domain.bus.Bus
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable

internal class ObservePublishingStateInteractor(private val bus: Lazy<Bus>) : Interactor<Flowable<Bus.PublishingState<*>>> {
    override fun execute(): Flowable<Bus.PublishingState<*>> {
        return bus.get().childPublishingState.get().toFlowable(BackpressureStrategy.LATEST)
    }
}
