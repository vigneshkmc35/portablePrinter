package com.pos.portablebilling

import android.app.Application
import com.pos.portablebilling.data.local.AppDatabase
import com.pos.portablebilling.data.repository_impl.BillingRepositoryImpl
import com.pos.portablebilling.domain.repository.BillingRepository
import com.pos.portablebilling.domain.usecase.BillingUseCases

class PortableBillingApplication : Application() {
    
    lateinit var database: AppDatabase
    lateinit var repository: BillingRepository
    lateinit var useCases: BillingUseCases

    override fun onCreate() {
        super.onCreate()
        
        database = AppDatabase.getDatabase(this)
        repository = BillingRepositoryImpl(database.itemDao(), database.transactionDao())
        useCases = BillingUseCases(repository)
    }
}
