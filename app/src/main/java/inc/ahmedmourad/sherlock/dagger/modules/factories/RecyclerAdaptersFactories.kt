package inc.ahmedmourad.sherlock.dagger.modules.factories

import androidx.recyclerview.widget.RecyclerView
import dagger.Lazy
import inc.ahmedmourad.sherlock.adapters.DynamicRecyclerAdapter
import inc.ahmedmourad.sherlock.adapters.ResultsRecyclerAdapter
import inc.ahmedmourad.sherlock.adapters.SectionsRecyclerAdapter
import inc.ahmedmourad.sherlock.domain.platform.DateManager
import inc.ahmedmourad.sherlock.formatter.Formatter
import inc.ahmedmourad.sherlock.model.AppSimpleRetrievedChild
import inc.ahmedmourad.sherlock.view.model.AppSection
import inc.ahmedmourad.sherlock.view.model.TaggedController

private typealias OnResultClickListener = (Pair<AppSimpleRetrievedChild, Int>) -> Unit
internal typealias ResultsRecyclerAdapterFactory =
        (@JvmSuppressWildcards OnResultClickListener) -> @JvmSuppressWildcards DynamicRecyclerAdapter<List<Pair<AppSimpleRetrievedChild, Int>>, *>

internal fun resultsRecyclerAdapterFactory(
        dateManager: Lazy<DateManager>,
        formatter: Lazy<Formatter>,
        onResultSelectedListener: (Pair<AppSimpleRetrievedChild, Int>) -> Unit
): DynamicRecyclerAdapter<List<Pair<AppSimpleRetrievedChild, Int>>, *> {
    return ResultsRecyclerAdapter(dateManager, formatter, onResultSelectedListener)
}

private typealias OnSectionClickListener = (Lazy<out TaggedController>?) -> Unit
internal typealias SectionsRecyclerAdapterFactory =
        (@JvmSuppressWildcards List<AppSection>, @JvmSuppressWildcards OnSectionClickListener) -> @JvmSuppressWildcards RecyclerView.Adapter<*>

internal fun sectionsRecyclerAdapterFactory(
        sectionsList: List<AppSection>,
        onSectionSelectedListener: (Lazy<out TaggedController>?) -> Unit
): RecyclerView.Adapter<*> {
    return SectionsRecyclerAdapter(sectionsList, onSectionSelectedListener)
}
