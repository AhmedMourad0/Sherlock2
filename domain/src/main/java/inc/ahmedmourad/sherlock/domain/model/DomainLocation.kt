package inc.ahmedmourad.sherlock.domain.model

data class DomainLocation(val id: String, val name: String, val address: String, val coordinates: DomainCoordinates) {
    companion object {
        fun empty() = DomainLocation("", "", "", DomainCoordinates(1000.0, 1000.0))
    }
}
