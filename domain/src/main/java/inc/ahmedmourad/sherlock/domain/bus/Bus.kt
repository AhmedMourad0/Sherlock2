package inc.ahmedmourad.sherlock.domain.bus

import io.reactivex.Observable

interface Bus {

    val publishingState: Event<PublishingState>

    enum class PublishingState {
        FAILURE, ONGOING, SUCCESS
    }

    interface Event<T> {
        val lastValue: T?
        fun notify(msg: T)
        fun get(): Observable<T>
    }
}
