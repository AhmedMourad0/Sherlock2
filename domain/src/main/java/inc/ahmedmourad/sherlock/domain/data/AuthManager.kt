package inc.ahmedmourad.sherlock.domain.data

import arrow.core.Either
import inc.ahmedmourad.sherlock.domain.model.auth.CompletedUser
import inc.ahmedmourad.sherlock.domain.model.auth.IncompleteUser
import inc.ahmedmourad.sherlock.domain.model.auth.SignUpUser
import inc.ahmedmourad.sherlock.domain.model.auth.SignedInUser
import io.reactivex.Flowable
import io.reactivex.Single

interface AuthManager {

    fun observeUserAuthState(): Flowable<Boolean>

    fun findSignedInUser(): Flowable<Either<Throwable, Either<IncompleteUser, SignedInUser>>>

    fun signIn(email: String, password: String): Single<Either<Throwable, Either<IncompleteUser, SignedInUser>>>

    fun signUp(user: SignUpUser): Single<Either<Throwable, SignedInUser>>

    fun completeSignUp(completedUser: CompletedUser): Single<Either<Throwable, SignedInUser>>

    fun signInWithGoogle(): Single<Either<Throwable, Either<IncompleteUser, SignedInUser>>>

    fun signInWithFacebook(): Single<Either<Throwable, Either<IncompleteUser, SignedInUser>>>

    fun signInWithTwitter(): Single<Either<Throwable, Either<IncompleteUser, SignedInUser>>>

    fun sendPasswordResetEmail(email: String): Single<Either<Throwable, Unit>>

    fun signOut(): Single<Either<Throwable, Unit>>
}
