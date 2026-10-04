package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "quick_replies")
data class QuickReply(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val category: String, // "First Contact", "Offer Sent", "Requirements", "Delivery", "Revision", "Safety / TOS", "General"
    val messageText: String,
    val isPreset: Boolean = false
)
