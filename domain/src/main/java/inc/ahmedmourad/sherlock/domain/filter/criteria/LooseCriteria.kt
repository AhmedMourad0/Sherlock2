package inc.ahmedmourad.sherlock.domain.filter.criteria

import dagger.Lazy
import inc.ahmedmourad.sherlock.domain.device.LocationManager
import inc.ahmedmourad.sherlock.domain.model.DomainChild
import me.xdrop.fuzzywuzzy.FuzzySearch

//TODO: publication date matters
class LooseCriteria(val rules: DomainChildCriteriaRules, private val locationManager: Lazy<LocationManager>) : Criteria<DomainChild> {

    //TODO: replace the pair with DomainScoreChild wrapper which implements DomainChild via a delegate parameter
    override fun apply(result: DomainChild): Pair<DomainChild, Criteria.Score> {
        return result to Score(getFirstNameRatio(result),
                getLastNameRatio(result),
                getDistance(result),
                isSameGender(result),
                isSameHair(result),
                isSameSkin(result),
                getAgeError(result),
                getHeightError(result)
        )
    }

    private fun getFirstNameRatio(child: DomainChild) = if (rules.firstName.isBlank() || child.name.first.isBlank())
        50
    else
        FuzzySearch.ratio(rules.firstName, child.name.first)


    private fun getLastNameRatio(child: DomainChild) = if (rules.lastName.isBlank() || child.name.last.isBlank())
        50
    else
        FuzzySearch.ratio(rules.lastName, child.name.last)

    private fun getDistance(child: DomainChild): Long {

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

    private fun isSameGender(child: DomainChild) = rules.gender == child.appearance.gender

    private fun isSameSkin(child: DomainChild) = rules.skin == child.appearance.skin

    private fun isSameHair(child: DomainChild) = rules.hair == child.appearance.hair

    // the two-years padding is applied to account for user error when estimating age
    private fun getAgeError(child: DomainChild): Int {

        val min = child.appearance.age.from - 2
        val max = child.appearance.age.to + 2

        return when {
            rules.age < min -> min - rules.age
            rules.age > max -> rules.age - max
            else -> 0
        }
    }

    // the 15 cm padding is applied to account for user error when estimating height
    private fun getHeightError(child: DomainChild): Int {

        val min = child.appearance.height.from - 15
        val max = child.appearance.height.to + 15

        return when {
            rules.height < min -> min - rules.height
            rules.height > max -> rules.height - max
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

        override fun calculate(): Int = getFirstNameRank() + getLastNameRank() + getDistanceRank() + getGenderRank() + getHairRank() + getSkinRank() + getAgeRank() + getHeightRank()

        private fun getFirstNameRank() = firstNameRatio

        private fun getLastNameRank() = lastNameRatio

        private fun getDistanceRank() = if (distance <= MAX_DISTANCE.value) 100 else (100 - ((distance - MAX_DISTANCE.value) / MAX_DISTANCE.factor)).toInt().coerceAtLeast(0)

        private fun getGenderRank() = if (isSameGender) 100 else 0

        private fun getHairRank() = if (isSameHair) 50 else 0

        private fun getSkinRank() = if (isSameSkin) 50 else 0

        private fun getAgeRank() = (100 - (20 * ageError)).coerceAtLeast(0)

        private fun getHeightRank() = (100 - (5 * heightError)).coerceAtLeast(0)
    }

    class MaxDistance(val value: Long, val factor: Long)
}
