package inc.ahmedmourad.sherlock.children.repository.dependencies

import arrow.core.Either
import inc.ahmedmourad.sherlock.domain.model.children.submodel.ChildId
import inc.ahmedmourad.sherlock.domain.model.common.Url
import io.reactivex.Single

internal interface ChildrenImageRepository {
    fun storeChildPicture(id: ChildId, picture: ByteArray?): Single<Either<Throwable, Url?>>
}
