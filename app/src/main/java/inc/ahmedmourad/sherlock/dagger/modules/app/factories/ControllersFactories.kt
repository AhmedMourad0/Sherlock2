package inc.ahmedmourad.sherlock.dagger.modules.app.factories

import inc.ahmedmourad.sherlock.model.AppChild
import inc.ahmedmourad.sherlock.model.AppChildCriteriaRules
import inc.ahmedmourad.sherlock.view.controllers.DisplayChildController
import inc.ahmedmourad.sherlock.view.controllers.SearchResultsController

//TODO: all factories should be abstract
class DisplayChildControllerFactory {
    fun create(child: Pair<AppChild, Int>) = DisplayChildController.newInstance(child)
    fun create(child: AppChild) = DisplayChildController.newInstance(child)
}

class SearchResultsControllerFactory {
    fun create(rules: AppChildCriteriaRules) = SearchResultsController.newInstance(rules)
}
