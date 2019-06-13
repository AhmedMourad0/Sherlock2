package inc.ahmedmourad.sherlock.domain.model

data class DomainUrlChild(
        override val id: String,
        override val timeMillis: Long,
        override val name: DomainName,
        override val notes: String,
        override val location: DomainLocation,
        override val appearance: DomainAppearance,
        val pictureUrl: String
) : DomainChild
