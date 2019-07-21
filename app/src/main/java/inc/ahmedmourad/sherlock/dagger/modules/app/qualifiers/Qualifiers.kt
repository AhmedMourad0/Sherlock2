package inc.ahmedmourad.sherlock.dagger.modules.app.qualifiers

import javax.inject.Qualifier
import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.*

@Qualifier
@Retention(RUNTIME)
@Target(FIELD, VALUE_PARAMETER, FUNCTION, PROPERTY_GETTER, PROPERTY_SETTER)
annotation class AddChildViewModelQualifier

@Qualifier
@Retention(RUNTIME)
@Target(FIELD, VALUE_PARAMETER, FUNCTION, PROPERTY_GETTER, PROPERTY_SETTER)
annotation class FindChildrenViewModelQualifier

@Qualifier
@Retention(RUNTIME)
@Target(FIELD, VALUE_PARAMETER, FUNCTION, PROPERTY_GETTER, PROPERTY_SETTER)
annotation class FindChildrenControllerQualifier

@Qualifier
@Retention(RUNTIME)
@Target(FIELD, VALUE_PARAMETER, FUNCTION, PROPERTY_GETTER, PROPERTY_SETTER)
annotation class HomeControllerQualifier
