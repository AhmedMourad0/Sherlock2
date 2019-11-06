package inc.ahmedmourad.sherlock.auth.manager.dependencies

import inc.ahmedmourad.sherlock.domain.model.DomainSignedInUser
import inc.ahmedmourad.sherlock.domain.model.DomainUserData
import inc.ahmedmourad.sherlock.domain.model.Optional
import io.reactivex.Completable
import io.reactivex.Single

internal interface AuthRemoteRepository {

    fun storeUser(user: DomainUserData): Single<DomainSignedInUser>

    fun findUser(id: String): Single<Optional<DomainSignedInUser>>

    fun updateUserLastLoginDate(id: String): Completable
}
