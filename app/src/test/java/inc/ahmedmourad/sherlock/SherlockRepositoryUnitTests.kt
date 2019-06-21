package inc.ahmedmourad.sherlock

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import dagger.Lazy
import inc.ahmedmourad.sherlock.data.repositories.CloudRepository
import inc.ahmedmourad.sherlock.data.repositories.LocalRepository
import inc.ahmedmourad.sherlock.data.repositories.SherlockRepository
import inc.ahmedmourad.sherlock.domain.constants.Gender
import inc.ahmedmourad.sherlock.domain.constants.Hair
import inc.ahmedmourad.sherlock.domain.constants.Skin
import inc.ahmedmourad.sherlock.domain.filter.Filter
import inc.ahmedmourad.sherlock.domain.filter.criteria.DomainChildCriteriaRules
import inc.ahmedmourad.sherlock.domain.model.*
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.observers.TestObserver
import org.mockito.Mockito
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

//TODO: replace all asserts with assert that
object SherlockRepositoryUnitTests : Spek({

    describe("SherlockRepository") {

        val localRepository by memoized { mock<LocalRepository>() }
        val cloudRepository by memoized { mock<CloudRepository>() }
        val sherlockRepository by memoized { SherlockRepository(Lazy { localRepository }, Lazy { cloudRepository }) }

        describe("publish") {

            it("should call publish on cloud repository with the same parameters") {

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

                whenever(cloudRepository.publish(child)).thenReturn(Single.just(returnedChild))

                sherlockRepository.publish(child)
                        .test()
                        .await()
                        .assertComplete()
                        .assertNoErrors()
                        .assertValue(returnedChild)

                verify(cloudRepository).publish(child)
            }
        }

        describe("find") {

            it("should call get results on local repository") {

                val filter = mock<Filter<DomainUrlChild>>()
                val list = listOf<Pair<DomainUrlChild, Int>>()
                val rules = DomainChildCriteriaRules(
                        DomainName("", ""),
                        DomainLocation("", "", "", DomainCoordinates(50.0, 40.0)),
                        DomainAppearance(
                                Gender.MALE,
                        Skin.WHEAT,
                        Hair.DARK,
                        20,
                        180
                        )
                )

                whenever(cloudRepository.find(rules, filter)).thenReturn(Flowable.just(list))
                whenever(localRepository.getResults()).thenReturn(Flowable.empty())
                whenever(localRepository.replaceResults(list)).thenReturn(Completable.complete())

                sherlockRepository.find(rules, filter)

                verify(localRepository).getResults()
            }

            it("should call find on cloud repository with the same parameters") {

                val filter = mock<Filter<DomainUrlChild>>()
                val list = listOf<Pair<DomainUrlChild, Int>>()
                val rules = DomainChildCriteriaRules(
                        DomainName("", ""),
                        DomainLocation("", "", "", DomainCoordinates(50.0, 40.0)),
                        DomainAppearance(
                                Gender.MALE,
                        Skin.WHEAT,
                        Hair.DARK,
                        20,
                        180
                        )
                )

                whenever(cloudRepository.find(rules, filter)).thenReturn(Flowable.just(list))
                whenever(localRepository.getResults()).thenReturn(Flowable.empty())
                whenever(localRepository.replaceResults(list)).thenReturn(Completable.complete())

                sherlockRepository.find(rules, filter)

                verify(cloudRepository).find(rules, filter)
            }

            it("should call replace results on local repository with the find results") {

                val filter = mock<Filter<DomainUrlChild>>()
                val list = listOf<Pair<DomainUrlChild, Int>>()
                val rules = DomainChildCriteriaRules(
                        DomainName("", ""),
                        DomainLocation("", "", "", DomainCoordinates(50.0, 40.0)),
                        DomainAppearance(
                                Gender.MALE,
                        Skin.WHEAT,
                        Hair.DARK,
                        20,
                        180
                        )
                )
                val testObserver = TestObserver.create<Unit>()
                val completable = Completable.complete()
                        .doOnSubscribe { testObserver.onSubscribe(it) }
                        .doOnError { testObserver.onError(it) }
                        .doOnComplete { testObserver.onComplete() }

                whenever(cloudRepository.find(rules, filter)).thenReturn(Flowable.just(list))
                whenever(localRepository.getResults()).thenReturn(Flowable.empty())
                whenever(localRepository.replaceResults(any())).thenReturn(completable)

                sherlockRepository.find(rules, filter)

                testObserver.await().assertSubscribed().assertNoErrors().assertComplete()

                verify(localRepository) {
                    1 * { replaceResults(list) }
                }
            }

            it("should return domain search results with the right values") {

                val filter = mock<Filter<DomainUrlChild>>()
                val list = listOf<Pair<DomainUrlChild, Int>>()
                val rules = DomainChildCriteriaRules(
                        DomainName("", ""),
                        DomainLocation("", "", "", DomainCoordinates(50.0, 40.0)),
                        DomainAppearance(
                                Gender.MALE,
                        Skin.WHEAT,
                        Hair.DARK,
                        20,
                        180
                        )
                )

                val initialList = listOf<Pair<DomainUrlChild, Int>>()

                whenever(cloudRepository.find(rules, filter)).thenReturn(Flowable.just(list))
                whenever(localRepository.getResults()).thenReturn(Flowable.fromArray(initialList))
                whenever(localRepository.replaceResults(list)).thenReturn(Completable.complete())

                val result = sherlockRepository.find(rules, filter)

                result.test().awaitCount(1).assertValues(initialList)

                verify(cloudRepository) {
                    1 * { find(rules, filter) }
                }
                verify(localRepository) {
                    1 * { replaceResults(list) }
                }
            }
        }

        describe("getLastSearchResults") {

            it("should return domain search results with the right values") {

                val list = listOf<Pair<DomainUrlChild, Int>>()

                whenever(localRepository.getResults()).thenReturn(Flowable.fromArray(list))

                sherlockRepository.getLastSearchResults().test().await().assertValues(list)

                verify(localRepository).getResults()
            }
        }

        after {
            Mockito.framework().clearInlineMocks()
        }
    }
})
