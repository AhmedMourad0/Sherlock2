package inc.ahmedmourad.sherlock.dagger.modules.app.factories

import androidx.recyclerview.widget.RecyclerView
import com.bluelinelabs.conductor.Controller
import dagger.Lazy
import inc.ahmedmourad.sherlock.adapters.ResultsRecyclerAdapter
import inc.ahmedmourad.sherlock.adapters.SectionsRecyclerAdapter
import inc.ahmedmourad.sherlock.domain.device.DateManager
import inc.ahmedmourad.sherlock.model.AppSection
import inc.ahmedmourad.sherlock.model.AppUrlChild
import inc.ahmedmourad.sherlock.utils.Formatter

interface ResultsRecyclerAdapterAbstractFactory {
    fun <T : RecyclerView.Adapter<*>> create(
            dateManager: Lazy<DateManager>,
            formatter: Lazy<Formatter<String>>,
            onResultSelectedListener: (Pair<AppUrlChild, Int>) -> Unit
    ): T
}

class ResultsRecyclerAdapterFactory : ResultsRecyclerAdapterAbstractFactory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : RecyclerView.Adapter<*>> create(
            dateManager: Lazy<DateManager>,
            formatter: Lazy<Formatter<String>>,
            onResultSelectedListener: (Pair<AppUrlChild, Int>) -> Unit
    ): T = ResultsRecyclerAdapter(dateManager, formatter, onResultSelectedListener) as T
}

interface SectionsRecyclerAdapterAbstractFactory {
    fun <T : RecyclerView.Adapter<*>> create(
            sectionsList: List<AppSection>,
            onSectionSelectedListener: (Lazy<out Controller>?) -> Unit
    ): T
}

class SectionsRecyclerAdapterFactory : SectionsRecyclerAdapterAbstractFactory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : RecyclerView.Adapter<*>> create(
            sectionsList: List<AppSection>,
            onSectionSelectedListener: (Lazy<out Controller>?) -> Unit
    ) = SectionsRecyclerAdapter(sectionsList, onSectionSelectedListener) as T
}
