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
import inc.ahmedmourad.sherlock.domain.interactors.children.AddChildInteractor
import inc.ahmedmourad.sherlock.domain.interactors.children.FindChildrenInteractor
import inc.ahmedmourad.sherlock.domain.interactors.children.FindLastSearchResultsInteractor
import inc.ahmedmourad.sherlock.domain.model.children.*
import inc.ahmedmourad.sherlock.domain.model.children.Range
import inc.ahmedmourad.sherlock.domain.model.children.submodel.*
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

                val child = PublishedChild(
                        FullName("", ""),
                        "",
                        Location("", "", "", Coordinates(50.0, 50.0)),
                        ApproximateAppearance(
                                Gender.MALE,
                                Skin.DARK,
                                Hair.BROWN,
                                Range(10, 15),
                                Range(100, 150)
                        ), ByteArray(0)
                )

                val returnedChild = RetrievedChild(
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

                val filter = mock<Filter<RetrievedChild>>()
                val result = Flowable.empty<List<Tuple2<SimpleRetrievedChild, Int>>>()
                val rules = ChildQuery(
                        FullName("", ""),
                        Location("", "", "", Coordinates(50.0, 40.0)),
                        ExactAppearance(
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

                val list1 = listOf<Tuple2<SimpleRetrievedChild, Int>>()
                val list2 = listOf<Tuple2<SimpleRetrievedChild, Int>>()

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
