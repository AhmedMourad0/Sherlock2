package inc.ahmedmourad.sherlock.domain.model

data class DomainUrlChild(
        override val id: String,
        override val publicationDate: Long,
        override val name: DomainName,
        override val notes: String,
        override val location: DomainLocation,
        override val appearance: DomainAppearance<DomainRange>,
        val pictureUrl: String
) : DomainChild
