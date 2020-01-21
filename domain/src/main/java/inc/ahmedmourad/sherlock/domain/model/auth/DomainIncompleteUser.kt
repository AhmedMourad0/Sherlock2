package inc.ahmedmourad.sherlock.domain.model.auth

import arrow.core.Option

data class DomainIncompleteUser(
        val id: String,
        val email: Option<String>,
        val name: Option<String>,
        val phoneNumber: Option<String>,
        val pictureUrl: Option<String>
)
