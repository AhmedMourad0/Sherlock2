package inc.ahmedmourad.sherlock.domain.interactors

import dagger.Lazy
import inc.ahmedmourad.sherlock.domain.model.DomainUrlChild
import inc.ahmedmourad.sherlock.domain.model.Optional
import inc.ahmedmourad.sherlock.domain.repository.Repository
import io.reactivex.Flowable

class FindChildInteractor(private val repository: Lazy<Repository>,
                          private val childId: String) : Interactor<Flowable<Optional<Pair<DomainUrlChild, Int>>>> {
    override fun execute(): Flowable<Optional<Pair<DomainUrlChild, Int>>> {
        return repository.get().find(childId)
    }
}
