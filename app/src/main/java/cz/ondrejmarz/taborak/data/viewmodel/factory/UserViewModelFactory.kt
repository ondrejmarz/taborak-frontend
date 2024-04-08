package cz.ondrejmarz.taborak.data.viewmodel.factory

import cz.ondrejmarz.taborak.data.viewmodel.UserViewModel

object UserViewModelFactory {
    private val viewModel: UserViewModel by lazy { UserViewModel() }

    fun getUserViewModel(): UserViewModel {
        return viewModel
    }
}
