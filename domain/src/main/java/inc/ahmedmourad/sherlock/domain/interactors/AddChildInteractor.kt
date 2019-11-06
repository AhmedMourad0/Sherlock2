package inc.ahmedmourad.sherlock.domain.interactors

import dagger.Lazy
import inc.ahmedmourad.sherlock.domain.data.ChildrenRepository
import inc.ahmedmourad.sherlock.domain.model.DomainPublishedChild
import inc.ahmedmourad.sherlock.domain.model.DomainRetrievedChild
import io.reactivex.Single

internal class AddChildInteractor(private val childrenRepository: Lazy<ChildrenRepository>,
                                  private val child: DomainPublishedChild) : Interactor<Single<DomainRetrievedChild>> {
    override fun execute(): Single<DomainRetrievedChild> {
        return childrenRepository.get().publish(child)
    }
}
