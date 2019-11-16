package inc.ahmedmourad.sherlock.domain.model

data class DomainLocation(
        val id: String,
        val name: String,
        val address: String,
        val coordinates: DomainCoordinates
)
