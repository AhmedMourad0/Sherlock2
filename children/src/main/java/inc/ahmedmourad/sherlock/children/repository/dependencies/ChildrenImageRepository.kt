package inc.ahmedmourad.sherlock.children.repository.dependencies

import arrow.core.Either
import inc.ahmedmourad.sherlock.domain.model.children.ChildId
import inc.ahmedmourad.sherlock.domain.model.children.Url
import io.reactivex.Single

internal interface ChildrenImageRepository {
    fun storeChildPicture(id: ChildId, picture: ByteArray?): Single<Either<Throwable, Url?>>
}
