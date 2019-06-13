package inc.ahmedmourad.sherlock.data.room.repository

import dagger.Lazy
import inc.ahmedmourad.sherlock.data.mapper.DataModelsMapper
import inc.ahmedmourad.sherlock.data.repositories.LocalRepository
import inc.ahmedmourad.sherlock.data.room.database.SherlockDatabase
import inc.ahmedmourad.sherlock.domain.model.DomainUrlChild
import io.reactivex.Completable
import io.reactivex.Flowable

class RoomLocalRepository(private val db: Lazy<SherlockDatabase>) : LocalRepository {

    override fun getResults(): Flowable<List<Pair<DomainUrlChild, Int>>> {
        return db.get()
                .resultsDao()
                .getResults()
                .distinctUntilChanged()
                .map { it.map(DataModelsMapper::toDomainUrlChild) }
    }

    override fun replaceResults(results: List<Pair<DomainUrlChild, Int>>): Completable {
        return db.get()
                .resultsDao()
                .replaceResults(results.map(DataModelsMapper::toRoomChildEntity))
    }
}
