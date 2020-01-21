package inc.ahmedmourad.sherlock.auth.model

import arrow.core.Option
import arrow.core.none
import inc.ahmedmourad.sherlock.domain.model.auth.DomainIncompleteUser

data class AuthIncompleteUser(
        val id: String,
        val email: Option<String>,
        val name: Option<String>,
        val pictureUrl: Option<String>
) {
    fun toDomainIncompleteUser() = DomainIncompleteUser(
            id,
            email,
            name,
            none(),
            pictureUrl
    )
}
