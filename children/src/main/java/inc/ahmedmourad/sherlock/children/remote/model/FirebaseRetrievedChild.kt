package inc.ahmedmourad.sherlock.children.remote.model

import inc.ahmedmourad.sherlock.domain.model.DomainRetrievedChild

internal data class FirebaseRetrievedChild(
        val id: String,
        val publicationDate: Long,
        val name: FirebaseName,
        val notes: String,
        val location: FirebaseLocation,
        val appearance: FirebaseEstimatedAppearance,
        val pictureUrl: String) {
    fun toDomainChild() = DomainRetrievedChild(
            id,
            publicationDate,
            name.toDomainName(),
            notes,
            location.toDomainLocation(),
            appearance.toDomainAppearance(),
            pictureUrl
    )
}

internal data class FirebaseSimpleRetrievedChild(
        val id: String,
        val publicationDate: Long,
        val name: FirebaseName,
        val notes: String,
        val locationName: String,
        val locationAddress: String,
        val pictureUrl: String
)
