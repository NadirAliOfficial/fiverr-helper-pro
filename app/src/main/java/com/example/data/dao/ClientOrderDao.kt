package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.ClientOrder
import kotlinx.coroutines.flow.Flow

@Dao
interface ClientOrderDao {
    @Query("SELECT * FROM orders ORDER BY dueDateEpochMs ASC")
    fun getAllOrders(): Flow<List<ClientOrder>>

    @Query("SELECT * FROM orders WHERE status = :status ORDER BY dueDateEpochMs ASC")
    fun getOrdersByStatus(status: String): Flow<List<ClientOrder>>

    @Query("SELECT * FROM orders WHERE id = :id")
    suspend fun getOrderById(id: Long): ClientOrder?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: ClientOrder): Long

    @Update
    suspend fun updateOrder(order: ClientOrder)

    @Delete
    suspend fun deleteOrder(order: ClientOrder)

    @Query("DELETE FROM orders WHERE id = :id")
    suspend fun deleteOrderById(id: Long)
}
