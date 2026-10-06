package com.example

import android.app.Application
import com.example.data.local.AppDatabase
import com.example.data.repository.CineHubRepository
import com.example.data.seed.CineHubDatabaseSeeder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class CineHubApplication : Application() {
    val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    lateinit var database: AppDatabase
        private set
    lateinit var repository: CineHubRepository
        private set

    override fun onCreate() {
        super.onCreate()
        com.example.data.remote.ApiClient.initialize(this)
        database = AppDatabase.getDatabase(this)
        repository = CineHubRepository(this, database, applicationScope)

        // Seed initial rich movie and series catalog
        applicationScope.launch(Dispatchers.IO) {
            CineHubDatabaseSeeder.seedIfNeeded(database)
        }
    }
}
