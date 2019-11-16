package inc.ahmedmourad.sherlock.auth.manager.dependencies

import arrow.core.Either
import io.reactivex.Single

internal interface AuthImageRepository {
    fun storeUserPicture(id: String, picture: ByteArray): Single<Either<Throwable, String>>
}
