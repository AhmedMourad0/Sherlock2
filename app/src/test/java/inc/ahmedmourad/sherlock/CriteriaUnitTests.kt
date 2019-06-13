package inc.ahmedmourad.sherlock

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import dagger.Lazy
import inc.ahmedmourad.sherlock.domain.constants.Gender
import inc.ahmedmourad.sherlock.domain.constants.Hair
import inc.ahmedmourad.sherlock.domain.constants.Skin
import inc.ahmedmourad.sherlock.domain.device.LocationManager
import inc.ahmedmourad.sherlock.domain.filter.ResultsFilter
import inc.ahmedmourad.sherlock.domain.filter.criteria.Criteria
import inc.ahmedmourad.sherlock.domain.filter.criteria.DomainChildCriteriaRules
import inc.ahmedmourad.sherlock.domain.filter.criteria.LooseCriteria
import inc.ahmedmourad.sherlock.domain.model.*
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import org.mockito.Mockito
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

object CriteriaUnitTests : Spek({

    describe("Criteria") {

        describe("LooseCriteria") {

            val locationManager by memoized {
                mock<LocationManager> {
                    on { distanceBetween(50.0, 40.0, 90.0, 140.0) } doReturn 4000L
                    on { distanceBetween(50.0, 40.0, 45.0, 180.0) } doReturn 7000L
                }
            }

            val rules by memoized {
                DomainChildCriteriaRules(
                        "Ahmed",
                        "Mourad",
                        DomainLocation("1", "a", "a", DomainCoordinates(50.0, 40.0)),
                        Gender.MALE,
                        Skin.WHEAT,
                        Hair.DARK,
                        20,
                        180
                )
            }

            val criteria by memoized { LooseCriteria(rules, Lazy { locationManager }) }

            it("should calculate the result supplied with apply") {

                val (_, rightScore) = criteria.apply(DomainUrlChild(
                        "1",
                        System.currentTimeMillis(),
                        DomainName("Ahmed", "Mourad"),
                        "",
                        DomainLocation("1", "a", "a", DomainCoordinates(90.0, 140.0)),
                        DomainAppearance(
                                Gender.MALE,
                                Skin.WHEAT,
                                Hair.DARK,
                                DomainRange(18, 22),
                                DomainRange(170, 190)
                        ), ""
                ))

                val (_, wrongsScore) = criteria.apply(DomainUrlChild(
                        "1",
                        System.currentTimeMillis(),
                        DomainName("Mohamed", "Mosad"),
                        "",
                        DomainLocation("1", "a", "a", DomainCoordinates(45.0, 180.0)),
                        DomainAppearance(
                                Gender.FEMALE,
                                Skin.WHITE,
                                Hair.BROWN,
                                DomainRange(16, 17),
                                DomainRange(135, 145)
                        ), ""
                ))

                verify(locationManager).distanceBetween(50.0, 40.0, 90.0, 140.0)
                verify(locationManager).distanceBetween(50.0, 40.0, 45.0, 180.0)

                //TODO: i don't like this, maybe
                assertEquals(rightScore.calculate(), 700)
                assertEquals(wrongsScore.calculate(), 300)

                assertTrue(rightScore.calculate() > wrongsScore.calculate())

                assertTrue(rightScore.passes())
                assertTrue(wrongsScore.passes())
            }
        }

        describe("ResultsFilter") {

            val result0 by memoized {
                DomainUrlChild(
                        "0",
                        System.currentTimeMillis(),
                        DomainName("", ""),
                        "",
                        DomainLocation("", "", "", DomainCoordinates(50.0, 50.0)),
                        DomainAppearance(
                                Gender.MALE,
                                Skin.DARK,
                                Hair.BROWN,
                                DomainRange(10, 15),
                                DomainRange(100, 150)
                        ), ""
                )
            }

            val result1 by memoized {
                DomainUrlChild(
                        "1",
                        System.currentTimeMillis(),
                        DomainName("", ""),
                        "",
                        DomainLocation("", "", "", DomainCoordinates(50.0, 50.0)),
                        DomainAppearance(
                                Gender.MALE,
                                Skin.DARK,
                                Hair.BROWN,
                                DomainRange(10, 15),
                                DomainRange(100, 150)
                        ), ""
                )
            }

            val result2 by memoized {
                DomainUrlChild(
                        "2",
                        System.currentTimeMillis(),
                        DomainName("", ""),
                        "",
                        DomainLocation("", "", "", DomainCoordinates(50.0, 50.0)),
                        DomainAppearance(
                                Gender.MALE,
                                Skin.DARK,
                                Hair.BROWN,
                                DomainRange(10, 15),
                                DomainRange(100, 150)
                        ), ""
                )
            }

            val result3 by memoized {
                DomainUrlChild(
                        "3",
                        System.currentTimeMillis(),
                        DomainName("", ""),
                        "",
                        DomainLocation("", "", "", DomainCoordinates(50.0, 50.0)),
                        DomainAppearance(
                                Gender.MALE,
                                Skin.DARK,
                                Hair.BROWN,
                                DomainRange(10, 15),
                                DomainRange(100, 150)
                        ), ""
                )
            }

            val result4 by memoized {
                DomainUrlChild(
                        "4",
                        System.currentTimeMillis(),
                        DomainName("", ""),
                        "",
                        DomainLocation("", "", "", DomainCoordinates(50.0, 50.0)),
                        DomainAppearance(
                                Gender.MALE,
                                Skin.DARK,
                                Hair.BROWN,
                                DomainRange(10, 15),
                                DomainRange(100, 150)
                        ), ""
                )
            }

            val score0 by memoized { mock<Criteria.Score>() }
            val score1 by memoized { mock<Criteria.Score>() }
            val score2 by memoized { mock<Criteria.Score>() }
            val score3 by memoized { mock<Criteria.Score>() }
            val score4 by memoized { mock<Criteria.Score>() }

            val pair0 by memoized { result0 to score0 }
            val pair1 by memoized { result1 to score1 }
            val pair2 by memoized { result2 to score2 }
            val pair3 by memoized { result3 to score3 }
            val pair4 by memoized { result4 to score4 }

            val criteria by memoized {
                mock<Criteria<DomainChild>> {
                    on { apply(result0) } doReturn pair0
                    on { apply(result1) } doReturn pair1
                    on { apply(result2) } doReturn pair2
                    on { apply(result3) } doReturn pair3
                    on { apply(result4) } doReturn pair4
                }
            }

            val results by memoized { listOf(result0, result1, result2, result3, result4) }

            val filter by memoized { ResultsFilter(criteria) }

            it("should only return results that pass") {

                whenever(score0.passes()).thenReturn(true)
                whenever(score1.passes()).thenReturn(false)
                whenever(score2.passes()).thenReturn(false)
                whenever(score3.passes()).thenReturn(true)
                whenever(score4.passes()).thenReturn(false)

                val filteredResults = filter.filter(results)

                results.forEach { verify(criteria).apply(it) }

                assertEquals(filteredResults.size, 2)
                assertEquals(filteredResults[0].first.id, result0.id)
                assertEquals(filteredResults[1].first.id, result3.id)
            }

            it("should descending sort results according to their calculate") {

                whenever(score0.passes()).thenReturn(true)
                whenever(score1.passes()).thenReturn(true)
                whenever(score2.passes()).thenReturn(true)
                whenever(score3.passes()).thenReturn(true)
                whenever(score4.passes()).thenReturn(true)

                whenever(score0.calculate()).thenReturn(532)
                whenever(score1.calculate()).thenReturn(400)
                whenever(score2.calculate()).thenReturn(652)
                whenever(score3.calculate()).thenReturn(700)
                whenever(score4.calculate()).thenReturn(300)

                val filteredResults = filter.filter(results)

                results.forEach { verify(criteria).apply(it) }

                assertEquals(filteredResults.find { it.first == result0 }?.second
                        ?: -1, score0.calculate())
                assertEquals(filteredResults.find { it.first == result1 }?.second
                        ?: -1, score1.calculate())
                assertEquals(filteredResults.find { it.first == result2 }?.second
                        ?: -1, score2.calculate())
                assertEquals(filteredResults.find { it.first == result3 }?.second
                        ?: -1, score3.calculate())
                assertEquals(filteredResults.find { it.first == result4 }?.second
                        ?: -1, score4.calculate())

                assertTrue(filteredResults.zipWithNext { res1, res2 -> (res1.second >= res2.second) }.all { it })
            }
        }

        after {
            Mockito.framework().clearInlineMocks()
        }
    }
})
