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
import inc.ahmedmourad.sherlock.dagger.modules.app.factories.DisplayChildControllerFactory
import inc.ahmedmourad.sherlock.dagger.modules.app.factories.ResultsRecyclerAdapterFactory
import inc.ahmedmourad.sherlock.dagger.modules.app.factories.SearchResultsViewModelFactoryFactory
import inc.ahmedmourad.sherlock.domain.device.DateManager
import inc.ahmedmourad.sherlock.model.AppChildCriteriaRules
import inc.ahmedmourad.sherlock.utils.Formatter
import inc.ahmedmourad.sherlock.utils.setSupportActionBar
import inc.ahmedmourad.sherlock.utils.viewModelProvider
import inc.ahmedmourad.sherlock.viewmodel.SearchResultsViewModel
import org.parceler.Parcels
import javax.inject.Inject

//TODO: needs a better name, maybe?
class SearchResultsController(args: Bundle) : LifecycleController(args) {

    @BindView(R.id.toolbar)
    internal lateinit var toolbar: Toolbar

    @BindView(R.id.search_found_results_recycler)
    internal lateinit var recyclerView: RecyclerView

    @Inject
    lateinit var dateManager: Lazy<DateManager>

    @Inject
    lateinit var formatter: Lazy<Formatter>

    @Inject
    lateinit var adapterFactory: ResultsRecyclerAdapterFactory

    @Inject
    lateinit var displayChildControllerFactory: Lazy<DisplayChildControllerFactory>

    @Inject
    lateinit var viewModelFactoryFactory: SearchResultsViewModelFactoryFactory

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

        toolbar.setTitle(R.string.results)

        rules = Parcels.unwrap(
                args.getParcelable(ARG_RULES)
                        ?: throw IllegalArgumentException("Rules cannot be null!")
        )

        initializeRecyclerView()

        viewModel = viewModelProvider(viewModelFactoryFactory.create(rules))[SearchResultsViewModel::class.java]

        viewModel?.searchResults?.observe(this, Observer {
            adapter.updateList(it)
        })

        return view
    }

    private fun initializeRecyclerView() {
        adapter = adapterFactory.create(dateManager, formatter) {
            router.pushController(RouterTransaction.with(displayChildControllerFactory.get().create(it)))
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

        private const val ARG_RULES = "inc.ahmedmourad.sherlock.view.controllers.arg.RULES"

        fun newInstance(rules: AppChildCriteriaRules) = SearchResultsController(Bundle(1).apply {
            putParcelable(ARG_RULES, Parcels.wrap(rules))
        })
    }
}
