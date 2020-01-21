package inc.ahmedmourad.sherlock.children.remote.model

import com.google.firebase.firestore.FieldValue
import inc.ahmedmourad.sherlock.children.remote.contract.Contract

internal data class FirebasePublishedChild(
        val name: FirebaseName,
        val notes: String,
        val location: FirebaseLocation,
        val appearance: FirebaseEstimatedAppearance,
        val picture: ByteArray) {

    fun toFirebaseRetrievedChild(id: String, publicationDate: Long, pictureUrl: String): FirebaseRetrievedChild {
        return FirebaseRetrievedChild(
                id,
                publicationDate,
                name,
                notes,
                location,
                appearance,
                pictureUrl
        )
    }

    fun toMap(pictureUrl: String): Map<String, Any> = hashMapOf(
            Contract.Database.Children.PUBLICATION_DATE to FieldValue.serverTimestamp(),
            Contract.Database.Children.FIRST_NAME to name.first,
            Contract.Database.Children.LAST_NAME to name.last,
            Contract.Database.Children.LOCATION_ID to location.id,
            Contract.Database.Children.LOCATION_NAME to location.name,
            Contract.Database.Children.LOCATION_ADDRESS to location.address,
            Contract.Database.Children.LOCATION_LATITUDE to location.coordinates.latitude,
            Contract.Database.Children.LOCATION_LONGITUDE to location.coordinates.longitude,
            Contract.Database.Children.START_AGE to appearance.age.from,
            Contract.Database.Children.END_AGE to appearance.age.to,
            Contract.Database.Children.START_HEIGHT to appearance.height.from,
            Contract.Database.Children.END_HEIGHT to appearance.height.to,
            Contract.Database.Children.GENDER to appearance.gender.value,
            Contract.Database.Children.SKIN to appearance.skin.value,
            Contract.Database.Children.HAIR to appearance.hair.value,
            Contract.Database.Children.NOTES to notes,
            Contract.Database.Children.PICTURE_URL to pictureUrl
    )

    override fun equals(other: Any?): Boolean {

        if (this === other)
            return true

        if (javaClass != other?.javaClass)
            return false

        other as FirebasePublishedChild

        if (name != other.name)
            return false

        if (notes != other.notes)
            return false

        if (location != other.location)
            return false

        if (appearance != other.appearance)
            return false

        if (!picture.contentEquals(other.picture))
            return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + notes.hashCode()
        result = 31 * result + location.hashCode()
        result = 31 * result + appearance.hashCode()
        result = 31 * result + picture.contentHashCode()
        return result
    }
}
