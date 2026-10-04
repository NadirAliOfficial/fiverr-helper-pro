package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "gigs")
data class Gig(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val category: String,
    val tags: String, // comma separated tags (max 5)
    val basicTitle: String = "Basic Package",
    val basicPrice: Double = 10.0,
    val basicDeliveryDays: Int = 2,
    val standardTitle: String = "Standard Package",
    val standardPrice: Double = 35.0,
    val standardDeliveryDays: Int = 3,
    val premiumTitle: String = "Premium Package",
    val premiumPrice: Double = 80.0,
    val premiumDeliveryDays: Int = 5,
    val description: String = "",
    val faq: String = "",
    val impressions: Int = 0,
    val clicks: Int = 0,
    val ordersCount: Int = 0,
    val status: String = "Active", // "Active", "Paused", "Draft"
    val updatedAt: Long = System.currentTimeMillis()
)
