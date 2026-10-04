package com.example.data.repository

import com.example.data.dao.ClientOrderDao
import com.example.data.dao.GigDao
import com.example.data.dao.QuickReplyDao
import com.example.data.model.ClientOrder
import com.example.data.model.Gig
import com.example.data.model.QuickReply
import kotlinx.coroutines.flow.Flow

class FiverrRepository(
    private val gigDao: GigDao,
    private val orderDao: ClientOrderDao,
    private val quickReplyDao: QuickReplyDao
) {
    // Gigs
    val allGigs: Flow<List<Gig>> = gigDao.getAllGigs()
    suspend fun getGigById(id: Long): Gig? = gigDao.getGigById(id)
    suspend fun insertGig(gig: Gig): Long = gigDao.insertGig(gig)
    suspend fun updateGig(gig: Gig) = gigDao.updateGig(gig)
    suspend fun deleteGig(gig: Gig) = gigDao.deleteGig(gig)
    suspend fun deleteGigById(id: Long) = gigDao.deleteGigById(id)

    // Orders
    val allOrders: Flow<List<ClientOrder>> = orderDao.getAllOrders()
    fun getOrdersByStatus(status: String): Flow<List<ClientOrder>> = orderDao.getOrdersByStatus(status)
    suspend fun getOrderById(id: Long): ClientOrder? = orderDao.getOrderById(id)
    suspend fun insertOrder(order: ClientOrder): Long = orderDao.insertOrder(order)
    suspend fun updateOrder(order: ClientOrder) = orderDao.updateOrder(order)
    suspend fun deleteOrder(order: ClientOrder) = orderDao.deleteOrder(order)
    suspend fun deleteOrderById(id: Long) = orderDao.deleteOrderById(id)

    // Quick Replies
    val allQuickReplies: Flow<List<QuickReply>> = quickReplyDao.getAllQuickReplies()
    fun getQuickRepliesByCategory(category: String): Flow<List<QuickReply>> =
        quickReplyDao.getQuickRepliesByCategory(category)
    suspend fun insertQuickReply(quickReply: QuickReply): Long =
        quickReplyDao.insertQuickReply(quickReply)
    suspend fun updateQuickReply(quickReply: QuickReply) =
        quickReplyDao.updateQuickReply(quickReply)
    suspend fun deleteQuickReply(quickReply: QuickReply) =
        quickReplyDao.deleteQuickReply(quickReply)
    suspend fun deleteQuickReplyById(id: Long) =
        quickReplyDao.deleteQuickReplyById(id)
}
