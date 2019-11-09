package inc.ahmedmourad.sherlock.domain.interactors

import dagger.Lazy
import inc.ahmedmourad.sherlock.domain.data.ChildrenRepository
import inc.ahmedmourad.sherlock.domain.filter.Filter
import inc.ahmedmourad.sherlock.domain.filter.criteria.DomainChildCriteriaRules
import inc.ahmedmourad.sherlock.domain.model.DomainRetrievedChild
import inc.ahmedmourad.sherlock.domain.model.DomainSimpleRetrievedChild
import inc.ahmedmourad.sherlock.domain.model.Either
import io.reactivex.Flowable

typealias FindChildrenInteractor =
        (@JvmSuppressWildcards DomainChildCriteriaRules, @JvmSuppressWildcards Filter<DomainRetrievedChild>) -> @JvmSuppressWildcards Flowable<Either<List<Pair<DomainSimpleRetrievedChild, Int>>, Throwable>>

internal fun findChildren(
        childrenRepository: Lazy<ChildrenRepository>,
        rules: DomainChildCriteriaRules,
        filter: Filter<DomainRetrievedChild>
): Flowable<Either<List<Pair<DomainSimpleRetrievedChild, Int>>, Throwable>> {
    return childrenRepository.get().findAll(rules, filter)
}
