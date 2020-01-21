package inc.ahmedmourad.sherlock.dagger.modules.factories

import android.content.Intent
import inc.ahmedmourad.sherlock.model.auth.AppIncompleteUser
import inc.ahmedmourad.sherlock.model.children.AppChildCriteriaRules
import inc.ahmedmourad.sherlock.model.children.AppPublishedChild
import inc.ahmedmourad.sherlock.model.children.AppSimpleRetrievedChild
import inc.ahmedmourad.sherlock.view.controllers.auth.CompleteSignUpController
import inc.ahmedmourad.sherlock.view.controllers.children.AddChildController
import inc.ahmedmourad.sherlock.view.controllers.children.ChildDetailsController
import inc.ahmedmourad.sherlock.view.controllers.children.ChildrenSearchResultsController
import inc.ahmedmourad.sherlock.view.model.TaggedController

internal typealias AddChildControllerIntentFactory =
        (@JvmSuppressWildcards AppPublishedChild) -> @JvmSuppressWildcards Intent

internal fun addChildControllerIntentFactory(activityFactory: MainActivityIntentFactory, child: AppPublishedChild): Intent {
    return AddChildController.createIntent(activityFactory, child)
}

internal typealias ChildDetailsControllerFactory =
        (@JvmSuppressWildcards AppSimpleRetrievedChild) -> @JvmSuppressWildcards TaggedController

internal fun childDetailsControllerFactory(child: AppSimpleRetrievedChild): TaggedController {
    return ChildDetailsController.newInstance(child)
}

internal typealias ChildDetailsControllerIntentFactory =
        (@JvmSuppressWildcards AppSimpleRetrievedChild) -> @JvmSuppressWildcards Intent

internal fun childDetailsControllerIntentFactory(activityFactory: MainActivityIntentFactory, child: AppSimpleRetrievedChild): Intent {
    return ChildDetailsController.createIntent(activityFactory, child)
}

internal typealias ChildrenSearchResultsControllerFactory =
        (@JvmSuppressWildcards AppChildCriteriaRules) -> @JvmSuppressWildcards TaggedController

internal fun childrenSearchResultsControllerFactory(rules: AppChildCriteriaRules): TaggedController {
    return ChildrenSearchResultsController.newInstance(rules)
}

internal typealias CompleteSignUpControllerFactory =
        (@JvmSuppressWildcards AppIncompleteUser) -> @JvmSuppressWildcards TaggedController

internal fun completeSignUpControllerFactory(incompleteUser: AppIncompleteUser): TaggedController {
    return CompleteSignUpController.newInstance(incompleteUser)
}

