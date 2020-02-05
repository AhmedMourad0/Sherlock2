package inc.ahmedmourad.sherlock.dagger.modules.factories

import androidx.recyclerview.widget.RecyclerView
import arrow.core.Tuple2
import dagger.Lazy
import inc.ahmedmourad.sherlock.adapters.AppSectionsRecyclerAdapter
import inc.ahmedmourad.sherlock.adapters.ChildrenRecyclerAdapter
import inc.ahmedmourad.sherlock.adapters.DynamicRecyclerAdapter
import inc.ahmedmourad.sherlock.domain.model.children.SimpleRetrievedChild
import inc.ahmedmourad.sherlock.domain.platform.DateManager
import inc.ahmedmourad.sherlock.model.core.AppSection
import inc.ahmedmourad.sherlock.model.core.TaggedController
import inc.ahmedmourad.sherlock.utils.formatter.Formatter

private typealias OnChildClickListener = (Tuple2<SimpleRetrievedChild, Int>) -> Unit

internal typealias ChildrenRecyclerAdapterFactory =
        (@JvmSuppressWildcards OnChildClickListener) ->
        @JvmSuppressWildcards DynamicRecyclerAdapter<List<Tuple2<SimpleRetrievedChild, Int>>, *>

internal fun childrenRecyclerAdapterFactory(
        dateManager: Lazy<DateManager>,
        formatter: Lazy<Formatter>,
        onResultSelectedListener: (Tuple2<SimpleRetrievedChild, Int>) -> Unit
): DynamicRecyclerAdapter<List<Tuple2<SimpleRetrievedChild, Int>>, *> {
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
