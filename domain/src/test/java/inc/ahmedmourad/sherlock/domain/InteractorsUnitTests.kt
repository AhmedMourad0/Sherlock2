package inc.ahmedmourad.sherlock.domain

import arrow.core.Tuple2
import arrow.core.right
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import dagger.Lazy
import inc.ahmedmourad.sherlock.domain.constants.Gender
import inc.ahmedmourad.sherlock.domain.constants.Hair
import inc.ahmedmourad.sherlock.domain.constants.Skin
import inc.ahmedmourad.sherlock.domain.data.ChildrenRepository
import inc.ahmedmourad.sherlock.domain.filter.Filter
import inc.ahmedmourad.sherlock.domain.filter.criteria.DomainChildCriteriaRules
import inc.ahmedmourad.sherlock.domain.interactors.children.AddChildInteractor
import inc.ahmedmourad.sherlock.domain.interactors.children.FindChildrenInteractor
import inc.ahmedmourad.sherlock.domain.interactors.children.FindLastSearchResultsInteractor
import inc.ahmedmourad.sherlock.domain.model.children.*
import io.reactivex.Flowable
import io.reactivex.Single
import junit.framework.TestCase.assertSame
import org.mockito.Mockito
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import java.util.*

object InteractorsUnitTests : Spek({

    describe("Interactors") {

        val repository by memoized { mock<ChildrenRepository>() }

        describe("AddChildInteractor") {

            it("should call publish on repository when execute is called") {

                val child = DomainPublishedChild(
                        DomainName("", ""),
                        "",
                        DomainLocation("", "", "", DomainCoordinates(50.0, 50.0)),
                        DomainEstimatedAppearance(
                                Gender.MALE,
                                Skin.DARK,
                                Hair.BROWN,
                                DomainRange(10, 15),
                                DomainRange(100, 150)
                        ), ByteArray(0)
                )

                val returnedChild = DomainRetrievedChild(
                        UUID.randomUUID().toString(),
                        System.currentTimeMillis(),
                        child.name,
                        child.notes,
                        child.location,
                        child.appearance,
                        "url"
                )

                whenever(repository.publish(child)).thenReturn(Single.just(returnedChild))

                AddChildInteractor(Lazy { repository }, child)
                        .execute()
                        .test()
                        .await()
                        .assertComplete()
                        .assertNoErrors()
                        .assertValue(returnedChild)

                verify(repository).publish(child)
            }
        }

        describe("FindChildrenInteractor") {

            it("should call findAll on repository when execute is called") {

                val filter = mock<Filter<DomainRetrievedChild>>()
                val result = Flowable.empty<List<Tuple2<DomainSimpleRetrievedChild, Int>>>()
                val rules = DomainChildCriteriaRules(
                        DomainName("", ""),
                        DomainLocation("", "", "", DomainCoordinates(50.0, 40.0)),
                        DomainExactAppearance(
                                Gender.MALE,
                                Skin.WHEAT,
                                Hair.DARK,
                                20,
                                180
                        )
                )

                whenever(repository.findAll(rules, filter)).thenReturn(result.map { it.right() })

                assertSame(
                        FindChildrenInteractor(Lazy { repository }, rules, filter).execute(),
                        result
                )

                verify(repository).findAll(rules, filter)
            }
        }

        describe("FindLastSearchResultsInteractor") {

            it("should call findLastSearchResults on repository when execute is called") {

                val list1 = listOf<Tuple2<DomainSimpleRetrievedChild, Int>>()
                val list2 = listOf<Tuple2<DomainSimpleRetrievedChild, Int>>()

                whenever(repository.findLastSearchResults()).thenReturn(Flowable.just(list1, list2))

                FindLastSearchResultsInteractor(Lazy { repository }).execute().test().await().assertValues(list1, list2)

                verify(repository).findLastSearchResults()
            }
        }

        after {
            Mockito.framework().clearInlineMocks()
        }
    }
})
