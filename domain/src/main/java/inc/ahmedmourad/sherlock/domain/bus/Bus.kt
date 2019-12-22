package inc.ahmedmourad.sherlock.domain.bus

import inc.ahmedmourad.sherlock.domain.model.DomainPublishedChild
import inc.ahmedmourad.sherlock.domain.model.DomainRetrievedChild
import io.reactivex.Observable

//TODO: make internal and all interactions should happen through interactors
interface Bus {

    val childPublishingState: Event<PublishingState<*>>
    val childFindingState: Event<BackgroundState>
    val childrenFindingState: Event<BackgroundState>

    sealed class PublishingState<T> {

        abstract val child: T

        data class Success(override val child: DomainRetrievedChild) : PublishingState<DomainRetrievedChild>()
        data class Ongoing(override val child: DomainPublishedChild) : PublishingState<DomainPublishedChild>()
        data class Failure(override val child: DomainPublishedChild) : PublishingState<DomainPublishedChild>()
    }

    enum class BackgroundState {
        FAILURE, ONGOING, SUCCESS
    }
}

//TODO: consider getting rid of this
interface Event<T> {
    //TODO: replace with blocking last or something
    val lastValue: T?

    fun accept(msg: T)
    fun get(): Observable<T>
}
