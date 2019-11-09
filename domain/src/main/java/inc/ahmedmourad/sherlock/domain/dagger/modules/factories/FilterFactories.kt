package inc.ahmedmourad.sherlock.domain.dagger.modules.factories

import dagger.Lazy
import inc.ahmedmourad.sherlock.domain.filter.Filter
import inc.ahmedmourad.sherlock.domain.filter.ResultsFilter
import inc.ahmedmourad.sherlock.domain.filter.criteria.Criteria
import inc.ahmedmourad.sherlock.domain.filter.criteria.DomainChildCriteriaRules
import inc.ahmedmourad.sherlock.domain.filter.criteria.LooseCriteria
import inc.ahmedmourad.sherlock.domain.model.DomainRetrievedChild
import inc.ahmedmourad.sherlock.domain.platform.LocationManager

typealias ChildrenFilterFactory =
        (@JvmSuppressWildcards DomainChildCriteriaRules) -> @JvmSuppressWildcards Filter<DomainRetrievedChild>

internal fun childrenFilterFactory(
        criteriaFactory: ChildrenCriteriaFactory,
        rules: DomainChildCriteriaRules
): Filter<DomainRetrievedChild> {
    return ResultsFilter(criteriaFactory(rules))
}

typealias ChildrenCriteriaFactory =
        (@JvmSuppressWildcards DomainChildCriteriaRules) -> @JvmSuppressWildcards Criteria<DomainRetrievedChild>

internal fun childrenLooseCriteriaFactory(
        locationManager: Lazy<LocationManager>,
        rules: DomainChildCriteriaRules
): Criteria<DomainRetrievedChild> {
    return LooseCriteria(rules, locationManager)
}
