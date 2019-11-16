package inc.ahmedmourad.sherlock.children.repository.dependencies

import arrow.core.Tuple2
import inc.ahmedmourad.sherlock.domain.model.DomainRetrievedChild
import inc.ahmedmourad.sherlock.domain.model.DomainSimpleRetrievedChild
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Single

internal interface ChildrenLocalRepository {

    fun updateIfExists(
            child: DomainRetrievedChild
    ): Maybe<Tuple2<DomainRetrievedChild, Int>>

    fun findAll(): Flowable<List<Tuple2<DomainSimpleRetrievedChild, Int>>>

    fun replaceAll(
            results: List<Tuple2<DomainRetrievedChild, Int>>
    ): Single<List<Tuple2<DomainSimpleRetrievedChild, Int>>>

    fun clear(): Completable
}
