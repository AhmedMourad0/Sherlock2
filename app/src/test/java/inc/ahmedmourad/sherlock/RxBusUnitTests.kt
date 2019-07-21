package inc.ahmedmourad.sherlock

import inc.ahmedmourad.sherlock.domain.bus.Bus
import inc.ahmedmourad.sherlock.domain.bus.RxBus
import org.mockito.Mockito
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

object RxBusUnitTests : Spek({

    describe("RxBus") {

        val rxBus by memoized { RxBus() }

        describe("RxState") {

            it("should send the objects through the publishing state relay when updated") {

                val publishingTest = rxBus.publishingState.get().test()

                rxBus.publishingState.notify(Bus.PublishingState.FAILURE)
                rxBus.publishingState.notify(Bus.PublishingState.ONGOING)

                publishingTest.assertValues(Bus.PublishingState.FAILURE, Bus.PublishingState.ONGOING)

                rxBus.publishingState.notify(Bus.PublishingState.SUCCESS)

                publishingTest.assertValues(
                        Bus.PublishingState.FAILURE,
                        Bus.PublishingState.ONGOING,
                        Bus.PublishingState.SUCCESS
                )
            }
        }

        after {
            Mockito.framework().clearInlineMocks()
        }
    }
})
