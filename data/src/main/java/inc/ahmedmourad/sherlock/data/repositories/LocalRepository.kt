package inc.ahmedmourad.sherlock.data.repositories

import inc.ahmedmourad.sherlock.domain.model.DomainUrlChild
import io.reactivex.Completable
import io.reactivex.Flowable

interface LocalRepository {

    fun getResults(): Flowable<List<Pair<DomainUrlChild, Int>>>

    fun replaceResults(results: List<Pair<DomainUrlChild, Int>>): Completable
}
