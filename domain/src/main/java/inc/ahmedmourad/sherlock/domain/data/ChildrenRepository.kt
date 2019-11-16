package inc.ahmedmourad.sherlock.domain.data

import arrow.core.Either
import arrow.core.Option
import arrow.core.Tuple2
import inc.ahmedmourad.sherlock.domain.filter.Filter
import inc.ahmedmourad.sherlock.domain.filter.criteria.DomainChildCriteriaRules
import inc.ahmedmourad.sherlock.domain.model.DomainPublishedChild
import inc.ahmedmourad.sherlock.domain.model.DomainRetrievedChild
import inc.ahmedmourad.sherlock.domain.model.DomainSimpleRetrievedChild
import io.reactivex.Flowable
import io.reactivex.Single

interface ChildrenRepository {

    fun publish(child: DomainPublishedChild): Single<Either<Throwable, DomainRetrievedChild>>

    fun find(
            child: DomainSimpleRetrievedChild
    ): Flowable<Either<Throwable, Option<Tuple2<DomainRetrievedChild, Int>>>>

    fun findAll(
            rules: DomainChildCriteriaRules,
            filter: Filter<DomainRetrievedChild>
    ): Flowable<Either<Throwable, List<Tuple2<DomainSimpleRetrievedChild, Int>>>>

    fun findLastSearchResults(): Flowable<List<Tuple2<DomainSimpleRetrievedChild, Int>>>

    fun test(): Tester

    interface Tester {
        fun clear(): Single<Either<Throwable, Unit>>
    }
}
