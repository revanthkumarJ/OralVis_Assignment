package com.example.oralvis.di

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.room.Room
import com.example.oralvis.data.db.AppDatabase
import com.example.oralvis.data.rep.SessionRepository
import com.example.oralvis.ui.screens.camera.CameraViewModel
import com.example.oralvis.ui.screens.home.HomeViewModel
import com.example.oralvis.ui.screens.session_detail.SessionDetailViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {
    single {
        Room.databaseBuilder(
            get(),
            AppDatabase::class.java,
            "sessioncam.db"
        ).build()
    }

    single { get<AppDatabase>().sessionDao() }
    single { get<AppDatabase>().photoDao() }
    single { SessionRepository(get(), get()) }

    viewModelOf(::CameraViewModel)
    viewModelOf(::HomeViewModel)
    viewModelOf(::SessionDetailViewModel)
}
