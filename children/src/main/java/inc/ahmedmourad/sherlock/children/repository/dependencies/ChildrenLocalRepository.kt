package inc.ahmedmourad.sherlock.children.repository.dependencies

import arrow.core.Either
import arrow.core.Tuple2
import inc.ahmedmourad.sherlock.domain.model.children.RetrievedChild
import inc.ahmedmourad.sherlock.domain.model.children.SimpleRetrievedChild
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Single

internal interface ChildrenLocalRepository {

    fun updateIfExists(
            child: RetrievedChild
    ): Maybe<Either<Throwable, Tuple2<RetrievedChild, Int?>>>

    fun findAllWithWeight(): Flowable<Either<Throwable, List<Tuple2<SimpleRetrievedChild, Int>>>>

    fun replaceAll(
            results: List<Tuple2<RetrievedChild, Int>>
    ): Single<Either<Throwable, List<Tuple2<SimpleRetrievedChild, Int?>>>>

    fun clear(): Completable
}
