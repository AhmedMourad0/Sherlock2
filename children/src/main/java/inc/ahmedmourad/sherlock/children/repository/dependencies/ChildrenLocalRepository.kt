package inc.ahmedmourad.sherlock.children.repository.dependencies

import inc.ahmedmourad.sherlock.domain.model.DomainRetrievedChild
import inc.ahmedmourad.sherlock.domain.model.DomainSimpleRetrievedChild
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Single

internal interface ChildrenLocalRepository {

    fun updateIfExists(child: DomainRetrievedChild): Maybe<Pair<DomainRetrievedChild, Int>>

    fun findAll(): Flowable<List<Pair<DomainSimpleRetrievedChild, Int>>>

    fun replaceAll(results: List<Pair<DomainRetrievedChild, Int>>): Single<List<Pair<DomainSimpleRetrievedChild, Int>>>

    fun clear(): Completable
}
