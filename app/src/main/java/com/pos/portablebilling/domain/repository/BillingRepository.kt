package com.pos.portablebilling.domain.repository

import com.pos.portablebilling.domain.model.ProductItem
import com.pos.portablebilling.domain.model.Transaction
import kotlinx.coroutines.flow.Flow

interface BillingRepository {
    fun getAllItems(): Flow<List<ProductItem>>
    suspend fun addItem(item: ProductItem)
    suspend fun deleteItem(item: ProductItem)
    
    fun getAllTransactions(): Flow<List<Transaction>>
    suspend fun addTransaction(transaction: Transaction)
}
