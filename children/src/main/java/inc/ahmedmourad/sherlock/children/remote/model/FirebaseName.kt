package inc.ahmedmourad.sherlock.children.remote.model

import inc.ahmedmourad.sherlock.domain.model.DomainName

internal data class FirebaseName(val first: String, val last: String) {
    fun toDomainName() = DomainName(first, last)
}
