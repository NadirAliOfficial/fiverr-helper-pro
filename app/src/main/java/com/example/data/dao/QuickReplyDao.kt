package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.QuickReply
import kotlinx.coroutines.flow.Flow

@Dao
interface QuickReplyDao {
    @Query("SELECT * FROM quick_replies ORDER BY id ASC")
    fun getAllQuickReplies(): Flow<List<QuickReply>>

    @Query("SELECT * FROM quick_replies WHERE category = :category ORDER BY id ASC")
    fun getQuickRepliesByCategory(category: String): Flow<List<QuickReply>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuickReply(quickReply: QuickReply): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(quickReplies: List<QuickReply>)

    @Update
    suspend fun updateQuickReply(quickReply: QuickReply)

    @Delete
    suspend fun deleteQuickReply(quickReply: QuickReply)

    @Query("DELETE FROM quick_replies WHERE id = :id")
    suspend fun deleteQuickReplyById(id: Long)
}
