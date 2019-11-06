package inc.ahmedmourad.sherlock.domain.interactors

import dagger.Lazy
import inc.ahmedmourad.sherlock.domain.data.ChildrenRepository
import inc.ahmedmourad.sherlock.domain.model.DomainRetrievedChild
import inc.ahmedmourad.sherlock.domain.model.DomainSimpleRetrievedChild
import inc.ahmedmourad.sherlock.domain.model.Either
import io.reactivex.Flowable

internal class FindChildInteractor(
        private val childrenRepository: Lazy<ChildrenRepository>,
        private val child: DomainSimpleRetrievedChild) : Interactor<Flowable<Either<Pair<DomainRetrievedChild, Int>?, Throwable>>> {
    override fun execute(): Flowable<Either<Pair<DomainRetrievedChild, Int>?, Throwable>> {
        return childrenRepository.get().find(child)
    }
}
