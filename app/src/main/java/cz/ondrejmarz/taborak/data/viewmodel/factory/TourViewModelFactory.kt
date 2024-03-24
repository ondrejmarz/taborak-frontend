package cz.ondrejmarz.taborak.data.viewmodel.factory

import cz.ondrejmarz.taborak.data.viewmodel.TourViewModel

object TourViewModelFactory {
    private val viewModel: TourViewModel by lazy { TourViewModel() }

    fun getTourViewModel(): TourViewModel {
        return viewModel
    }
}