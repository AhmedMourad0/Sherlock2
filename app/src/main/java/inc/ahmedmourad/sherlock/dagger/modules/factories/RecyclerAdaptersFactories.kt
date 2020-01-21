package inc.ahmedmourad.sherlock.dagger.modules.factories

import androidx.recyclerview.widget.RecyclerView
import arrow.core.Tuple2
import dagger.Lazy
import inc.ahmedmourad.sherlock.adapters.AppSectionsRecyclerAdapter
import inc.ahmedmourad.sherlock.adapters.ChildrenRecyclerAdapter
import inc.ahmedmourad.sherlock.adapters.DynamicRecyclerAdapter
import inc.ahmedmourad.sherlock.domain.platform.DateManager
import inc.ahmedmourad.sherlock.model.children.AppSimpleRetrievedChild
import inc.ahmedmourad.sherlock.utils.formatter.Formatter
import inc.ahmedmourad.sherlock.view.model.AppSection
import inc.ahmedmourad.sherlock.view.model.TaggedController

private typealias OnChildClickListener = (Tuple2<AppSimpleRetrievedChild, Int>) -> Unit

internal typealias ChildrenRecyclerAdapterFactory =
        (@JvmSuppressWildcards OnChildClickListener) ->
        @JvmSuppressWildcards DynamicRecyclerAdapter<List<Tuple2<AppSimpleRetrievedChild, Int>>, *>

internal fun childrenRecyclerAdapterFactory(
        dateManager: Lazy<DateManager>,
        formatter: Lazy<Formatter>,
        onResultSelectedListener: (Tuple2<AppSimpleRetrievedChild, Int>) -> Unit
): DynamicRecyclerAdapter<List<Tuple2<AppSimpleRetrievedChild, Int>>, *> {
    return ChildrenRecyclerAdapter(dateManager, formatter, onResultSelectedListener)
}

private typealias OnSectionClickListener = (Lazy<out TaggedController>?) -> Unit

internal typealias AppSectionsRecyclerAdapterFactory =
        (@JvmSuppressWildcards List<AppSection>, @JvmSuppressWildcards OnSectionClickListener) ->
        @JvmSuppressWildcards RecyclerView.Adapter<*>

internal fun appSectionsRecyclerAdapterFactory(
        sectionsList: List<AppSection>,
        onSectionSelectedListener: (Lazy<out TaggedController>?) -> Unit
): RecyclerView.Adapter<*> {
    return AppSectionsRecyclerAdapter(sectionsList, onSectionSelectedListener)
}
