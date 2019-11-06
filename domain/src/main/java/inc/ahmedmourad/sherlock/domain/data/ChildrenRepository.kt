package inc.ahmedmourad.sherlock.domain.data

import inc.ahmedmourad.sherlock.domain.filter.Filter
import inc.ahmedmourad.sherlock.domain.filter.criteria.DomainChildCriteriaRules
import inc.ahmedmourad.sherlock.domain.model.DomainPublishedChild
import inc.ahmedmourad.sherlock.domain.model.DomainRetrievedChild
import inc.ahmedmourad.sherlock.domain.model.DomainSimpleRetrievedChild
import inc.ahmedmourad.sherlock.domain.model.Either
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

interface ChildrenRepository {

    fun publish(child: DomainPublishedChild): Single<DomainRetrievedChild>

    fun find(child: DomainSimpleRetrievedChild): Flowable<Either<Pair<DomainRetrievedChild, Int>?, Throwable>>

    fun findAll(rules: DomainChildCriteriaRules, filter: Filter<DomainRetrievedChild>): Flowable<Either<List<Pair<DomainSimpleRetrievedChild, Int>>, Throwable>>

    fun findLastSearchResults(): Flowable<List<Pair<DomainSimpleRetrievedChild, Int>>>

    fun test(): Tester

    interface Tester {
        fun clear(): Completable
    }
}
