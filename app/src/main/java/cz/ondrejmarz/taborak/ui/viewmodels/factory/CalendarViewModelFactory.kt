package cz.ondrejmarz.taborak.ui.viewmodels.factory

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import cz.ondrejmarz.taborak.ui.viewmodels.CalendarViewModel

@RequiresApi(Build.VERSION_CODES.O)
class CalendarViewModelFactory(private val id: String) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CalendarViewModel(id) as T
    }
}