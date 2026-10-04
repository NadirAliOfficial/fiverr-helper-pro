package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.dao.ClientOrderDao
import com.example.data.dao.GigDao
import com.example.data.dao.QuickReplyDao
import com.example.data.model.ClientOrder
import com.example.data.model.Gig
import com.example.data.model.QuickReply
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [Gig::class, ClientOrder::class, QuickReply::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun gigDao(): GigDao
    abstract fun orderDao(): ClientOrderDao
    abstract fun quickReplyDao(): QuickReplyDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope = CoroutineScope(Dispatchers.IO)): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "fiverr_helper_db"
                )
                    .addCallback(DatabaseCallback(scope))
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialData(database)
                    }
                }
            }
        }

        suspend fun populateInitialData(database: AppDatabase) {
            val quickReplyDao = database.quickReplyDao()
            val gigDao = database.gigDao()
            val orderDao = database.orderDao()

            val initialReplies = listOf(
                QuickReply(
                    title = "Warm First Greeting",
                    category = "First Contact",
                    messageText = "Hi {buyer_name}, thank you for reaching out! I'd love to help you with {gig_name}. To ensure we get the best result for your project, could you please share a few more details about your specific goals and timeline? Looking forward to working together!",
                    isPreset = true
                ),
                QuickReply(
                    title = "Custom Offer Confirmation",
                    category = "Offer Sent",
                    messageText = "Hello {buyer_name}, I have created a custom offer tailored to your requirements for {gig_name}. It includes full delivery in {delivery_days} days for \${price}. Please review the scope and accept when you are ready to kick off!",
                    isPreset = true
                ),
                QuickReply(
                    title = "Order Kick-off Acknowledgment",
                    category = "Requirements",
                    messageText = "Hi {buyer_name}, thank you for placing your order! I've received your requirements and I'm currently working on the project. I will keep you posted with progress. Have a wonderful day!",
                    isPreset = true
                ),
                QuickReply(
                    title = "Delivery & Feedback Note",
                    category = "Delivery",
                    messageText = "Hello {buyer_name}, your order for {gig_name} is complete and delivered! Please review the attached files. If you need any adjustments, feel free to let me know and I'll gladly assist. If you are satisfied with the delivery, your honest feedback and rating are greatly appreciated!",
                    isPreset = true
                ),
                QuickReply(
                    title = "Prompt Revision Handling",
                    category = "Revision",
                    messageText = "Hi {buyer_name}, thank you for the feedback! I understand the modifications you requested. I am working on it right away and will provide the revised delivery within a few hours. Thank you for your patience!",
                    isPreset = true
                ),
                QuickReply(
                    title = "Fiverr TOS Contact Boundary",
                    category = "Safety / TOS",
                    messageText = "Hi {buyer_name}, for both of our security and to strictly adhere to Fiverr Terms of Service, all communications, file transfers, and transactions must stay directly on the Fiverr platform. Thank you for understanding!",
                    isPreset = true
                ),
                QuickReply(
                    title = "Missing Requirements Follow-up",
                    category = "Requirements",
                    messageText = "Hi {buyer_name}, just following up regarding your order requirements. To proceed with the delivery on time, I still need the files/details we discussed. Whenever you're ready, please submit them. Thank you!",
                    isPreset = true
                )
            )
            quickReplyDao.insertAll(initialReplies)

            // Zero pre-seeded fake gigs or fake orders. Clean database for real user data!
        }
    }
}
