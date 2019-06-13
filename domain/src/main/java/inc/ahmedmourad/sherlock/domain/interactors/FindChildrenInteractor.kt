package inc.ahmedmourad.sherlock.domain.interactors

import dagger.Lazy
import inc.ahmedmourad.sherlock.domain.filter.Filter
import inc.ahmedmourad.sherlock.domain.filter.criteria.DomainChildCriteriaRules
import inc.ahmedmourad.sherlock.domain.model.DomainRefreshableResults
import inc.ahmedmourad.sherlock.domain.model.DomainUrlChild
import inc.ahmedmourad.sherlock.domain.repository.Repository

class FindChildrenInteractor(private val repository: Lazy<Repository>,
                             private val rules: DomainChildCriteriaRules,
                             private val filter: Filter<DomainUrlChild>) : Interactor<DomainRefreshableResults> {
    override fun execute(): DomainRefreshableResults {
        return repository.get().find(rules, filter)
    }
}
