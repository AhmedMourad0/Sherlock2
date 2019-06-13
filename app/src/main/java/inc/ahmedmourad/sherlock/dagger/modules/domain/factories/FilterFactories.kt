package inc.ahmedmourad.sherlock.dagger.modules.domain.factories

import dagger.Lazy
import inc.ahmedmourad.sherlock.domain.device.LocationManager
import inc.ahmedmourad.sherlock.domain.filter.Filter
import inc.ahmedmourad.sherlock.domain.filter.ResultsFilter
import inc.ahmedmourad.sherlock.domain.filter.criteria.Criteria
import inc.ahmedmourad.sherlock.domain.filter.criteria.DomainChildCriteriaRules
import inc.ahmedmourad.sherlock.domain.filter.criteria.LooseCriteria
import inc.ahmedmourad.sherlock.domain.model.DomainChild

interface FilterFactory {
    fun <C : DomainChild, T : Filter<C>> create(rules: DomainChildCriteriaRules): T
}

class ResultsFilterFactory(private val criteriaFactory: CriteriaFactory) : FilterFactory {
    @Suppress("UNCHECKED_CAST")
    override fun <C : DomainChild, T : Filter<C>> create(rules: DomainChildCriteriaRules): T =
            ResultsFilter<C>(criteriaFactory.create(rules)) as T

}

interface CriteriaFactory {
    fun <T : Criteria<*>> create(rules: DomainChildCriteriaRules): T
}

class LooseCriteriaFactory(private val locationManager: Lazy<LocationManager>) : CriteriaFactory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : Criteria<*>> create(rules: DomainChildCriteriaRules): T = LooseCriteria(rules, locationManager) as T
}
