package inc.ahmedmourad.sherlock.children.repository.dependencies

import arrow.core.Either
import io.reactivex.Single

internal interface ChildrenImageRepository {
    fun storeChildPicture(id: String, picture: ByteArray): Single<Either<Throwable, String>>
}
