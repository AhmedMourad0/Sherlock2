package inc.ahmedmourad.sherlock.dagger.modules.app.qualifiers

import javax.inject.Qualifier
import kotlin.annotation.AnnotationRetention.BINARY
import kotlin.annotation.AnnotationTarget.*

@Qualifier
@Retention(BINARY)
@Target(FIELD, VALUE_PARAMETER, FUNCTION, PROPERTY_GETTER, PROPERTY_SETTER)
annotation class AddChildViewModelQualifier

@Qualifier
@Retention(BINARY)
@Target(FIELD, VALUE_PARAMETER, FUNCTION, PROPERTY_GETTER, PROPERTY_SETTER)
annotation class FindChildrenViewModelQualifier

@Qualifier
@Retention(BINARY)
@Target(FIELD, VALUE_PARAMETER, FUNCTION, PROPERTY_GETTER, PROPERTY_SETTER)
annotation class AddChildControllerQualifier

@Qualifier
@Retention(BINARY)
@Target(FIELD, VALUE_PARAMETER, FUNCTION, PROPERTY_GETTER, PROPERTY_SETTER)
annotation class FindChildrenControllerQualifier

@Qualifier
@Retention(BINARY)
@Target(FIELD, VALUE_PARAMETER, FUNCTION, PROPERTY_GETTER, PROPERTY_SETTER)
annotation class HomeControllerQualifier
