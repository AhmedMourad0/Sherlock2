package inc.ahmedmourad.sherlock.children

import arrow.core.Tuple2
import com.nhaarman.mockitokotlin2.*
import dagger.Lazy
import inc.ahmedmourad.sherlock.children.repository.SherlockChildrenRepository
import inc.ahmedmourad.sherlock.children.repository.dependencies.ChildrenLocalRepository
import inc.ahmedmourad.sherlock.children.repository.dependencies.ChildrenRemoteRepository
import inc.ahmedmourad.sherlock.domain.bus.Bus
import inc.ahmedmourad.sherlock.domain.constants.*
import inc.ahmedmourad.sherlock.domain.filter.Filter
import inc.ahmedmourad.sherlock.domain.filter.criteria.DomainChildCriteriaRules
import inc.ahmedmourad.sherlock.domain.model.children.*
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Single
import org.junit.Assert.assertSame
import org.mockito.Mockito
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import java.util.*

//TODO: replace all asserts with assert that
object SherlockRepositoryUnitTests : Spek({

    describe("SherlockChildrenRepository") {

        val localRepository by memoized { mock<ChildrenLocalRepository>() }
        val remoteRepository by memoized { mock<ChildrenRemoteRepository>() }
        val bus by memoized {
            mock<Bus> {
                on { childPublishingState } doReturn mock()
                on { childFindingState } doReturn mock()
                on { childrenFindingState } doReturn mock()
            }
        }
        val sherlockRepository by memoized {
            SherlockChildrenRepository(
                    Lazy { localRepository },
                    Lazy { remoteRepository },
                    Lazy { bus }
            )
        }

        describe("publish") {

            it("should call publish on remote repository with the same parameters") {

                val publishedChild = DomainPublishedChild(
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

                val retrievedChild = DomainRetrievedChild(
                        UUID.randomUUID().toString(),
                        System.currentTimeMillis(),
                        publishedChild.name,
                        publishedChild.notes,
                        publishedChild.location,
                        publishedChild.appearance,
                        "url"
                )

                whenever(remoteRepository.publish(publishedChild)).thenReturn(Single.just(retrievedChild))

                sherlockRepository.publish(publishedChild)
                        .test()
                        .await()
                        .assertComplete()
                        .assertNoErrors()
                        .assertValue(retrievedChild)

                verify(remoteRepository).publish(publishedChild)

                verify(bus.childPublishingState) {
                    1 * { accept(eq(PublishingState.Success(retrievedChild))) }
                    1 * { accept(eq(PublishingState.Ongoing(publishedChild))) }
                    0 * { accept(eq(PublishingState.Failure(publishedChild))) }
                }
            }
        }

        describe("findAll") {

            it("should call findAll on remote repository with the same parameters and return its results") {

                val filter = mock<Filter<DomainRetrievedChild>>()
                val list = listOf<Tuple2<DomainRetrievedChild, Int>>()
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

                whenever(remoteRepository.findAll(rules, filter)).thenReturn(Flowable.just(list).map { Either.Value(it) })
                whenever(localRepository.replaceAll(list)).thenReturn(Single.just(list.map { it.first.simplify() to it.second }))

                sherlockRepository.findAll(rules, filter)
                        .test()
                        .awaitCount(1)
                        .assertNoErrors()
                        .assertValue(Either.Value(list.map { it.first.simplify() to it.second }))

                verify(remoteRepository).findAll(rules, filter)

                verify(bus.childrenFindingState) {
                    1 * { accept(BackgroundState.ONGOING) }
                    1 * { accept(BackgroundState.SUCCESS) }
                    0 * { accept(BackgroundState.FAILURE) }
                }
            }

            it("should call replaceAll on local repository with the findAll results") {

                val filter = mock<Filter<DomainRetrievedChild>>()
                val list = listOf<Pair<DomainRetrievedChild, Int>>()
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

                whenever(remoteRepository.findAll(rules, filter)).thenReturn(Flowable.just(list).map { Either.Value(it) })
                whenever(localRepository.replaceAll(any())).thenReturn(Single.just(list.map { it.first.simplify() to it.second }))

                sherlockRepository.findAll(rules, filter)
                        .test()
                        .await()
                        .assertSubscribed()
                        .assertNoErrors()
                        .assertComplete()

                verify(localRepository) {
                    1 * { replaceAll(list) }
                }

                verify(bus.childrenFindingState) {
                    1 * { accept(BackgroundState.ONGOING) }
                    1 * { accept(BackgroundState.SUCCESS) }
                    0 * { accept(BackgroundState.FAILURE) }
                }
            }
        }

        describe("find") {

            it("should call find on remote repository with the same parameters and return its result") {

                val child = DomainRetrievedChild(
                        UUID.randomUUID().toString(),
                        System.currentTimeMillis(),
                        DomainName("", ""),
                        "",
                        DomainLocation("", "", "", DomainCoordinates(50.0, 50.0)),
                        DomainEstimatedAppearance(
                                Gender.MALE,
                                Skin.DARK,
                                Hair.BROWN,
                                DomainRange(10, 15),
                                DomainRange(100, 150)
                        ), "url"
                )

                val childResult = child to 98

                whenever(remoteRepository.find(child.simplify())).thenReturn(Flowable.just(child).map { Either.Value(it) })
                whenever(localRepository.updateIfExists(child)).thenReturn(Maybe.just(childResult))

                sherlockRepository.find(child.simplify())
                        .test()
                        .awaitCount(1)
                        .assertNoErrors()
                        .assertValue(Either.Value(childResult))

                verify(remoteRepository) {
                    1 * { find(child.simplify()) }
                }

                verify(bus.childFindingState) {
                    1 * { accept(BackgroundState.ONGOING) }
                    1 * { accept(BackgroundState.SUCCESS) }
                    0 * { accept(BackgroundState.FAILURE) }
                }

                val nullResult = Either.NULL

                whenever(remoteRepository.find(child.simplify())).thenReturn(Flowable.just(nullResult))

                sherlockRepository.find(child.simplify())
                        .test()
                        .awaitCount(1)
                        .assertNoErrors()
                        .assertValue(nullResult)

                verify(remoteRepository) {
                    2 * { find(child.simplify()) }
                }

                verify(bus.childFindingState) {
                    2 * { accept(BackgroundState.ONGOING) }
                    2 * { accept(BackgroundState.SUCCESS) }
                    0 * { accept(BackgroundState.FAILURE) }
                }
            }

            it("should call updateIfExists on local repository with the find result") {

                val child = DomainRetrievedChild(
                        UUID.randomUUID().toString(),
                        System.currentTimeMillis(),
                        DomainName("", ""),
                        "",
                        DomainLocation("", "", "", DomainCoordinates(50.0, 50.0)),
                        DomainEstimatedAppearance(
                                Gender.MALE,
                                Skin.DARK,
                                Hair.BROWN,
                                DomainRange(10, 15),
                                DomainRange(100, 150)
                        ), "url"
                )

                val childResult = child to 98

                whenever(remoteRepository.find(child.simplify())).thenReturn(Flowable.just(child).map { Either.Value(it) })
                whenever(localRepository.updateIfExists(child)).thenReturn(Maybe.just(childResult))

                sherlockRepository.find(child.simplify())
                        .test()
                        .awaitCount(1)
                        .assertNoErrors()
                        .assertValue(Either.Value(childResult))

                verify(remoteRepository) {
                    1 * { find(child.simplify()) }
                }

                verify(localRepository) {
                    1 * { updateIfExists(child) }
                }

                verify(bus.childFindingState) {
                    1 * { accept(BackgroundState.ONGOING) }
                    1 * { accept(BackgroundState.SUCCESS) }
                    0 * { accept(BackgroundState.FAILURE) }
                }

                val nullResult = Either.NULL

                whenever(remoteRepository.find(child.simplify())).thenReturn(Flowable.just(nullResult))

                sherlockRepository.find(child.simplify())
                        .test()
                        .awaitCount(1)
                        .assertNoErrors()
                        .assertValue(nullResult)

                verify(remoteRepository) {
                    2 * { find(child.simplify()) }
                }

                verify(localRepository) {
                    1 * { updateIfExists(any()) }
                }

                verify(bus.childFindingState) {
                    2 * { accept(BackgroundState.ONGOING) }
                    2 * { accept(BackgroundState.SUCCESS) }
                    0 * { accept(BackgroundState.FAILURE) }
                }
            }
        }

        describe("findLastSearchResults") {

            it("should return flowable with the right values") {

                val list = listOf<Pair<DomainRetrievedChild, Int>>()

                whenever(localRepository.findAll()).thenReturn(Flowable.fromArray(list.map { it.first.simplify() to it.second }))

                sherlockRepository.findLastSearchResults().test().await().assertValues(list.map { it.first.simplify() to it.second })

                verify(localRepository).findAll()
            }
        }

        describe("test") {
            it("should return same SherlockTest object on every call") {
                assertSame(sherlockRepository.test(), sherlockRepository.test())
            }
        }

        describe("SherlockTest") {

            describe("clear") {

                it("should call clear on local and remote repositories") {

                    whenever(remoteRepository.clear()).thenReturn(Completable.complete())
                    whenever(localRepository.clear()).thenReturn(Completable.complete())

                    sherlockRepository.test().clear()

                    verify(localRepository) {
                        1 * { clear() }
                    }

                    verify(remoteRepository) {
                        1 * { clear() }
                    }
                }
            }
        }

        after {
            Mockito.framework().clearInlineMocks()
        }
    }
})
