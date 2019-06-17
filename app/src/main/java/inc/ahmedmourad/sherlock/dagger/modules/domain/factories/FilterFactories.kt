package inc.ahmedmourad.sherlock.dagger.modules.domain.factories

import dagger.Lazy
import inc.ahmedmourad.sherlock.domain.device.LocationManager
import inc.ahmedmourad.sherlock.domain.filter.Filter
import inc.ahmedmourad.sherlock.domain.filter.ResultsFilter
import inc.ahmedmourad.sherlock.domain.filter.criteria.Criteria
import inc.ahmedmourad.sherlock.domain.filter.criteria.DomainChildCriteriaRules
import inc.ahmedmourad.sherlock.domain.filter.criteria.LooseCriteria
import inc.ahmedmourad.sherlock.domain.model.DomainChild

interface FilterAbstractFactory {
    fun <C : DomainChild> create(rules: DomainChildCriteriaRules): Filter<C>
}

class ResultsFilterFactory(private val criteriaFactory: CriteriaAbstractFactory) : FilterAbstractFactory {
    override fun <C : DomainChild> create(rules: DomainChildCriteriaRules): Filter<C> =
            ResultsFilter(criteriaFactory.create(rules))

}

interface CriteriaAbstractFactory {
    fun <C : DomainChild> create(rules: DomainChildCriteriaRules): Criteria<C>
}

class LooseCriteriaFactory(private val locationManager: Lazy<LocationManager>) : CriteriaAbstractFactory {
    override fun <C : DomainChild> create(rules: DomainChildCriteriaRules): Criteria<C> = LooseCriteria(rules, locationManager)
}
