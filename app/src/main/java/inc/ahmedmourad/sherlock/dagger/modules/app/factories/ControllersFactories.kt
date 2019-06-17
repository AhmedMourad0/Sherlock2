package inc.ahmedmourad.sherlock.dagger.modules.app.factories

import com.bluelinelabs.conductor.Controller
import inc.ahmedmourad.sherlock.model.AppChild
import inc.ahmedmourad.sherlock.model.AppChildCriteriaRules
import inc.ahmedmourad.sherlock.view.controllers.DisplayChildController
import inc.ahmedmourad.sherlock.view.controllers.SearchResultsController

interface DisplayChildControllerAbstractFactory {
    fun create(child: Pair<AppChild, Int>): Controller
    fun create(child: AppChild): Controller
}

class DisplayChildControllerFactory : DisplayChildControllerAbstractFactory {
    override fun create(child: Pair<AppChild, Int>) = DisplayChildController.newInstance(child)
    override fun create(child: AppChild) = DisplayChildController.newInstance(child)
}

interface SearchResultsControllerAbstractFactory {
    fun create(rules: AppChildCriteriaRules): Controller
}

class SearchResultsControllerFactory : SearchResultsControllerAbstractFactory {
    override fun create(rules: AppChildCriteriaRules) = SearchResultsController.newInstance(rules)
}
