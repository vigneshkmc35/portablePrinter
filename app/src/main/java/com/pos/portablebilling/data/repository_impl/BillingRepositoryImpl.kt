package com.pos.portablebilling.data.repository_impl

import com.pos.portablebilling.data.local.dao.ItemDao
import com.pos.portablebilling.data.local.dao.TransactionDao
import com.pos.portablebilling.data.local.entity.toDomain
import com.pos.portablebilling.data.local.entity.toEntity
import com.pos.portablebilling.data.local.entity.TransactionEntity
import com.pos.portablebilling.domain.model.ProductItem
import com.pos.portablebilling.domain.model.Transaction
import com.pos.portablebilling.domain.model.TransactionItem
import com.pos.portablebilling.domain.repository.BillingRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject

class BillingRepositoryImpl(
    private val itemDao: ItemDao,
    private val transactionDao: TransactionDao
) : BillingRepository {

    override fun getAllItems(): Flow<List<ProductItem>> {
        return itemDao.getAllItems().map { entities -> 
            entities.map { it.toDomain() }
        }
    }

    override suspend fun addItem(item: ProductItem) {
        itemDao.insertItem(item.toEntity())
    }

    override suspend fun deleteItem(item: ProductItem) {
        itemDao.deleteItem(item.toEntity())
    }

    override fun getAllTransactions(): Flow<List<Transaction>> {
        return transactionDao.getAllTransactions().map { entities ->
            entities.map { entity ->
                // Simple JSON Parsing for MVP
                val itemsList = mutableListOf<TransactionItem>()
                try {
                    val array = JSONArray(entity.itemsJson)
                    for (i in 0 until array.length()) {
                        val obj = array.getJSONObject(i)
                        itemsList.add(
                            TransactionItem(
                                productId = obj.getLong("productId"),
                                productName = obj.getString("productName"),
                                quantity = obj.getDouble("quantity"),
                                pricePerUnit = obj.getDouble("pricePerUnit"),
                                unit = obj.optString("unit", "pcs"), // Fallback to pcs for old data
                                totalPrice = obj.getDouble("totalPrice")
                            )
                        )
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                Transaction(
                    id = entity.id,
                    timestamp = entity.timestamp,
                    totalAmount = entity.totalAmount,
                    items = itemsList
                )
            }
        }
    }

    override suspend fun addTransaction(transaction: Transaction) {
        val jsonArray = JSONArray()
        transaction.items.forEach { item ->
            val obj = JSONObject().apply {
                put("productId", item.productId)
                put("productName", item.productName)
                put("quantity", item.quantity)
                put("pricePerUnit", item.pricePerUnit)
                put("unit", item.unit)
                put("totalPrice", item.totalPrice)
            }
            jsonArray.put(obj)
        }

        val entity = TransactionEntity(
            timestamp = transaction.timestamp,
            totalAmount = transaction.totalAmount,
            itemsJson = jsonArray.toString()
        )
        transactionDao.insertTransaction(entity)
    }
}
