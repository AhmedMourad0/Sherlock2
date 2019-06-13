package inc.ahmedmourad.sherlock.domain.interactors

interface Interactor<out R> {
    fun execute(): R
}
