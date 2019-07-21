package inc.ahmedmourad.sherlock.data.repositories

import inc.ahmedmourad.sherlock.domain.model.DomainUrlChild
import inc.ahmedmourad.sherlock.domain.model.Optional
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

interface LocalRepository {

    fun updateIfExists(child: DomainUrlChild): Single<Optional<Pair<DomainUrlChild, Int>>>

    fun findScore(childId: String): Single<Int>

    fun findAll(): Flowable<List<Pair<DomainUrlChild, Int>>>

    fun replaceAll(results: List<Pair<DomainUrlChild, Int>>): Completable
}
