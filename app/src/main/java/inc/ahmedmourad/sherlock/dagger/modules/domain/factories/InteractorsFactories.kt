package inc.ahmedmourad.sherlock.dagger.modules.domain.factories

import dagger.Lazy
import inc.ahmedmourad.sherlock.domain.filter.Filter
import inc.ahmedmourad.sherlock.domain.filter.criteria.DomainChildCriteriaRules
import inc.ahmedmourad.sherlock.domain.interactors.AddChildInteractor
import inc.ahmedmourad.sherlock.domain.interactors.FindChildInteractor
import inc.ahmedmourad.sherlock.domain.interactors.FindChildrenInteractor
import inc.ahmedmourad.sherlock.domain.interactors.Interactor
import inc.ahmedmourad.sherlock.domain.model.DomainPictureChild
import inc.ahmedmourad.sherlock.domain.model.DomainUrlChild
import inc.ahmedmourad.sherlock.domain.model.Optional
import inc.ahmedmourad.sherlock.domain.repository.Repository
import io.reactivex.Flowable
import io.reactivex.Single

interface AddChildInteractorAbstractFactory {
    fun create(child: DomainPictureChild): Interactor<Single<DomainUrlChild>>
}

class AddChildInteractorFactory(private val repository: Lazy<Repository>) : AddChildInteractorAbstractFactory {
    override fun create(child: DomainPictureChild): Interactor<Single<DomainUrlChild>> {
        return AddChildInteractor(repository, child)
    }
}

interface FindChildrenInteractorAbstractFactory {
    fun create(rules: DomainChildCriteriaRules, filter: Filter<DomainUrlChild>): Interactor<Flowable<List<Pair<DomainUrlChild, Int>>>>
}

class FindChildrenInteractorFactory(private val repository: Lazy<Repository>) : FindChildrenInteractorAbstractFactory {
    override fun create(rules: DomainChildCriteriaRules, filter: Filter<DomainUrlChild>): Interactor<Flowable<List<Pair<DomainUrlChild, Int>>>> {
        return FindChildrenInteractor(repository, rules, filter)
    }
}

interface FindChildInteractorAbstractFactory {
    fun create(childId: String): Interactor<Flowable<Optional<Pair<DomainUrlChild, Int>>>>
}

class FindChildInteractorFactory(private val repository: Lazy<Repository>) : FindChildInteractorAbstractFactory {
    override fun create(childId: String): Interactor<Flowable<Optional<Pair<DomainUrlChild, Int>>>> {
        return FindChildInteractor(repository, childId)
    }
}
