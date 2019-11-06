package inc.ahmedmourad.sherlock.domain.model

data class DomainRetrievedChild(
        val id: String,
        val publicationDate: Long,
        override val name: DomainName,
        override val notes: String,
        override val location: DomainLocation,
        override val appearance: DomainEstimatedAppearance,
        val pictureUrl: String) : DomainChild {
    fun simplify() = DomainSimpleRetrievedChild(
            id,
            publicationDate,
            name,
            notes,
            location.name,
            location.address,
            pictureUrl
    )
}

data class DomainSimpleRetrievedChild(
        val id: String,
        val publicationDate: Long,
        val name: DomainName,
        val notes: String,
        val locationName: String,
        val locationAddress: String,
        val pictureUrl: String
)
