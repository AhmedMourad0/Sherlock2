package inc.ahmedmourad.sherlock.domain.model

data class DomainRetrievedChild(
        val id: String,
        val publicationDate: Long,
        val name: DomainName,
        val notes: String,
        val location: DomainLocation,
        val appearance: DomainEstimatedAppearance,
        val pictureUrl: String) {
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
