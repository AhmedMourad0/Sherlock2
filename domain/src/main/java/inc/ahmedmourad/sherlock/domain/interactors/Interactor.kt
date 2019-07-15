package inc.ahmedmourad.sherlock.domain.interactors

//TODO: replace execute with the invoke operator
interface Interactor<out R> {
    fun execute(): R
}
