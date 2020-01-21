package inc.ahmedmourad.sherlock.domain.interactors.children

import arrow.core.Either
import dagger.Lazy
import inc.ahmedmourad.sherlock.domain.data.ChildrenRepository
import inc.ahmedmourad.sherlock.domain.model.children.DomainPublishedChild
import inc.ahmedmourad.sherlock.domain.model.children.DomainRetrievedChild
import io.reactivex.Single

typealias AddChildInteractor =
        (@JvmSuppressWildcards DomainPublishedChild) ->
        @JvmSuppressWildcards Single<Either<Throwable, DomainRetrievedChild>>

internal fun addChild(childrenRepository: Lazy<ChildrenRepository>, child: DomainPublishedChild): Single<Either<Throwable, DomainRetrievedChild>> {
    return childrenRepository.get().publish(child)
}
