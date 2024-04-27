package cz.ondrejmarz.taborak.ui.viewmodels.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import cz.ondrejmarz.taborak.ui.viewmodels.TourViewModel

class TourViewModelFactory(private val id: String) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return TourViewModel(id) as T
    }
}