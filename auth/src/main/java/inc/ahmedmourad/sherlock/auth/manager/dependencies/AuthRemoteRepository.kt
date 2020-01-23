package inc.ahmedmourad.sherlock.auth.manager.dependencies

import arrow.core.Either
import arrow.core.Option
import inc.ahmedmourad.sherlock.auth.model.AuthRetrievedUserDetails
import inc.ahmedmourad.sherlock.auth.model.AuthStoredUserDetails
import io.reactivex.Flowable
import io.reactivex.Single

internal interface AuthRemoteRepository {

    fun storeUserDetails(details: AuthStoredUserDetails): Single<Either<Throwable, AuthRetrievedUserDetails>>

    fun findUser(id: String): Flowable<Either<Throwable, Option<AuthRetrievedUserDetails>>>

    fun updateUserLastLoginDate(id: String): Single<Either<Throwable, Unit>>
}
