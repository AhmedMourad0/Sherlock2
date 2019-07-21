package inc.ahmedmourad.sherlock.view.controllers

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.Unbinder
import com.bluelinelabs.conductor.RouterTransaction
import com.bluelinelabs.conductor.archlifecycle.LifecycleController
import dagger.Lazy
import inc.ahmedmourad.sherlock.R
import inc.ahmedmourad.sherlock.adapters.ResultsRecyclerAdapter
import inc.ahmedmourad.sherlock.dagger.SherlockComponent
import inc.ahmedmourad.sherlock.dagger.modules.app.factories.DisplayChildControllerAbstractFactory
import inc.ahmedmourad.sherlock.dagger.modules.app.factories.ResultsRecyclerAdapterAbstractFactory
import inc.ahmedmourad.sherlock.dagger.modules.app.factories.SearchResultsViewModelFactoryAbstractFactory
import inc.ahmedmourad.sherlock.domain.framework.DateManager
import inc.ahmedmourad.sherlock.model.AppChildCriteriaRules
import inc.ahmedmourad.sherlock.utils.Formatter
import inc.ahmedmourad.sherlock.utils.setSupportActionBar
import inc.ahmedmourad.sherlock.utils.viewModelProvider
import inc.ahmedmourad.sherlock.view.model.TaggedController
import inc.ahmedmourad.sherlock.viewmodel.SearchResultsViewModel
import javax.inject.Inject

//TODO: needs a better name, maybe?
class SearchResultsController(args: Bundle) : LifecycleController(args) {

    @BindView(R.id.toolbar)
    internal lateinit var toolbar: Toolbar

    @BindView(R.id.search_results_recycler)
    internal lateinit var recyclerView: RecyclerView

    @Inject
    lateinit var dateManager: Lazy<DateManager>

    @Inject
    lateinit var formatter: Lazy<Formatter<String>>

    @Inject
    lateinit var adapterFactory: ResultsRecyclerAdapterAbstractFactory

    @Inject
    lateinit var displayChildControllerFactory: Lazy<DisplayChildControllerAbstractFactory>

    @Inject
    lateinit var viewModelFactoryFactory: SearchResultsViewModelFactoryAbstractFactory

    private lateinit var context: Context

    private lateinit var rules: AppChildCriteriaRules

    private lateinit var adapter: ResultsRecyclerAdapter

    private var viewModel: SearchResultsViewModel? = null

    private lateinit var unbinder: Unbinder

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {

        SherlockComponent.Controllers.searchResultsComponent.get().inject(this)

        val view = inflater.inflate(R.layout.controller_search_results, container, false)

        unbinder = ButterKnife.bind(this, view)

        context = view.context

        setSupportActionBar(toolbar)

        toolbar.setTitle(R.string.search_results)

        rules = requireNotNull(args.getParcelable(ARG_RULES)) {
            "Rules cannot be null!"
        }

        initializeRecyclerView()

        viewModel = viewModelProvider(viewModelFactoryFactory.create(rules))[SearchResultsViewModel::class.java]

        viewModel?.searchResults?.observe(this, Observer {
            adapter.updateList(it)
        })

        return view
    }

    private fun initializeRecyclerView() {

        adapter = adapterFactory.create(dateManager, formatter) {

            val taggedController = displayChildControllerFactory.get().create(it.first.id)

            router.pushController(RouterTransaction.with(taggedController.controller).tag(taggedController.tag))
        }

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        recyclerView.isVerticalScrollBarEnabled = true
    }

    override fun onDestroy() {
        SherlockComponent.Controllers.searchResultsComponent.release()
        unbinder.unbind()
        super.onDestroy()
    }

    companion object {

        private const val CONTROLLER_TAG = "inc.ahmedmourad.sherlock.view.controllers.tag.SearchResultsController"

        private const val ARG_RULES = "inc.ahmedmourad.sherlock.view.controllers.arg.RULES"

        fun newInstance(rules: AppChildCriteriaRules) = TaggedController(SearchResultsController(Bundle(1).apply {
            putParcelable(ARG_RULES, rules)
        }), CONTROLLER_TAG)
    }
}
