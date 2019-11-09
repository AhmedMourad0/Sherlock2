package inc.ahmedmourad.sherlock.domain.interactors

import dagger.Lazy
import inc.ahmedmourad.sherlock.domain.data.ChildrenRepository
import inc.ahmedmourad.sherlock.domain.model.DomainRetrievedChild
import inc.ahmedmourad.sherlock.domain.model.DomainSimpleRetrievedChild
import inc.ahmedmourad.sherlock.domain.model.Either
import io.reactivex.Flowable

typealias FindChildInteractor =
        (@JvmSuppressWildcards DomainSimpleRetrievedChild) -> @JvmSuppressWildcards Flowable<Either<Pair<DomainRetrievedChild, Int>?, Throwable>>

internal fun findChild(
        childrenRepository: Lazy<ChildrenRepository>,
        child: DomainSimpleRetrievedChild
): Flowable<Either<Pair<DomainRetrievedChild, Int>?, Throwable>> {
    return childrenRepository.get().find(child)
}
