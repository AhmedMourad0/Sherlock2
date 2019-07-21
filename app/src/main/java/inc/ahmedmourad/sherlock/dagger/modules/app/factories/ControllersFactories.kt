package inc.ahmedmourad.sherlock.dagger.modules.app.factories

import android.content.Intent
import dagger.Lazy
import inc.ahmedmourad.sherlock.model.AppChildCriteriaRules
import inc.ahmedmourad.sherlock.model.AppPictureChild
import inc.ahmedmourad.sherlock.view.controllers.AddChildController
import inc.ahmedmourad.sherlock.view.controllers.DisplayChildController
import inc.ahmedmourad.sherlock.view.controllers.SearchResultsController
import inc.ahmedmourad.sherlock.view.model.TaggedController

interface AddChildControllerAbstractFactory {
    fun create(): Lazy<TaggedController>
    fun createIntent(child: AppPictureChild): Intent
}

class AddChildControllerFactory : AddChildControllerAbstractFactory {
    override fun create() = Lazy { AddChildController.newInstance() }
    override fun createIntent(child: AppPictureChild) = AddChildController.createIntent(child)
}

interface DisplayChildControllerAbstractFactory {
    fun create(childId: String): TaggedController
    fun createIntent(childId: String): Intent
}

class DisplayChildControllerFactory : DisplayChildControllerAbstractFactory {
    override fun create(childId: String) = DisplayChildController.newInstance(childId)
    override fun createIntent(childId: String) = DisplayChildController.createIntent(childId)
}

interface SearchResultsControllerAbstractFactory {
    fun create(rules: AppChildCriteriaRules): TaggedController
}

class SearchResultsControllerFactory : SearchResultsControllerAbstractFactory {
    override fun create(rules: AppChildCriteriaRules) = SearchResultsController.newInstance(rules)
}
