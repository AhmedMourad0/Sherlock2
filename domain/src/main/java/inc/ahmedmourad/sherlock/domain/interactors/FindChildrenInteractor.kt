package inc.ahmedmourad.sherlock.domain.interactors

import dagger.Lazy
import inc.ahmedmourad.sherlock.domain.filter.Filter
import inc.ahmedmourad.sherlock.domain.filter.criteria.DomainChildCriteriaRules
import inc.ahmedmourad.sherlock.domain.model.DomainUrlChild
import inc.ahmedmourad.sherlock.domain.repository.Repository
import io.reactivex.Flowable

class FindChildrenInteractor(private val repository: Lazy<Repository>,
                             private val rules: DomainChildCriteriaRules,
                             private val filter: Filter<DomainUrlChild>) : Interactor<Flowable<List<Pair<DomainUrlChild, Int>>>> {
    override fun execute(): Flowable<List<Pair<DomainUrlChild, Int>>> {
        return repository.get().find(rules, filter)
    }
}
