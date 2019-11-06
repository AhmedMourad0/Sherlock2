package inc.ahmedmourad.sherlock.domain.bus

import com.jakewharton.rxrelay2.BehaviorRelay
import com.jakewharton.rxrelay2.Relay
import inc.ahmedmourad.sherlock.domain.bus.Bus.BackgroundState
import inc.ahmedmourad.sherlock.domain.bus.Bus.PublishingState

internal class RxBus : Bus {
    //TODO: maybe all of them should also become behaviour relays
    override val childPublishingState = RxEvent<PublishingState<*>>()
    override val childFindingState = RxEvent<BackgroundState>()
    override val childrenFindingState = RxEvent<BackgroundState>()
}

internal class RxEvent<T>(private val create: () -> Relay<T> = { BehaviorRelay.create() }) : Event<T> {

    private val relay by lazy { create.invoke() }

    override var lastValue: T? = null
        private set

    override fun accept(msg: T) = relay.accept(msg).also { lastValue = msg }

    override fun get() = relay
}
