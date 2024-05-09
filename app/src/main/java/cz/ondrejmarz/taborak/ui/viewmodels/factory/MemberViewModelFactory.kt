package cz.ondrejmarz.taborak.ui.viewmodels.factory

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import cz.ondrejmarz.taborak.ui.viewmodels.MemberViewModel

@RequiresApi(Build.VERSION_CODES.O)
class MemberViewModelFactory(private val tourId: String, private val userId: String) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MemberViewModel(tourId, userId) as T
    }
}