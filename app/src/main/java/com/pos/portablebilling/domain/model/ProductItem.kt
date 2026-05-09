package com.pos.portablebilling.domain.model

data class ProductItem(
    val id: Long = 0,
    val name: String,
    val unit: String,
    val price: Double
)
