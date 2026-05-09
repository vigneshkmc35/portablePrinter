package com.pos.portablebilling.domain.model

data class Transaction(
    val id: Long = 0,
    val timestamp: Long,
    val totalAmount: Double,
    val items: List<TransactionItem>
)

data class TransactionItem(
    val productId: Long,
    val productName: String,
    val quantity: Double,
    val pricePerUnit: Double,
    val unit: String,
    val totalPrice: Double
)
