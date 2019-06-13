package inc.ahmedmourad.sherlock.domain.repository

import inc.ahmedmourad.sherlock.domain.filter.Filter
import inc.ahmedmourad.sherlock.domain.filter.criteria.DomainChildCriteriaRules
import inc.ahmedmourad.sherlock.domain.model.DomainPictureChild
import inc.ahmedmourad.sherlock.domain.model.DomainRefreshableResults
import inc.ahmedmourad.sherlock.domain.model.DomainUrlChild
import io.reactivex.Flowable
import io.reactivex.Single

interface Repository {

    fun publish(domainChild: DomainPictureChild): Single<DomainUrlChild>

    fun find(rules: DomainChildCriteriaRules, filter: Filter<DomainUrlChild>): DomainRefreshableResults

    fun getLastSearchResults(): Flowable<List<Pair<DomainUrlChild, Int>>>
}
