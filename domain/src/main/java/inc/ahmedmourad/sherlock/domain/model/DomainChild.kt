package inc.ahmedmourad.sherlock.domain.model

interface DomainChild {
    val name: DomainName
    val notes: String
    val location: DomainLocation
    val appearance: DomainEstimatedAppearance
}
