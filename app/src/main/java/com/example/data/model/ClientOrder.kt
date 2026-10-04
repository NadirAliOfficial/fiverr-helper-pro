package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "orders")
data class ClientOrder(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val buyerUsername: String,
    val gigTitle: String,
    val orderAmount: Double,
    val tipAmount: Double = 0.0,
    val status: String = "In Progress", // "In Progress", "Revision Requested", "Delivered", "Completed", "Cancelled"
    val dueDateEpochMs: Long,
    val notes: String = "",
    val revisionCount: Int = 0,
    val createdAtEpochMs: Long = System.currentTimeMillis()
) {
    // 20% Fiverr service fee
    val netEarnings: Double
        get() = (orderAmount + tipAmount) * 0.80

    val fiverrFee: Double
        get() = (orderAmount + tipAmount) * 0.20
}
