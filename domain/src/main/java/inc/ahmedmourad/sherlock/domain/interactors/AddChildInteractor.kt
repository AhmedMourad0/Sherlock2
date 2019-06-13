package inc.ahmedmourad.sherlock.domain.interactors

import dagger.Lazy
import inc.ahmedmourad.sherlock.domain.model.DomainPictureChild
import inc.ahmedmourad.sherlock.domain.model.DomainUrlChild
import inc.ahmedmourad.sherlock.domain.repository.Repository
import io.reactivex.Single

class AddChildInteractor(private val repository: Lazy<Repository>,
                         private val child: DomainPictureChild) : Interactor<Single<DomainUrlChild>> {
    override fun execute(): Single<DomainUrlChild> {
        return repository.get().publish(child)
    }
}
