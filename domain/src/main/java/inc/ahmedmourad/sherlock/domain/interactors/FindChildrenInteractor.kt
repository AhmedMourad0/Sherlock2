package inc.ahmedmourad.sherlock.domain.interactors

import dagger.Lazy
import inc.ahmedmourad.sherlock.domain.data.ChildrenRepository
import inc.ahmedmourad.sherlock.domain.filter.Filter
import inc.ahmedmourad.sherlock.domain.filter.criteria.DomainChildCriteriaRules
import inc.ahmedmourad.sherlock.domain.model.DomainRetrievedChild
import inc.ahmedmourad.sherlock.domain.model.DomainSimpleRetrievedChild
import inc.ahmedmourad.sherlock.domain.model.Either
import io.reactivex.Flowable

internal class FindChildrenInteractor(private val childrenRepository: Lazy<ChildrenRepository>,
                                      private val rules: DomainChildCriteriaRules,
                                      private val filter: Filter<DomainRetrievedChild>) : Interactor<Flowable<Either<List<Pair<DomainSimpleRetrievedChild, Int>>, Throwable>>> {
    override fun execute(): Flowable<Either<List<Pair<DomainSimpleRetrievedChild, Int>>, Throwable>> {
        return childrenRepository.get().findAll(rules, filter)
    }
}
