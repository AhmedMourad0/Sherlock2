package inc.ahmedmourad.sherlock.children.remote.model

import inc.ahmedmourad.sherlock.domain.model.children.DomainRange

internal data class FirebaseRange(val from: Int, val to: Int) {
    fun toDomainRange() = DomainRange(from, to)
}
