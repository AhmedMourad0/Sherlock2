package inc.ahmedmourad.sherlock.domain

import com.nhaarman.mockitokotlin2.mock
import inc.ahmedmourad.sherlock.domain.bus.Bus
import inc.ahmedmourad.sherlock.domain.bus.RxBus
import org.mockito.Mockito
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

object RxBusUnitTests : Spek({

    describe("RxBus") {

        val rxBus by memoized { RxBus() }

        describe("childPublishingState") {

            it("should send the objects through the relay when notified") {

                val publishingTest = rxBus.childPublishingState.get().test()

                val success = Bus.PublishingState.Success(mock())
                val ongoing = Bus.PublishingState.Ongoing(mock())
                val failure = Bus.PublishingState.Failure(mock())

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

                rxBus.childFindingState.accept(Bus.BackgroundState.FAILURE)
                rxBus.childFindingState.accept(Bus.BackgroundState.ONGOING)

                publishingTest.assertValues(Bus.BackgroundState.FAILURE, Bus.BackgroundState.ONGOING)

                rxBus.childFindingState.accept(Bus.BackgroundState.SUCCESS)

                publishingTest.assertValues(
                        Bus.BackgroundState.FAILURE,
                        Bus.BackgroundState.ONGOING,
                        Bus.BackgroundState.SUCCESS
                )
            }
        }

        describe("childrenFindingState") {

            it("should send the objects through the relay when notified") {

                val publishingTest = rxBus.childrenFindingState.get().test()

                rxBus.childrenFindingState.accept(Bus.BackgroundState.FAILURE)
                rxBus.childrenFindingState.accept(Bus.BackgroundState.ONGOING)

                publishingTest.assertValues(Bus.BackgroundState.FAILURE, Bus.BackgroundState.ONGOING)

                rxBus.childrenFindingState.accept(Bus.BackgroundState.SUCCESS)

                publishingTest.assertValues(
                        Bus.BackgroundState.FAILURE,
                        Bus.BackgroundState.ONGOING,
                        Bus.BackgroundState.SUCCESS
                )
            }
        }

        after {
            Mockito.framework().clearInlineMocks()
        }
    }
})
