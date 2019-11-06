package inc.ahmedmourad.sherlock.children.local.model

import inc.ahmedmourad.sherlock.domain.model.DomainName

internal data class RoomName(val first: String, val last: String) {
    fun toDomainName() = DomainName(first, last)
}
