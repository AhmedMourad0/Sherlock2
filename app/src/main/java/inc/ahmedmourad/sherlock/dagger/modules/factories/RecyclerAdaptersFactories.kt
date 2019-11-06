package inc.ahmedmourad.sherlock.dagger.modules.factories

import androidx.recyclerview.widget.RecyclerView
import com.bluelinelabs.conductor.Controller
import dagger.Lazy
import inc.ahmedmourad.sherlock.adapters.ResultsRecyclerAdapter
import inc.ahmedmourad.sherlock.adapters.SectionsRecyclerAdapter
import inc.ahmedmourad.sherlock.domain.platform.DateManager
import inc.ahmedmourad.sherlock.formatter.Formatter
import inc.ahmedmourad.sherlock.model.AppSimpleRetrievedChild
import inc.ahmedmourad.sherlock.view.model.AppSection
import inc.ahmedmourad.sherlock.view.model.TaggedController

internal interface ResultsRecyclerAdapterAbstractFactory {
    fun <T : RecyclerView.Adapter<*>> create(onResultSelectedListener: (Pair<AppSimpleRetrievedChild, Int>) -> Unit): T
}

internal class ResultsRecyclerAdapterFactory(
        private val dateManager: Lazy<DateManager>,
        private val formatter: Lazy<Formatter>
) : ResultsRecyclerAdapterAbstractFactory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : RecyclerView.Adapter<*>> create(
            onResultSelectedListener: (Pair<AppSimpleRetrievedChild, Int>) -> Unit
    ): T = ResultsRecyclerAdapter(dateManager, formatter, onResultSelectedListener) as T
}

internal interface SectionsRecyclerAdapterAbstractFactory {
    fun <T : RecyclerView.Adapter<*>> create(
            sectionsList: List<AppSection>,
            onSectionSelectedListener: (Lazy<out TaggedController<Controller>>?) -> Unit
    ): T
}

internal class SectionsRecyclerAdapterFactory : SectionsRecyclerAdapterAbstractFactory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : RecyclerView.Adapter<*>> create(
            sectionsList: List<AppSection>,
            onSectionSelectedListener: (Lazy<out TaggedController<Controller>>?) -> Unit
    ) = SectionsRecyclerAdapter(sectionsList, onSectionSelectedListener) as T
}
