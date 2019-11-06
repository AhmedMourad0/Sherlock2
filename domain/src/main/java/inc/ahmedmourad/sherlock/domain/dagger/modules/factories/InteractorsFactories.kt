package inc.ahmedmourad.sherlock.domain.dagger.modules.factories

import dagger.Lazy
import inc.ahmedmourad.sherlock.domain.bus.Bus
import inc.ahmedmourad.sherlock.domain.data.ChildrenRepository
import inc.ahmedmourad.sherlock.domain.filter.Filter
import inc.ahmedmourad.sherlock.domain.filter.criteria.DomainChildCriteriaRules
import inc.ahmedmourad.sherlock.domain.interactors.*
import inc.ahmedmourad.sherlock.domain.model.*
import inc.ahmedmourad.sherlock.domain.platform.ConnectivityManager
import io.reactivex.Flowable
import io.reactivex.Single

interface AddChildInteractorAbstractFactory {
    fun create(child: DomainPublishedChild): Interactor<Single<DomainRetrievedChild>>
}

internal class AddChildInteractorFactory(private val childrenRepository: Lazy<ChildrenRepository>) : AddChildInteractorAbstractFactory {
    override fun create(child: DomainPublishedChild): Interactor<Single<DomainRetrievedChild>> {
        return AddChildInteractor(childrenRepository, child)
    }
}

interface FindChildrenInteractorAbstractFactory {
    fun create(rules: DomainChildCriteriaRules, filter: Filter<DomainRetrievedChild>): Interactor<Flowable<Either<List<Pair<DomainSimpleRetrievedChild, Int>>, Throwable>>>
}

internal class FindChildrenInteractorFactory(private val childrenRepository: Lazy<ChildrenRepository>) : FindChildrenInteractorAbstractFactory {
    override fun create(rules: DomainChildCriteriaRules, filter: Filter<DomainRetrievedChild>): Interactor<Flowable<Either<List<Pair<DomainSimpleRetrievedChild, Int>>, Throwable>>> {
        return FindChildrenInteractor(childrenRepository, rules, filter)
    }
}

interface FindChildInteractorAbstractFactory {
    fun create(child: DomainSimpleRetrievedChild): Interactor<Flowable<Either<Pair<DomainRetrievedChild, Int>?, Throwable>>>
}

internal class FindChildInteractorFactory(private val childrenRepository: Lazy<ChildrenRepository>) : FindChildInteractorAbstractFactory {
    override fun create(child: DomainSimpleRetrievedChild): Interactor<Flowable<Either<Pair<DomainRetrievedChild, Int>?, Throwable>>> {
        return FindChildInteractor(childrenRepository, child)
    }
}

interface FindLastSearchResultsInteractorAbstractFactory {
    fun create(): Interactor<Flowable<List<Pair<DomainSimpleRetrievedChild, Int>>>>
}

internal class FindLastSearchResultsInteractorFactory(private val childrenRepository: Lazy<ChildrenRepository>) : FindLastSearchResultsInteractorAbstractFactory {
    override fun create(): Interactor<Flowable<List<Pair<DomainSimpleRetrievedChild, Int>>>> {
        return FindLastSearchResultsInteractor(childrenRepository)
    }
}

interface ObserveInternetConnectivityInteractorAbstractFactory {
    fun create(): Interactor<Flowable<Boolean>>
}

internal class ObserveInternetConnectivityInteractorFactory(private val connectivityManager: Lazy<ConnectivityManager>) : ObserveInternetConnectivityInteractorAbstractFactory {
    override fun create(): Interactor<Flowable<Boolean>> {
        return ObserveInternetConnectivityInteractor(connectivityManager)
    }
}

interface CheckInternetConnectivityInteractorAbstractFactory {
    fun create(): Interactor<Single<Boolean>>
}

internal class CheckInternetConnectivityInteractorFactory(private val connectivityManager: Lazy<ConnectivityManager>) : CheckInternetConnectivityInteractorAbstractFactory {
    override fun create(): Interactor<Single<Boolean>> {
        return CheckInternetConnectivityInteractor(connectivityManager)
    }
}

interface ObservePublishingStateInteractorAbstractFactory {
    fun create(): Interactor<Flowable<Bus.PublishingState<*>>>
}

internal class ObservePublishingStateInteractorFactory(private val bus: Lazy<Bus>) : ObservePublishingStateInteractorAbstractFactory {
    override fun create(): Interactor<Flowable<Bus.PublishingState<*>>> {
        return ObservePublishingStateInteractor(bus)
    }
}

interface CheckPublishingStateInteractorAbstractFactory {
    fun create(): Interactor<Single<Optional<Bus.PublishingState<*>>>>
}

internal class CheckPublishingStateInteractorFactory(private val bus: Lazy<Bus>) : CheckPublishingStateInteractorAbstractFactory {
    override fun create(): Interactor<Single<Optional<Bus.PublishingState<*>>>> {
        return CheckPublishingStateInteractor(bus)
    }
}
