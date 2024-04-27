package cz.ondrejmarz.taborak.ui.viewmodels.factory

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import cz.ondrejmarz.taborak.ui.viewmodels.ParticipantsViewModel

@RequiresApi(Build.VERSION_CODES.O)
class ParticipantsViewModelFactory(private val id: String) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ParticipantsViewModel(id) as T
    }
}