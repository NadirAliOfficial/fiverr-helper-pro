package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.Gig
import kotlinx.coroutines.flow.Flow

@Dao
interface GigDao {
    @Query("SELECT * FROM gigs ORDER BY updatedAt DESC")
    fun getAllGigs(): Flow<List<Gig>>

    @Query("SELECT * FROM gigs WHERE id = :id")
    suspend fun getGigById(id: Long): Gig?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGig(gig: Gig): Long

    @Update
    suspend fun updateGig(gig: Gig)

    @Delete
    suspend fun deleteGig(gig: Gig)

    @Query("DELETE FROM gigs WHERE id = :id")
    suspend fun deleteGigById(id: Long)
}
