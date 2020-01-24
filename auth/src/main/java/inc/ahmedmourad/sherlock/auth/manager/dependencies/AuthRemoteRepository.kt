package inc.ahmedmourad.sherlock.auth.manager.dependencies

import arrow.core.Either
import inc.ahmedmourad.sherlock.auth.model.AuthRetrievedUserDetails
import inc.ahmedmourad.sherlock.auth.model.AuthStoredUserDetails
import io.reactivex.Flowable
import io.reactivex.Single

internal interface AuthRemoteRepository {

    fun storeUserDetails(details: AuthStoredUserDetails): Single<Either<Throwable, AuthRetrievedUserDetails>>

    fun findUser(id: String): Flowable<Either<Throwable, AuthRetrievedUserDetails?>>

    fun updateUserLastLoginDate(id: String): Single<Either<Throwable, Unit>>
}
