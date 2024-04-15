package cz.ondrejmarz.taborak.data.viewmodel.states

import cz.ondrejmarz.taborak.data.models.Tour

data class TourListState(
    val listedTours: List<Tour> = emptyList()
)
