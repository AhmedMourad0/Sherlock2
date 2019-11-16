package inc.ahmedmourad.sherlock.children.repository.dependencies

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

internal interface ChildrenRemoteRepository {

    fun publish(
            domainChild: DomainPublishedChild
    ): Single<Either<Throwable, DomainRetrievedChild>>

    fun find(
            child: DomainSimpleRetrievedChild
    ): Flowable<Either<Throwable, Option<DomainRetrievedChild>>>

    fun findAll(
            rules: DomainChildCriteriaRules,
            filter: Filter<DomainRetrievedChild>
    ): Flowable<Either<Throwable, List<Tuple2<DomainRetrievedChild, Int>>>>

    fun clear(): Single<Either<Throwable, Unit>>
}
