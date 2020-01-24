package inc.ahmedmourad.sherlock.domain.interactors.children

import arrow.core.Either
import arrow.core.Tuple2
import dagger.Lazy
import inc.ahmedmourad.sherlock.domain.data.ChildrenRepository
import inc.ahmedmourad.sherlock.domain.model.children.DomainRetrievedChild
import inc.ahmedmourad.sherlock.domain.model.children.DomainSimpleRetrievedChild
import io.reactivex.Flowable

typealias FindChildInteractor =
        (@JvmSuppressWildcards DomainSimpleRetrievedChild) ->
        @JvmSuppressWildcards Flowable<Either<Throwable, Tuple2<DomainRetrievedChild, Int?>?>>

internal fun findChild(
        childrenRepository: Lazy<ChildrenRepository>,
        child: DomainSimpleRetrievedChild
): Flowable<Either<Throwable, Tuple2<DomainRetrievedChild, Int?>?>> {
    return childrenRepository.get().find(child)
}
