package inc.ahmedmourad.sherlock.domain.filter.criteria

import arrow.core.Tuple2
import arrow.core.toT
import dagger.Lazy
import inc.ahmedmourad.sherlock.domain.model.children.DomainRetrievedChild
import inc.ahmedmourad.sherlock.domain.platform.LocationManager
import me.xdrop.fuzzywuzzy.FuzzySearch

//TODO: date of losing and finding the child matters
internal class LooseCriteria(
        private val rules: DomainChildCriteriaRules,
        private val locationManager: Lazy<LocationManager>
) : Criteria<DomainRetrievedChild> {

    override fun apply(result: DomainRetrievedChild): Tuple2<DomainRetrievedChild, Criteria.Score> {
        return result toT Score(getFirstNameRatio(result),
                getLastNameRatio(result),
                getDistance(result),
                isSameGender(result),
                isSameHair(result),
                isSameSkin(result),
                getAgeError(result),
                getHeightError(result)
        )
    }

    private fun getFirstNameRatio(child: DomainRetrievedChild): Int {
        return if (rules.name.first.isBlank() || child.name.first.isBlank())
            50
        else
            FuzzySearch.ratio(rules.name.first, child.name.first)
    }

    private fun getLastNameRatio(child: DomainRetrievedChild): Int {
        return if (rules.name.last.isBlank() || child.name.last.isBlank())
            50
        else
            FuzzySearch.ratio(rules.name.last, child.name.last)
    }

    private fun getDistance(child: DomainRetrievedChild): Long {

        if (!rules.location.coordinates.isValid())
            return MAX_DISTANCE.value / 2

        if (!child.location.coordinates.isValid())
            return MAX_DISTANCE.value / 2

        return locationManager.get().distanceBetween(rules.location.coordinates.latitude,
                rules.location.coordinates.longitude,
                child.location.coordinates.latitude,
                child.location.coordinates.longitude
        )
    }

    private fun isSameGender(child: DomainRetrievedChild): Boolean {
        return rules.appearance.gender == child.appearance.gender
    }

    private fun isSameSkin(child: DomainRetrievedChild): Boolean {
        return rules.appearance.skin == child.appearance.skin
    }

    private fun isSameHair(child: DomainRetrievedChild): Boolean {
        return rules.appearance.hair == child.appearance.hair
    }

    // the two-years padding is applied to account for user error when estimating age
    private fun getAgeError(child: DomainRetrievedChild): Int {

        val min = child.appearance.age.from - 2
        val max = child.appearance.age.to + 2

        return when {
            rules.appearance.age < min -> min - rules.appearance.age
            rules.appearance.age > max -> rules.appearance.age - max
            else -> 0
        }
    }

    // the 15 cm padding is applied to account for user error when estimating height
    private fun getHeightError(child: DomainRetrievedChild): Int {

        val min = child.appearance.height.from - 15
        val max = child.appearance.height.to + 15

        return when {
            rules.appearance.height < min -> min - rules.appearance.height
            rules.appearance.height > max -> rules.appearance.height - max
            else -> 0
        }
    }

    companion object {
        val MAX_DISTANCE = MaxDistance(5000L, 100L)
    }

    class Score(private val firstNameRatio: Int,
                private val lastNameRatio: Int,
                private val distance: Long,
                private val isSameGender: Boolean,
                private val isSameHair: Boolean,
                private val isSameSkin: Boolean,
                private val ageError: Int,
                private val heightError: Int) : Criteria.Score {

        override fun passes() = true

        override fun calculate(): Int {
            return getFirstNameRank() +
                    getLastNameRank() +
                    getDistanceRank() +
                    getGenderRank() +
                    getHairRank() +
                    getSkinRank() +
                    getAgeRank() +
                    getHeightRank()
        }

        private fun getFirstNameRank() = firstNameRatio

        private fun getLastNameRank() = lastNameRatio

        private fun getDistanceRank(): Int {
            return if (distance <= MAX_DISTANCE.value)
                100 else
                (100 - ((distance - MAX_DISTANCE.value) / MAX_DISTANCE.factor)).toInt().coerceAtLeast(0)
        }

        private fun getGenderRank() = if (isSameGender) 100 else 0

        private fun getHairRank() = if (isSameHair) 50 else 0

        private fun getSkinRank() = if (isSameSkin) 50 else 0

        private fun getAgeRank() = (100 - (20 * ageError)).coerceAtLeast(0)

        private fun getHeightRank() = (100 - (5 * heightError)).coerceAtLeast(0)
    }

    class MaxDistance(val value: Long, val factor: Long)
}
