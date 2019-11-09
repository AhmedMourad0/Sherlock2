package inc.ahmedmourad.sherlock.view.controllers

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.Unbinder
import com.bluelinelabs.conductor.RouterTransaction
import com.bluelinelabs.conductor.archlifecycle.LifecycleController
import dagger.Lazy
import inc.ahmedmourad.sherlock.R
import inc.ahmedmourad.sherlock.adapters.DynamicRecyclerAdapter
import inc.ahmedmourad.sherlock.dagger.SherlockComponent
import inc.ahmedmourad.sherlock.dagger.modules.factories.DisplayChildControllerFactory
import inc.ahmedmourad.sherlock.dagger.modules.factories.ResultsRecyclerAdapterFactory
import inc.ahmedmourad.sherlock.dagger.modules.factories.SearchResultsViewModelFactoryFactory
import inc.ahmedmourad.sherlock.domain.model.Either
import inc.ahmedmourad.sherlock.domain.model.disposable
import inc.ahmedmourad.sherlock.domain.platform.DateManager
import inc.ahmedmourad.sherlock.model.AppChildCriteriaRules
import inc.ahmedmourad.sherlock.model.AppSimpleRetrievedChild
import inc.ahmedmourad.sherlock.utils.formatter.Formatter
import inc.ahmedmourad.sherlock.utils.setSupportActionBar
import inc.ahmedmourad.sherlock.utils.viewModelProvider
import inc.ahmedmourad.sherlock.view.model.TaggedController
import inc.ahmedmourad.sherlock.viewmodel.controllers.SearchResultsViewModel
import timber.log.Timber
import javax.inject.Inject

//TODO: needs a better name, maybe?
internal class SearchResultsController(args: Bundle) : LifecycleController(args) {

    @BindView(R.id.toolbar)
    internal lateinit var toolbar: Toolbar

    @BindView(R.id.search_results_recycler)
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

    //TODO: create an interface instead
    private lateinit var adapter: DynamicRecyclerAdapter<List<Pair<AppSimpleRetrievedChild, Int>>, *>

    private lateinit var viewModel: SearchResultsViewModel

    private var findAllResultsDisposable by disposable()

    private lateinit var unbinder: Unbinder

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {

        SherlockComponent.Controllers.searchResultsComponent.get().inject(this)

        val view = inflater.inflate(R.layout.controller_search_results, container, false)

        unbinder = ButterKnife.bind(this, view)

        context = view.context

        setSupportActionBar(toolbar)

        toolbar.setTitle(R.string.search_results)

        rules = requireNotNull(args.getParcelable(ARG_RULES))

        initializeRecyclerView()

        viewModel = viewModelProvider(viewModelFactoryFactory(rules))[SearchResultsViewModel::class.java]

        return view
    }

    override fun onAttach(view: View) {
        super.onAttach(view)

        //TODO: either give the option to update or not, or onPublish new values to the bottom
        //TODO: paginate
        findAllResultsDisposable = viewModel.searchResults.subscribe({

            when (it) {

                is Either.Value -> adapter.update(it.value)

                is Either.Error -> {
                    Timber.e(it.error)
                    Toast.makeText(context, it.error.localizedMessage, Toast.LENGTH_LONG).show()
                }
            }

        }, Timber::e)
    }

    override fun onDetach(view: View) {
        findAllResultsDisposable?.dispose()
        super.onDetach(view)
    }

    private fun initializeRecyclerView() {

        adapter = adapterFactory {
            val taggedController = displayChildControllerFactory.get()(it.first)
            router.pushController(RouterTransaction.with(taggedController.controller).tag(taggedController.tag))
        }

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        recyclerView.isVerticalScrollBarEnabled = true
    }

    override fun onDestroy() {
        SherlockComponent.Controllers.searchResultsComponent.release()
        findAllResultsDisposable?.dispose()
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
