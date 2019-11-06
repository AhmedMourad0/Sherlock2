package inc.ahmedmourad.sherlock.children.repository.dependencies

import io.reactivex.Single

internal interface ChildrenImageRepository {
    fun storeChildPicture(id: String, picture: ByteArray): Single<String>
}
