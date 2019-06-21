package inc.ahmedmourad.sherlock

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import dagger.Lazy
import inc.ahmedmourad.sherlock.domain.constants.Gender
import inc.ahmedmourad.sherlock.domain.constants.Hair
import inc.ahmedmourad.sherlock.domain.constants.Skin
import inc.ahmedmourad.sherlock.domain.filter.Filter
import inc.ahmedmourad.sherlock.domain.filter.criteria.DomainChildCriteriaRules
import inc.ahmedmourad.sherlock.domain.interactors.AddChildInteractor
import inc.ahmedmourad.sherlock.domain.interactors.FindChildrenInteractor
import inc.ahmedmourad.sherlock.domain.interactors.GetLastSearchResultsInteractor
import inc.ahmedmourad.sherlock.domain.model.*
import inc.ahmedmourad.sherlock.domain.repository.Repository
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import junit.framework.TestCase.assertSame
import org.mockito.Mockito

import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

object InteractorsUnitTests : Spek({

    describe("Interactors") {

        val repository by memoized { mock<Repository>() }

        describe("AddChildInteractor") {

            it("should call publish on repository when execute is called") {

                val child = DomainPictureChild(
                        "",
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
                        ), ByteArray(0)
                )

                val returnedChild = DomainUrlChild(
                        child.id,
                        child.publicationDate,
                        child.name,
                        child.notes,
                        child.location,
                        child.appearance,
                        "url"
                )

                whenever(repository.publish(child)).thenReturn(Single.just(returnedChild))

                AddChildInteractor(Lazy { repository }).execute { it.child(child) }
                        .test()
                        .await()
                        .assertComplete()
                        .assertNoErrors()
                        .assertValue(returnedChild)

                verify(repository).publish(child)
            }
        }

        describe("FindChildrenInteractor") {

            it("should call find on repository when execute is called") {

                val filter = mock<Filter<DomainUrlChild>>()
                val result = DomainRefreshableResults(Flowable.empty()) { Completable.complete() }
                val rules = DomainChildCriteriaRules(
                        "",
                        "",
                        DomainLocation("", "", "", DomainCoordinates(50.0, 40.0)),
                        Gender.MALE,
                        Skin.WHEAT,
                        Hair.DARK,
                        20,
                        180
                )

                whenever(repository.find(rules, filter)).thenReturn(result)

                assertSame(
                        FindChildrenInteractor(Lazy { repository }).execute { it.rules(rules).filter(filter) },
                        result
                )

                verify(repository).find(rules, filter)
            }
        }

        describe("GetLastSearchResultsInteractor") {

            it("should call getLastSearchResults on repository when execute is called") {

                val list1 = listOf<Pair<DomainUrlChild, Int>>()
                val list2 = listOf<Pair<DomainUrlChild, Int>>()

                whenever(repository.getLastSearchResults()).thenReturn(Flowable.just(list1, list2))

                GetLastSearchResultsInteractor(Lazy { repository }).execute { it }.test().await().assertValues(list1, list2)

                verify(repository).getLastSearchResults()
            }
        }

        after {
            Mockito.framework().clearInlineMocks()
        }
    }
})
