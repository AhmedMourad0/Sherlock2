package inc.ahmedmourad.sherlock.domain.model.children

data class DomainLocation(
        val id: String,
        val name: String,
        val address: String,
        val coordinates: DomainCoordinates
)
