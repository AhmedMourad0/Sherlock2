package inc.ahmedmourad.sherlock.domain.interactors.children

import arrow.core.Either
import arrow.core.Tuple2
import dagger.Lazy
import inc.ahmedmourad.sherlock.domain.data.ChildrenRepository
import inc.ahmedmourad.sherlock.domain.filter.Filter
import inc.ahmedmourad.sherlock.domain.filter.criteria.DomainChildCriteriaRules
import inc.ahmedmourad.sherlock.domain.model.children.DomainRetrievedChild
import inc.ahmedmourad.sherlock.domain.model.children.DomainSimpleRetrievedChild
import io.reactivex.Flowable

typealias FindChildrenInteractor =
        (@JvmSuppressWildcards DomainChildCriteriaRules, @JvmSuppressWildcards Filter<DomainRetrievedChild>) ->
        @JvmSuppressWildcards Flowable<Either<Throwable, List<Tuple2<DomainSimpleRetrievedChild, Int>>>>

internal fun findChildren(
        childrenRepository: Lazy<ChildrenRepository>,
        rules: DomainChildCriteriaRules,
        filter: Filter<DomainRetrievedChild>
): Flowable<Either<Throwable, List<Tuple2<DomainSimpleRetrievedChild, Int>>>> {
    return childrenRepository.get().findAll(rules, filter)
}
