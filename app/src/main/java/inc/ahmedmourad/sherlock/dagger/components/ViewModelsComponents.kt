package inc.ahmedmourad.sherlock.dagger.components

import dagger.Subcomponent
import inc.ahmedmourad.sherlock.viewmodel.AddChildViewModel
import inc.ahmedmourad.sherlock.viewmodel.SearchResultsViewModel

@Subcomponent
interface SearchResultsViewModelComponent {
    fun inject(viewModel: SearchResultsViewModel)
}

@Subcomponent
interface AddChildViewModelComponent {
    fun inject(viewModel: AddChildViewModel)
}
