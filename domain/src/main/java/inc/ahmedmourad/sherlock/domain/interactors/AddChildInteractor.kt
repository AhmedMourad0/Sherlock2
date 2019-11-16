package inc.ahmedmourad.sherlock.domain.interactors

import arrow.core.Either
import dagger.Lazy
import inc.ahmedmourad.sherlock.domain.data.ChildrenRepository
import inc.ahmedmourad.sherlock.domain.model.DomainPublishedChild
import inc.ahmedmourad.sherlock.domain.model.DomainRetrievedChild
import io.reactivex.Single

typealias AddChildInteractor =
        (@JvmSuppressWildcards DomainPublishedChild) ->
        @JvmSuppressWildcards Single<Either<Throwable, DomainRetrievedChild>>

internal fun addChild(childrenRepository: Lazy<ChildrenRepository>, child: DomainPublishedChild): Single<Either<Throwable, DomainRetrievedChild>> {
    return childrenRepository.get().publish(child)
}
