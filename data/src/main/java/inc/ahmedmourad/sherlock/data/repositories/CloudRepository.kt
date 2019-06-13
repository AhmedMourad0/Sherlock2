package inc.ahmedmourad.sherlock.data.repositories

import inc.ahmedmourad.sherlock.domain.filter.Filter
import inc.ahmedmourad.sherlock.domain.filter.criteria.DomainChildCriteriaRules
import inc.ahmedmourad.sherlock.domain.model.DomainPictureChild
import inc.ahmedmourad.sherlock.domain.model.DomainUrlChild
import io.reactivex.Single

interface CloudRepository {

    fun publish(domainChild: DomainPictureChild): Single<DomainUrlChild>

    fun find(rules: DomainChildCriteriaRules, filter: Filter<DomainUrlChild>): Single<List<Pair<DomainUrlChild, Int>>>
}
