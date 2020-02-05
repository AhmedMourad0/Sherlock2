package inc.ahmedmourad.sherlock.domain.interactors.children

import arrow.core.Either
import arrow.core.Tuple2
import dagger.Lazy
import inc.ahmedmourad.sherlock.domain.data.ChildrenRepository
import inc.ahmedmourad.sherlock.domain.model.children.RetrievedChild
import inc.ahmedmourad.sherlock.domain.model.children.SimpleRetrievedChild
import io.reactivex.Flowable

typealias FindChildInteractor =
        (@JvmSuppressWildcards SimpleRetrievedChild) ->
        @JvmSuppressWildcards Flowable<Either<Throwable, Tuple2<RetrievedChild, Int?>?>>

internal fun findChild(
        childrenRepository: Lazy<ChildrenRepository>,
        child: SimpleRetrievedChild
): Flowable<Either<Throwable, Tuple2<RetrievedChild, Int?>?>> {
    return childrenRepository.get().find(child)
}
