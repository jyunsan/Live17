package com.example.live17.module

import com.example.live17.repository.UserRepository
import com.example.live17.ui.UserListViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val myModule = module {
    viewModel { UserListViewModel(get()) }
}

val repoModule = module {
    single { UserRepository() }
}