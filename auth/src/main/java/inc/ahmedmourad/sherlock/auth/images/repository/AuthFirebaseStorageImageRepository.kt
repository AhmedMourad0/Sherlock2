package inc.ahmedmourad.sherlock.auth.images.repository

import android.net.Uri
import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import dagger.Lazy
import inc.ahmedmourad.sherlock.auth.images.contract.Contract
import inc.ahmedmourad.sherlock.auth.manager.IsUserSignedIn
import inc.ahmedmourad.sherlock.auth.manager.dependencies.AuthImageRepository
import inc.ahmedmourad.sherlock.domain.exceptions.NoInternetConnectionException
import inc.ahmedmourad.sherlock.domain.exceptions.NoSignedInUserException
import inc.ahmedmourad.sherlock.domain.platform.ConnectivityManager
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

internal class AuthFirebaseStorageImageRepository(
        private val connectivityManager: Lazy<ConnectivityManager>,
        private val isUserSignedIn: IsUserSignedIn,
        private val storage: Lazy<FirebaseStorage>
) : AuthImageRepository {

    override fun storeUserPicture(id: String, picture: ByteArray): Single<Either<Throwable, String>> {
        return connectivityManager.get()
                .isInternetConnected()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .flatMap { isInternetConnected ->
                    if (isInternetConnected)
                        isUserSignedIn().map(Boolean::right)
                    else
                        Single.just(NoInternetConnectionException().left())
                }.flatMap { isUserSignedInEither ->
                    isUserSignedInEither.fold(ifLeft = {
                        Single.just(it.left())
                    }, ifRight = { isUserSignedIn ->
                        if (isUserSignedIn) {
                            storePicture(Contract.Users.PATH, id, picture)
                        } else {
                            Single.just(NoSignedInUserException().left())
                        }
                    })
                }.flatMap { referenceEither ->
                    referenceEither.fold(ifLeft = {
                        Single.just(it.left())
                    }, ifRight = {
                        fetchPictureUrl(it)
                    })
                }
    }

    private fun storePicture(path: String, id: String, picture: ByteArray): Single<Either<Throwable, StorageReference>> {

        val filePath = storage.get().getReference(path)
                .child("$id.${Contract.FILE_FORMAT}")

        return Single.create<Either<Throwable, StorageReference>> { emitter ->

            val successListener = { _: UploadTask.TaskSnapshot ->
                emitter.onSuccess(filePath.right())
            }

            val failureListener = { throwable: Throwable ->
                emitter.onSuccess(throwable.left())
            }

            filePath.putBytes(picture)
                    .addOnSuccessListener(successListener)
                    .addOnFailureListener(failureListener)

        }.subscribeOn(Schedulers.io()).observeOn(Schedulers.io())
    }

    private fun fetchPictureUrl(filePath: StorageReference): Single<Either<Throwable, String>> {

        return Single.create<Either<Throwable, String>> { emitter ->

            val successListener = { uri: Uri ->
                emitter.onSuccess(uri.toString().right())
            }

            val failureListener = { throwable: Throwable ->
                emitter.onSuccess(throwable.left())
            }

            filePath.downloadUrl
                    .addOnSuccessListener(successListener)
                    .addOnFailureListener(failureListener)

        }.subscribeOn(Schedulers.io()).observeOn(Schedulers.io())
    }
}
