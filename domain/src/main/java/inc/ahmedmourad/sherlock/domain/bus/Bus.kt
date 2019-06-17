package inc.ahmedmourad.sherlock.domain.bus

import dagger.Lazy
import inc.ahmedmourad.sherlock.domain.device.TextManager
import io.reactivex.Observable

interface Bus {

    val state: State
    val errors: Errors
    val widget: Widget

    interface State {
        val foregroundState: Event<ForegroundState>
        val backgroundState: Event<BackgroundState>
    }

    interface Errors {
        val normalErrors: Event<NormalError>
        val retriableErrors: Event<RetriableError>
        val recoverableErrors: Event<RecoverableError>
    }

    interface Widget {
        val retriableErrors: Event<RetriableError>
    }

    enum class ForegroundState {
        IDLE, LOADING, ERROR
    }

    interface BackgroundState {
        val message: String
        val isIndefinite: Boolean
    }

    class PublishingState(override val message: String, override val isIndefinite: Boolean) : BackgroundState {

        interface Provider {
            fun failure(): PublishingState
            fun ongoing(): PublishingState
            fun success(): PublishingState
        }

        class TextManagerProvider(private val textManager: Lazy<TextManager>) : Provider {
            override fun failure(): PublishingState = PublishingState(textManager.get().somethingWentWrong(), false)
            override fun ongoing(): PublishingState = PublishingState(textManager.get().publishing(), true)
            override fun success(): PublishingState = PublishingState(textManager.get().publishedSuccessfully(), false)
        }
    }

    class NormalError(message: String, throwable: Throwable) : Exception(message, throwable)

    class RetriableError(message: String, private val throwable: Throwable, private val retry: (Throwable) -> Unit) : Exception(message, throwable) {
        fun retry() = retry(throwable)
    }

    class RecoverableError(message: String, private val throwable: Throwable, private val recover: (Throwable) -> Unit) : Exception(message, throwable) {
        fun recover() = recover(throwable)
    }

    interface Event<T> {
        fun notify(msg: T)
        fun get(): Observable<T>
    }
}
