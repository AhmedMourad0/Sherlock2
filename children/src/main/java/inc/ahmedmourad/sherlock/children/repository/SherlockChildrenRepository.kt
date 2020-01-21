package inc.ahmedmourad.sherlock.children.repository

import arrow.core.*
import dagger.Lazy
import inc.ahmedmourad.sherlock.children.repository.dependencies.ChildrenLocalRepository
import inc.ahmedmourad.sherlock.children.repository.dependencies.ChildrenRemoteRepository
import inc.ahmedmourad.sherlock.domain.constants.BackgroundState
import inc.ahmedmourad.sherlock.domain.constants.PublishingState
import inc.ahmedmourad.sherlock.domain.data.ChildrenRepository
import inc.ahmedmourad.sherlock.domain.filter.Filter
import inc.ahmedmourad.sherlock.domain.filter.criteria.DomainChildCriteriaRules
import inc.ahmedmourad.sherlock.domain.interactors.core.NotifyChildFindingStateChangeInteractor
import inc.ahmedmourad.sherlock.domain.interactors.core.NotifyChildPublishingStateChangeInteractor
import inc.ahmedmourad.sherlock.domain.interactors.core.NotifyChildrenFindingStateChangeInteractor
import inc.ahmedmourad.sherlock.domain.model.children.DomainPublishedChild
import inc.ahmedmourad.sherlock.domain.model.children.DomainRetrievedChild
import inc.ahmedmourad.sherlock.domain.model.children.DomainSimpleRetrievedChild
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

//TODO: if requireUserSignedIn doesn't fail and user is not signed in with FirebaseAuth
// implement a fallback mechanism and sign the user in anonymously
internal class SherlockChildrenRepository(
        private val childrenLocalRepository: Lazy<ChildrenLocalRepository>,
        private val childrenRemoteRepository: Lazy<ChildrenRemoteRepository>,
        private val notifyChildPublishingStateChangeInteractor: NotifyChildPublishingStateChangeInteractor,
        private val notifyChildFindingStateChangeInteractor: NotifyChildFindingStateChangeInteractor,
        private val notifyChildrenFindingStateChangeInteractor: NotifyChildrenFindingStateChangeInteractor
) : ChildrenRepository {

    private val tester by lazy { SherlockTester(childrenRemoteRepository, childrenLocalRepository) }

    override fun publish(child: DomainPublishedChild): Single<Either<Throwable, DomainRetrievedChild>> {
        return childrenRemoteRepository.get()
                .publish(child)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .doOnSuccess { childEither ->
                    childEither.fold(ifLeft = {
                        notifyChildPublishingStateChangeInteractor(PublishingState.Failure(child))
                    }, ifRight = {
                        notifyChildPublishingStateChangeInteractor(PublishingState.Success(it))
                    })
                }.doOnSubscribe { notifyChildPublishingStateChangeInteractor(PublishingState.Ongoing(child)) }
                .doOnError { notifyChildPublishingStateChangeInteractor(PublishingState.Failure(child)) }
    }

    override fun find(
            child: DomainSimpleRetrievedChild
    ): Flowable<Either<Throwable, Option<Tuple2<DomainRetrievedChild, Int>>>> {
        return childrenRemoteRepository.get()
                .find(child)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .flatMap { childEither ->
                    childEither.fold(ifLeft = {
                        Flowable.just(it.left())
                    }, ifRight = { childOption ->
                        childOption.fold(ifEmpty = {
                            Flowable.just(none<Tuple2<DomainRetrievedChild, Int>>().right())
                        }, ifSome = { child ->
                            childrenLocalRepository.get()
                                    .updateIfExists(child)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(Schedulers.io())
                                    .map { it.some().right() }
                                    .toSingle((child toT -1).some().right())
                                    .toFlowable()
                        })
                    })
                }.doOnSubscribe { notifyChildFindingStateChangeInteractor(BackgroundState.ONGOING) }
                .doOnNext { notifyChildFindingStateChangeInteractor(BackgroundState.SUCCESS) }
                .doOnError { notifyChildFindingStateChangeInteractor(BackgroundState.FAILURE) }
    }

    override fun findAll(
            rules: DomainChildCriteriaRules,
            filter: Filter<DomainRetrievedChild>
    ): Flowable<Either<Throwable, List<Tuple2<DomainSimpleRetrievedChild, Int>>>> {
        return childrenRemoteRepository.get()
                .findAll(rules, filter)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .flatMap { resultsEither ->
                    resultsEither.fold(ifLeft = {
                        Flowable.just(it.left())
                    }, ifRight = { results ->
                        childrenLocalRepository.get()
                                .replaceAll(results)
                                .subscribeOn(Schedulers.io())
                                .observeOn(Schedulers.io())
                                .map { it.right() }
                                .toFlowable()
                    })
                }.doOnSubscribe { notifyChildrenFindingStateChangeInteractor(BackgroundState.ONGOING) }
                .doOnNext { notifyChildrenFindingStateChangeInteractor(BackgroundState.SUCCESS) }
                .doOnError { notifyChildrenFindingStateChangeInteractor(BackgroundState.FAILURE) }
    }

    override fun findLastSearchResults(): Flowable<List<Tuple2<DomainSimpleRetrievedChild, Int>>> {
        return childrenLocalRepository.get()
                .findAll()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
    }

    override fun test(): ChildrenRepository.Tester {
        return tester
    }

    class SherlockTester(
            private val childrenRemoteRepository: Lazy<ChildrenRemoteRepository>,
            private val childrenLocalRepository: Lazy<ChildrenLocalRepository>
    ) : ChildrenRepository.Tester {
        override fun clear(): Single<Either<Throwable, Unit>> {
            return childrenRemoteRepository.get()
                    .clear()
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.io())
                    .flatMap { either ->
                        either.fold(ifLeft = {
                            Single.just(it.left())
                        }, ifRight = {
                            childrenLocalRepository.get()
                                    .clear()
                                    .andThen(Single.just(Unit.right()))
                        })
                    }
        }
    }
}
