package inc.ahmedmourad.sherlock

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import inc.ahmedmourad.sherlock.domain.model.DomainRefreshableResults
import inc.ahmedmourad.sherlock.domain.model.DomainUrlChild
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.observers.TestObserver
import org.mockito.Mockito

import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

object DomainRefreshableResultsUnitTests : Spek({

    val results by memoized { Flowable.empty<List<Pair<DomainUrlChild, Int>>>() }

    val refresh by memoized { mock<() -> Completable> { on { it() } doReturn Completable.complete() } }

    describe("DomainRefreshableResults") {

        it("should call our refresh when constructed") {

            DomainRefreshableResults(results, refresh)

            verify(refresh)()
        }

        it("should subscribe to the result of our refresh when constructed") {

            val testObserver = TestObserver.create<Unit>()
            val completable = Completable.complete()
                    .doOnSubscribe { testObserver.onSubscribe(it) }
                    .doOnComplete { testObserver.onComplete() }
                    .doOnError { testObserver.onError(it) }

            DomainRefreshableResults(results) { completable }

            testObserver.await().assertSubscribed()
        }

        it("should call our refresh when its refresh is called") {

            DomainRefreshableResults(results, refresh).refresh()

            verify(refresh) {
                2 * { refresh() }
            }
        }

        after {
            Mockito.framework().clearInlineMocks()
        }
    }
})
