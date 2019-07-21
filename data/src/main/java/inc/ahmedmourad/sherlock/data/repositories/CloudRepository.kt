package inc.ahmedmourad.sherlock.data.repositories

import inc.ahmedmourad.sherlock.domain.filter.Filter
import inc.ahmedmourad.sherlock.domain.filter.criteria.DomainChildCriteriaRules
import inc.ahmedmourad.sherlock.domain.model.DomainPictureChild
import inc.ahmedmourad.sherlock.domain.model.DomainUrlChild
import inc.ahmedmourad.sherlock.domain.model.Optional
import io.reactivex.Flowable
import io.reactivex.Single

interface CloudRepository {

    fun publish(domainChild: DomainPictureChild): Single<DomainUrlChild>

    fun find(childId: String): Flowable<Optional<DomainUrlChild>>

    fun findAll(rules: DomainChildCriteriaRules, filter: Filter<DomainUrlChild>): Flowable<List<Pair<DomainUrlChild, Int>>>
}
