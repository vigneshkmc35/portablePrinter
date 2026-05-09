package com.pos.portablebilling.domain.usecase

import com.pos.portablebilling.domain.model.ProductItem
import com.pos.portablebilling.domain.model.Transaction
import com.pos.portablebilling.domain.repository.BillingRepository
import kotlinx.coroutines.flow.Flow

class BillingUseCases(private val repository: BillingRepository) {
    
    fun getItems(): Flow<List<ProductItem>> = repository.getAllItems()
    
    suspend fun saveItem(item: ProductItem) {
        repository.addItem(item)
    }

    suspend fun removeItem(item: ProductItem) {
        repository.deleteItem(item)
    }

    fun getTransactions(): Flow<List<Transaction>> = repository.getAllTransactions()

    suspend fun saveTransaction(transaction: Transaction) {
        repository.addTransaction(transaction)
    }
}
