package inc.ahmedmourad.sherlock

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import dagger.Lazy
import inc.ahmedmourad.sherlock.domain.bus.Bus
import inc.ahmedmourad.sherlock.domain.bus.RxBus
import inc.ahmedmourad.sherlock.domain.device.TextManager
import junit.framework.TestCase.*
import org.mockito.Mockito
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

object RxBusUnitTests : Spek({

    describe("RxBus") {

        val rxBus by memoized { RxBus() }

        describe("RxState") {

            it("should send the objects through the foreground state relay when updated") {

                val idle = Bus.ForegroundState.IDLE
                val loading = Bus.ForegroundState.LOADING
                val error = Bus.ForegroundState.ERROR

                val foregroundTest = rxBus.state.foregroundState.get().test()

                rxBus.state.foregroundState.notify(loading)
                rxBus.state.foregroundState.notify(error)

                foregroundTest.assertValues(loading, error)

                rxBus.state.foregroundState.notify(idle)

                foregroundTest.assertValues(loading, error, idle)
            }

            it("should send the objects through the background state relay when updated") {

                val textManager = mock<TextManager> {
                    on { publishing() } doReturn "publishing"
                    on { publishedSuccessfully() } doReturn "publishedSuccessfully"
                    on { somethingWentWrong() } doReturn "somethingWentWrong"
                }

                val provider = Bus.PublishingState.Provider(Lazy { textManager })

                val failure = provider.failure()
                val success = provider.success()
                val ongoing = provider.ongoing()

                val backgroundTest = rxBus.state.backgroundState.get().test()

                rxBus.state.backgroundState.notify(failure)
                rxBus.state.backgroundState.notify(ongoing)

                backgroundTest.assertValues(failure, ongoing)

                rxBus.state.backgroundState.notify(success)

                backgroundTest.assertValues(failure, ongoing, success)

                verify(textManager).publishing()
                verify(textManager).publishedSuccessfully()
                verify(textManager).somethingWentWrong()

                assertEquals(failure.message, textManager.somethingWentWrong())
                assertEquals(success.message, textManager.publishedSuccessfully())
                assertEquals(ongoing.message, textManager.publishing())

                assertFalse(failure.isIndefinite)
                assertFalse(success.isIndefinite)
                assertTrue(ongoing.isIndefinite)
            }
        }

        describe("RxError") {

            it("should send the objects through the normal errors relay when notified") {

                val error0 = mock<Bus.NormalError>()
                val error1 = mock<Bus.NormalError>()
                val error2 = mock<Bus.NormalError>()

                val errorsTest = rxBus.errors.normalErrors.get().test()

                rxBus.errors.normalErrors.notify(error0)
                rxBus.errors.normalErrors.notify(error2)

                errorsTest.assertValues(error0, error2)

                rxBus.errors.normalErrors.notify(error1)

                errorsTest.assertValues(error0, error2, error1)

                verify(error0).printStackTrace()
                verify(error1).printStackTrace()
                verify(error2).printStackTrace()
            }

            it("should send the objects through the retriable errors relay when notified") {

                val error0 = mock<Bus.RetriableError>()
                val error1 = mock<Bus.RetriableError>()
                val error2 = mock<Bus.RetriableError>()

                val errorsTest = rxBus.errors.retriableErrors.get().test()

                rxBus.errors.retriableErrors.notify(error0)
                rxBus.errors.retriableErrors.notify(error2)

                errorsTest.assertValues(error0, error2)

                rxBus.errors.retriableErrors.notify(error1)

                errorsTest.assertValues(error0, error2, error1)

                verify(error0).printStackTrace()
                verify(error1).printStackTrace()
                verify(error2).printStackTrace()
            }

            it("should send the objects through the recoverable errors relay when notified") {

                val error0 = mock<Bus.RecoverableError>()
                val error1 = mock<Bus.RecoverableError>()
                val error2 = mock<Bus.RecoverableError>()

                val errorsTest = rxBus.errors.recoverableErrors.get().test()

                rxBus.errors.recoverableErrors.notify(error0)
                rxBus.errors.recoverableErrors.notify(error2)

                errorsTest.assertValues(error0, error2)

                rxBus.errors.recoverableErrors.notify(error1)

                errorsTest.assertValues(error0, error2, error1)

                verify(error0).printStackTrace()
                verify(error1).printStackTrace()
                verify(error2).printStackTrace()
            }
        }

        describe("RxWidget") {

            it("should send the objects through the retriable errors relay when notified") {

                val error0 = mock<Bus.RetriableError>()
                val error1 = mock<Bus.RetriableError>()
                val error2 = mock<Bus.RetriableError>()

                val errorsTest = rxBus.widget.retriableErrors.get().test()

                rxBus.widget.retriableErrors.notify(error0)
                rxBus.widget.retriableErrors.notify(error2)

                errorsTest.assertValues(error0, error2)

                rxBus.widget.retriableErrors.notify(error1)

                errorsTest.assertValues(error0, error2, error1)

                verify(error0).printStackTrace()
                verify(error1).printStackTrace()
                verify(error2).printStackTrace()
            }
        }

        describe("NormalError") {

            it("should return the message we supply through the constructor when message is accessed") {

                val normalMessage = "normal"
                val error = Bus.NormalError(normalMessage, Exception())

                assertEquals(error.message, normalMessage)
            }
        }

        describe("RetriableError") {

            it("should call our retry only when its retry is called") {

                val retry = mock<(Throwable) -> Unit>()
                val retriableMessage = "retriable"
                val exception = Exception()
                val error = Bus.RetriableError(retriableMessage, exception, retry)

                verify(retry) {
                    0 * { retry(exception) }
                }

                error.retry()

                verify(retry) {
                    1 * { retry(exception) }
                }

                error.retry()

                verify(retry) {
                    2 * { retry(exception) }
                }

                assertEquals(error.message, retriableMessage)
            }
        }

        describe("RecoverableError") {

            it("should call our recover only when its recover is called") {

                val recover = mock<(Throwable) -> Unit>()
                val recoverableMessage = "recoverable"
                val exception = Exception()
                val error = Bus.RecoverableError(recoverableMessage, exception, recover)

                verify(recover) {
                    0 * { recover(exception) }
                }

                error.recover()

                verify(recover) {
                    1 * { recover(exception) }
                }

                error.recover()

                verify(recover) {
                    2 * { recover(exception) }
                }

                assertEquals(error.message, recoverableMessage)
            }
        }

        after {
            Mockito.framework().clearInlineMocks()
        }
    }
})
