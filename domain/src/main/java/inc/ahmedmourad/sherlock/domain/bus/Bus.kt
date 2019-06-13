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

    sealed class PublishingState(override val message: String, override val isIndefinite: Boolean) : BackgroundState {

        class Failure(textManager: TextManager) : PublishingState(textManager.somethingWentWrong(), false)
        class Ongoing(textManager: TextManager) : PublishingState(textManager.publishing(), true)
        class Success(textManager: TextManager) : PublishingState(textManager.publishedSuccessfully(), false)

        class Provider(private val textManager: Lazy<TextManager>) {
            fun failure(): PublishingState = Failure(textManager.get())
            fun ongoing(): PublishingState = Ongoing(textManager.get())
            fun success(): PublishingState = Success(textManager.get())
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
