package inc.ahmedmourad.sherlock.dagger.modules.factories

import android.content.Intent
import dagger.Lazy
import inc.ahmedmourad.sherlock.model.AppChildCriteriaRules
import inc.ahmedmourad.sherlock.model.AppPublishedChild
import inc.ahmedmourad.sherlock.model.AppSimpleRetrievedChild
import inc.ahmedmourad.sherlock.view.controllers.AddChildController
import inc.ahmedmourad.sherlock.view.controllers.DisplayChildController
import inc.ahmedmourad.sherlock.view.controllers.SearchResultsController
import inc.ahmedmourad.sherlock.view.model.TaggedController

internal typealias AddChildControllerFactory = () -> @JvmSuppressWildcards Lazy<TaggedController>

internal fun addChildControllerFactory(): Lazy<TaggedController> {
    return Lazy { AddChildController.newInstance() }
}

internal typealias AddChildControllerIntentFactory =
        (@JvmSuppressWildcards AppPublishedChild) -> @JvmSuppressWildcards Intent

internal fun addChildControllerIntentFactory(activityFactory: MainActivityIntentFactory, child: AppPublishedChild): Intent {
    return AddChildController.createIntent(activityFactory, child)
}

internal typealias DisplayChildControllerFactory =
        (@JvmSuppressWildcards AppSimpleRetrievedChild) -> @JvmSuppressWildcards TaggedController

internal fun displayChildControllerFactory(child: AppSimpleRetrievedChild): TaggedController {
    return DisplayChildController.newInstance(child)
}

internal typealias DisplayChildControllerIntentFactory =
        (@JvmSuppressWildcards AppSimpleRetrievedChild) -> @JvmSuppressWildcards Intent

internal fun displayChildControllerIntentFactory(activityFactory: MainActivityIntentFactory, child: AppSimpleRetrievedChild): Intent {
    return DisplayChildController.createIntent(activityFactory, child)
}

internal typealias SearchResultsControllerFactory =
        (@JvmSuppressWildcards AppChildCriteriaRules) -> @JvmSuppressWildcards TaggedController

internal fun searchResultsControllerFactory(rules: AppChildCriteriaRules): TaggedController {
    return SearchResultsController.newInstance(rules)
}
