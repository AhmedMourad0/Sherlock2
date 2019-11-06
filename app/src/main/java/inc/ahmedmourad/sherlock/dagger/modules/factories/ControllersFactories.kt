package inc.ahmedmourad.sherlock.dagger.modules.factories

import android.content.Intent
import com.bluelinelabs.conductor.Controller
import dagger.Lazy
import inc.ahmedmourad.sherlock.model.AppChildCriteriaRules
import inc.ahmedmourad.sherlock.model.AppPublishedChild
import inc.ahmedmourad.sherlock.model.AppSimpleRetrievedChild
import inc.ahmedmourad.sherlock.view.controllers.AddChildController
import inc.ahmedmourad.sherlock.view.controllers.DisplayChildController
import inc.ahmedmourad.sherlock.view.controllers.SearchResultsController
import inc.ahmedmourad.sherlock.view.model.TaggedController

internal interface AddChildControllerAbstractFactory {
    fun create(): Lazy<TaggedController<Controller>>
    fun createIntent(child: AppPublishedChild): Intent
}

internal class AddChildControllerFactory(private val activityFactory: MainActivityAbstractFactory) : AddChildControllerAbstractFactory {
    override fun create() = Lazy<TaggedController<Controller>> { AddChildController.newInstance() }
    override fun createIntent(child: AppPublishedChild) = AddChildController.createIntent(activityFactory, child)
}

internal interface DisplayChildControllerAbstractFactory {
    fun create(child: AppSimpleRetrievedChild): TaggedController<Controller>
    fun createIntent(child: AppSimpleRetrievedChild): Intent
}

internal class DisplayChildControllerFactory(private val activityFactory: MainActivityAbstractFactory) : DisplayChildControllerAbstractFactory {
    override fun create(child: AppSimpleRetrievedChild) = DisplayChildController.newInstance(child)
    override fun createIntent(child: AppSimpleRetrievedChild) = DisplayChildController.createIntent(activityFactory, child)
}

internal interface SearchResultsControllerAbstractFactory {
    fun create(rules: AppChildCriteriaRules): TaggedController<Controller>
}

internal class SearchResultsControllerFactory : SearchResultsControllerAbstractFactory {
    override fun create(rules: AppChildCriteriaRules) = SearchResultsController.newInstance(rules)
}
