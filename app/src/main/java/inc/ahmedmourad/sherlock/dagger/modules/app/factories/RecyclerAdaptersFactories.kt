package inc.ahmedmourad.sherlock.dagger.modules.app.factories

import com.bluelinelabs.conductor.Controller
import dagger.Lazy
import inc.ahmedmourad.sherlock.adapters.ResultsRecyclerAdapter
import inc.ahmedmourad.sherlock.adapters.SectionsRecyclerAdapter
import inc.ahmedmourad.sherlock.domain.device.DateManager
import inc.ahmedmourad.sherlock.model.AppSection
import inc.ahmedmourad.sherlock.model.AppUrlChild
import inc.ahmedmourad.sherlock.utils.Formatter

class ResultsRecyclerAdapterFactory {
    fun create(dateManager: Lazy<DateManager>, formatter: Lazy<Formatter>, onResultSelectedListener: (AppUrlChild) -> Unit) = ResultsRecyclerAdapter(dateManager, formatter, onResultSelectedListener)
}

class SectionsRecyclerAdapterFactory {
    fun create(sectionsList: List<AppSection>, onSectionSelectedListener: (Lazy<out Controller>?) -> Unit) = SectionsRecyclerAdapter(sectionsList, onSectionSelectedListener)
}
