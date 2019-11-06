package inc.ahmedmourad.sherlock.children.remote.model

import inc.ahmedmourad.sherlock.domain.model.DomainChild

internal interface FirebaseChild {

    val name: FirebaseName
    val notes: String
    val location: FirebaseLocation
    val appearance: FirebaseEstimatedAppearance

    fun toDomainChild(): DomainChild
}
