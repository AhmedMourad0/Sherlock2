package inc.ahmedmourad.sherlock.children.repository.dependencies

import inc.ahmedmourad.sherlock.domain.filter.Filter
import inc.ahmedmourad.sherlock.domain.filter.criteria.DomainChildCriteriaRules
import inc.ahmedmourad.sherlock.domain.model.DomainPublishedChild
import inc.ahmedmourad.sherlock.domain.model.DomainRetrievedChild
import inc.ahmedmourad.sherlock.domain.model.DomainSimpleRetrievedChild
import inc.ahmedmourad.sherlock.domain.model.Either
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

internal interface ChildrenRemoteRepository {

    fun publish(domainChild: DomainPublishedChild): Single<DomainRetrievedChild>

    fun find(child: DomainSimpleRetrievedChild): Flowable<Either<DomainRetrievedChild?, Throwable>>

    fun findAll(rules: DomainChildCriteriaRules, filter: Filter<DomainRetrievedChild>): Flowable<Either<List<Pair<DomainRetrievedChild, Int>>, Throwable>>

    fun clear(): Completable
}
