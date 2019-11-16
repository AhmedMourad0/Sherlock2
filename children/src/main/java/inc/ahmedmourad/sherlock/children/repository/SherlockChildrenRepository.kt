package inc.ahmedmourad.sherlock.children.repository

import arrow.core.*
import dagger.Lazy
import inc.ahmedmourad.sherlock.children.repository.dependencies.ChildrenLocalRepository
import inc.ahmedmourad.sherlock.children.repository.dependencies.ChildrenRemoteRepository
import inc.ahmedmourad.sherlock.domain.bus.Bus
import inc.ahmedmourad.sherlock.domain.data.ChildrenRepository
import inc.ahmedmourad.sherlock.domain.filter.Filter
import inc.ahmedmourad.sherlock.domain.filter.criteria.DomainChildCriteriaRules
import inc.ahmedmourad.sherlock.domain.model.DomainPublishedChild
import inc.ahmedmourad.sherlock.domain.model.DomainRetrievedChild
import inc.ahmedmourad.sherlock.domain.model.DomainSimpleRetrievedChild
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

//TODO: if requireUserSignedIn doesn't fail and user is not signed in with FirebaseAuth
// implement a fallback mechanism and sign the user in anonymously
internal class SherlockChildrenRepository(
        private val childrenLocalRepository: Lazy<ChildrenLocalRepository>,
        private val childrenRemoteRepository: Lazy<ChildrenRemoteRepository>,
        private val bus: Lazy<Bus>
) : ChildrenRepository {

    private val tester by lazy { SherlockTester(childrenRemoteRepository, childrenLocalRepository) }

    override fun publish(child: DomainPublishedChild): Single<Either<Throwable, DomainRetrievedChild>> {
        return childrenRemoteRepository.get()
                .publish(child)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .doOnSuccess { childEither ->
                    childEither.fold(ifLeft = {
                        bus.get().childPublishingState.accept(Bus.PublishingState.Failure(child))
                    }, ifRight = {
                        bus.get().childPublishingState.accept(Bus.PublishingState.Success(it))
                    })
                }.doOnSubscribe { bus.get().childPublishingState.accept(Bus.PublishingState.Ongoing(child)) }
                .doOnError { bus.get().childPublishingState.accept(Bus.PublishingState.Failure(child)) }
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
                }.doOnSubscribe { bus.get().childFindingState.accept(Bus.BackgroundState.ONGOING) }
                .doOnNext { bus.get().childFindingState.accept(Bus.BackgroundState.SUCCESS) }
                .doOnError { bus.get().childFindingState.accept(Bus.BackgroundState.FAILURE) }
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
                }.doOnSubscribe { bus.get().childrenFindingState.accept(Bus.BackgroundState.ONGOING) }
                .doOnNext { bus.get().childrenFindingState.accept(Bus.BackgroundState.SUCCESS) }
                .doOnError { bus.get().childrenFindingState.accept(Bus.BackgroundState.FAILURE) }
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
