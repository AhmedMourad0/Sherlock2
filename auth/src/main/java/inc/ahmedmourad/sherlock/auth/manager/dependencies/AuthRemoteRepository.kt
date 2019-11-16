package inc.ahmedmourad.sherlock.auth.manager.dependencies

import arrow.core.Either
import arrow.core.Option
import inc.ahmedmourad.sherlock.domain.model.DomainSignedInUser
import inc.ahmedmourad.sherlock.domain.model.DomainUserData
import io.reactivex.Single

internal interface AuthRemoteRepository {

    fun storeUser(user: DomainUserData): Single<Either<Throwable, DomainSignedInUser>>

    fun findUser(id: String): Single<Either<Throwable, Option<DomainSignedInUser>>>

    fun updateUserLastLoginDate(id: String): Single<Either<Throwable, Unit>>
}
