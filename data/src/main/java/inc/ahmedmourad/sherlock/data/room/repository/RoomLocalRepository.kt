package inc.ahmedmourad.sherlock.data.room.repository

import dagger.Lazy
import inc.ahmedmourad.sherlock.data.mapper.DataModelsMapper
import inc.ahmedmourad.sherlock.data.repositories.LocalRepository
import inc.ahmedmourad.sherlock.data.room.database.SherlockDatabase
import inc.ahmedmourad.sherlock.domain.model.DomainUrlChild
import inc.ahmedmourad.sherlock.domain.model.Optional
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

class RoomLocalRepository(private val db: Lazy<SherlockDatabase>) : LocalRepository {

    override fun updateIfExists(child: DomainUrlChild): Single<Optional<Pair<DomainUrlChild, Int>>> {
        return db.get()
                .resultsDao()
                .updateIfExists(DataModelsMapper.toRoomChildEntity(child))
                .map { it.map(DataModelsMapper::toDomainUrlChild) }
    }

    override fun findScore(childId: String): Single<Int> {
        return db.get()
                .resultsDao()
                .findScore(childId)
                .toSingle(-1)
    }

    override fun findAll(): Flowable<List<Pair<DomainUrlChild, Int>>> {
        return db.get()
                .resultsDao()
                .findAll()
                .distinctUntilChanged()
                .map { it.map(DataModelsMapper::toDomainUrlChild) }
    }

    override fun replaceAll(results: List<Pair<DomainUrlChild, Int>>): Completable {
        return db.get()
                .resultsDao()
                .replaceAll(results.map(DataModelsMapper::toRoomChildEntity))
    }
}
