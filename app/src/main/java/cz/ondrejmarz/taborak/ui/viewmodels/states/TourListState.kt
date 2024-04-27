package cz.ondrejmarz.taborak.ui.viewmodels.states

import cz.ondrejmarz.taborak.data.models.Tour

data class TourListState(
    val listedTours: List<Tour> = emptyList()
)
