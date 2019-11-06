package inc.ahmedmourad.sherlock.auth.manager.dependencies

import io.reactivex.Single

internal interface AuthImageRepository {
    fun storeUserPicture(id: String, picture: ByteArray): Single<String>
}
