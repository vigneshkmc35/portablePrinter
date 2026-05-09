package com.pos.portablebilling.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.pos.portablebilling.domain.model.ProductItem

@Entity(tableName = "items")
data class ItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val unit: String,
    val price: Double
)

fun ItemEntity.toDomain() = ProductItem(id, name, unit, price)
fun ProductItem.toEntity() = ItemEntity(id, name, unit, price)
