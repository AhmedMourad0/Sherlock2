package inc.ahmedmourad.sherlock.domain.bus

import com.jakewharton.rxrelay2.PublishRelay
import com.jakewharton.rxrelay2.Relay
import inc.ahmedmourad.sherlock.domain.bus.Bus.*

//TODO: listen for error and state changes
class RxBus : Bus {

    override val state = RxState()
    override val errors = RxError()
    override val widget = RxWidget()

    inner class RxState : State {
        override val backgroundState = RxEvent<BackgroundState>()
        override val foregroundState = RxEvent<ForegroundState>()
    }

    inner class RxError : Errors {

        override val normalErrors = RxEvent<NormalError>({ relay, value ->
            value.printStackTrace()
            relay.accept(value)
        })

        override val retriableErrors = RxEvent<RetriableError>({ relay, value ->
            value.printStackTrace()
            relay.accept(value)
        })

        override val recoverableErrors = RxEvent<RecoverableError>({ relay, value ->
            value.printStackTrace()
            relay.accept(value)
        })
    }

    inner class RxWidget : Widget {
        override val retriableErrors = RxEvent<RetriableError>({ relay, value ->
            value.printStackTrace()
            relay.accept(value)
        })
    }

    class RxEvent<T>(
            private val notify: (Relay<T>, T) -> Unit = { relay, value -> relay.accept(value) },
            private val create: () -> Relay<T> = { PublishRelay.create() }) : Event<T> {

        private val relay by lazy { create.invoke() }

        override fun notify(msg: T) = notify.invoke(relay, msg)

        override fun get() = relay
    }
}
