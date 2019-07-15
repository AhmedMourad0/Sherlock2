package inc.ahmedmourad.sherlock.utils

import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import dagger.Lazy
import inc.ahmedmourad.sherlock.data.firebase.contract.FirebaseContract
import inc.ahmedmourad.sherlock.data.room.database.SherlockDatabase
import inc.ahmedmourad.sherlock.data.room.repository.RoomLocalRepository
import io.reactivex.Completable

fun deleteChildren() {

    Completable.create {
        FirebaseDatabase.getInstance()
                .getReference(FirebaseContract.Database.PATH_CHILDREN)
                .removeValue { _, _ ->
                    it.onComplete()
                }
    }.blockingAwait()

    RoomLocalRepository(Lazy { SherlockDatabase.getInstance() }).replaceResults(emptyList())
            .test()
            .await()
            .assertNoErrors()
            .assertComplete()
}

fun deletePictures() {
    Completable.create {
        FirebaseStorage.getInstance()
                .getReference(FirebaseContract.Storage.PATH_CHILDREN)
                .delete()
                .addOnCompleteListener { _ ->
                    it.onComplete()
                }
    }.blockingAwait()
}
