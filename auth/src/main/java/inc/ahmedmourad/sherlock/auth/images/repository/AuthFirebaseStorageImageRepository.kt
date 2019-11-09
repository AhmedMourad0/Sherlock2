package inc.ahmedmourad.sherlock.auth.images.repository

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import dagger.Lazy
import inc.ahmedmourad.sherlock.auth.images.contract.Contract
import inc.ahmedmourad.sherlock.auth.manager.dependencies.AuthImageRepository
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

internal class AuthFirebaseStorageImageRepository(private val storage: Lazy<FirebaseStorage>) : AuthImageRepository {

    override fun storeUserPicture(id: String, picture: ByteArray): Single<String> {
        return storePicture(Contract.Users.PATH, id, picture)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .flatMap(this::fetchPictureUrl)
    }

    private fun storePicture(path: String, id: String, picture: ByteArray): Single<StorageReference> {

        val filePath = storage.get().getReference(path)
                .child("$id.${Contract.FILE_FORMAT}")

        return Single.create<StorageReference> { emitter ->

            val successListener = { _: UploadTask.TaskSnapshot ->
                emitter.onSuccess(filePath)
            }

            val failureListener = emitter::onError

            filePath.putBytes(picture)
                    .addOnSuccessListener(successListener)
                    .addOnFailureListener(failureListener)

        }.subscribeOn(Schedulers.io()).observeOn(Schedulers.io())
    }

    private fun fetchPictureUrl(filePath: StorageReference): Single<String> {

        return Single.create<String> { emitter ->

            val successListener = { uri: Uri ->
                emitter.onSuccess(uri.toString())
            }

            val failureListener = emitter::onError

            filePath.downloadUrl
                    .addOnSuccessListener(successListener)
                    .addOnFailureListener(failureListener)

        }.subscribeOn(Schedulers.io()).observeOn(Schedulers.io())
    }
}