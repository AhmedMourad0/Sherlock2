package inc.ahmedmourad.sherlock.domain.bus

import com.jakewharton.rxrelay2.PublishRelay
import com.jakewharton.rxrelay2.Relay
import inc.ahmedmourad.sherlock.domain.bus.Bus.Event
import inc.ahmedmourad.sherlock.domain.bus.Bus.PublishingState

class RxBus : Bus {

    override val publishingState = RxEvent<PublishingState>()

    class RxEvent<T>(
            private val notify: (Relay<T>, T) -> Unit = { relay, value -> relay.accept(value) },
            private val create: () -> Relay<T> = { PublishRelay.create() }) : Event<T> {

        private val relay by lazy { create.invoke() }

        override var lastValue: T? = null
            private set

        override fun notify(msg: T) = notify.invoke(relay, msg).also { lastValue = msg }

        override fun get() = relay
    }
}
