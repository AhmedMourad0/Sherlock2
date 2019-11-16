package inc.ahmedmourad.sherlock.domain.interactors

import arrow.core.Either
import arrow.core.Option
import arrow.core.Tuple2
import dagger.Lazy
import inc.ahmedmourad.sherlock.domain.data.ChildrenRepository
import inc.ahmedmourad.sherlock.domain.model.DomainRetrievedChild
import inc.ahmedmourad.sherlock.domain.model.DomainSimpleRetrievedChild
import io.reactivex.Flowable

typealias FindChildInteractor =
        (@JvmSuppressWildcards DomainSimpleRetrievedChild) ->
        @JvmSuppressWildcards Flowable<Either<Throwable, Option<Tuple2<DomainRetrievedChild, Int>>>>

internal fun findChild(
        childrenRepository: Lazy<ChildrenRepository>,
        child: DomainSimpleRetrievedChild
): Flowable<Either<Throwable, Option<Tuple2<DomainRetrievedChild, Int>>>> {
    return childrenRepository.get().find(child)
}
