package inc.ahmedmourad.sherlock.domain.model

interface DomainChild {
    val id: String
    val publicationDate: Long
    val name: DomainName
    val notes: String
    val location: DomainLocation
    val appearance: DomainAppearance<DomainRange>
}
