package com.pos.portablebilling.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val timestamp: Long,
    val totalAmount: Double,
    val itemsJson: String // Storing list of TransactionItem as JSON string for simplicity
)
