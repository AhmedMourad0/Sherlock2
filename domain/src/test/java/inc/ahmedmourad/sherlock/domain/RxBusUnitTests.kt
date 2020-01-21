package inc.ahmedmourad.sherlock.domain

import com.nhaarman.mockitokotlin2.mock
import inc.ahmedmourad.sherlock.domain.bus.RxBus
import inc.ahmedmourad.sherlock.domain.constants.BackgroundState
import inc.ahmedmourad.sherlock.domain.constants.PublishingState
import org.mockito.Mockito
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

object RxBusUnitTests : Spek({

    describe("RxBus") {

        val rxBus by memoized { RxBus() }

        describe("childPublishingState") {

            it("should send the objects through the relay when notified") {

                val publishingTest = rxBus.childPublishingState.get().test()

                val success = PublishingState.Success(mock())
                val ongoing = PublishingState.Ongoing(mock())
                val failure = PublishingState.Failure(mock())

                rxBus.childPublishingState.accept(failure)
                rxBus.childPublishingState.accept(ongoing)

                publishingTest.assertValues(failure, ongoing)

                rxBus.childPublishingState.accept(success)

                publishingTest.assertValues(failure, ongoing, success)
            }
        }

        describe("childFindingState") {

            it("should send the objects through the relay when notified") {

                val publishingTest = rxBus.childFindingState.get().test()

                rxBus.childFindingState.accept(BackgroundState.FAILURE)
                rxBus.childFindingState.accept(BackgroundState.ONGOING)

                publishingTest.assertValues(BackgroundState.FAILURE, BackgroundState.ONGOING)

                rxBus.childFindingState.accept(BackgroundState.SUCCESS)

                publishingTest.assertValues(
                        BackgroundState.FAILURE,
                        BackgroundState.ONGOING,
                        BackgroundState.SUCCESS
                )
            }
        }

        describe("childrenFindingState") {

            it("should send the objects through the relay when notified") {

                val publishingTest = rxBus.childrenFindingState.get().test()

                rxBus.childrenFindingState.accept(BackgroundState.FAILURE)
                rxBus.childrenFindingState.accept(BackgroundState.ONGOING)

                publishingTest.assertValues(BackgroundState.FAILURE, BackgroundState.ONGOING)

                rxBus.childrenFindingState.accept(BackgroundState.SUCCESS)

                publishingTest.assertValues(
                        BackgroundState.FAILURE,
                        BackgroundState.ONGOING,
                        BackgroundState.SUCCESS
                )
            }
        }

        after {
            Mockito.framework().clearInlineMocks()
        }
    }
})
