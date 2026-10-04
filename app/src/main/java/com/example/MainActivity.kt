package com.example

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.WorkOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import android.widget.Toast
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.service.FiverrNotificationListener
import com.example.ui.FiverrViewModel
import com.example.ui.components.AutoSyncManagerDialog
import com.example.ui.components.FiverrSyncDialog
import com.example.ui.components.FiverrTopBar
import com.example.ui.screens.AiAndSafetyScreen
import com.example.ui.screens.FeeCalculatorScreen
import com.example.ui.screens.GigManagerScreen
import com.example.ui.screens.OrdersDashboardScreen
import com.example.ui.screens.QuickRepliesScreen
import com.example.ui.theme.FiverrDarkGreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

    private val viewModel: FiverrViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                FiverrHelperApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun FiverrHelperApp(viewModel: FiverrViewModel) {
    var showSyncDialog by remember { mutableStateOf(false) }
    var showAutoSyncDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        FiverrNotificationListener.autoCapturedOrders.collect { message ->
            Toast.makeText(context, "⚡ $message", Toast.LENGTH_LONG).show()
        }
    }

    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val orders by viewModel.orders.collectAsStateWithLifecycle()
    val gigs by viewModel.gigs.collectAsStateWithLifecycle()
    val quickReplies by viewModel.quickReplies.collectAsStateWithLifecycle()

    val isSyncing by viewModel.isSyncing.collectAsStateWithLifecycle()
    val syncStatusMessage by viewModel.syncStatusMessage.collectAsStateWithLifecycle()
    val connectedUsername by viewModel.connectedUsername.collectAsStateWithLifecycle()

    val orderStatusFilter by viewModel.orderStatusFilter.collectAsStateWithLifecycle()
    val selectedReplyCategory by viewModel.selectedReplyCategory.collectAsStateWithLifecycle()

    val calculatorMode by viewModel.calculatorMode.collectAsStateWithLifecycle()
    val grossAmount by viewModel.grossAmount.collectAsStateWithLifecycle()
    val tipAmount by viewModel.tipAmount.collectAsStateWithLifecycle()
    val reverseNet by viewModel.reverseNet.collectAsStateWithLifecycle()
    val selectedCurrency by viewModel.selectedCurrency.collectAsStateWithLifecycle()
    val targetGoal by viewModel.targetGoal.collectAsStateWithLifecycle()

    val seoTitle by viewModel.seoTitle.collectAsStateWithLifecycle()
    val seoTags by viewModel.seoTags.collectAsStateWithLifecycle()
    val seoDescription by viewModel.seoDescription.collectAsStateWithLifecycle()

    val safetyText by viewModel.safetyText.collectAsStateWithLifecycle()

    val aiType by viewModel.aiType.collectAsStateWithLifecycle()
    val aiSkill by viewModel.aiSkill.collectAsStateWithLifecycle()
    val aiDetails by viewModel.aiDetails.collectAsStateWithLifecycle()
    val aiTone by viewModel.aiTone.collectAsStateWithLifecycle()
    val isGenerating by viewModel.isGenerating.collectAsStateWithLifecycle()
    val aiResult by viewModel.aiResult.collectAsStateWithLifecycle()

    // Handle back button to return to Dashboard/Orders tab
    BackHandler(enabled = currentTab != 0) {
        viewModel.selectTab(0)
    }

    val activeOrders = orders.filter { it.status != "Completed" && it.status != "Cancelled" }
    val queueNetEarnings = activeOrders.sumOf { (it.orderAmount + it.tipAmount) * 0.80 }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            FiverrTopBar(
                activeOrderCount = activeOrders.size,
                queueNetEarnings = queueNetEarnings,
                onSyncClick = { showAutoSyncDialog = true },
                isSyncing = isSyncing,
                username = connectedUsername
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                modifier = Modifier.testTag("bottom_nav_bar")
            ) {
                NavigationBarItem(
                    selected = currentTab == 0,
                    onClick = { viewModel.selectTab(0) },
                    icon = { Icon(Icons.Default.WorkOutline, contentDescription = "Orders") },
                    label = { Text("Orders", fontSize = 11.sp, fontWeight = if (currentTab == 0) FontWeight.Bold else FontWeight.Normal) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = FiverrDarkGreen,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    modifier = Modifier.testTag("nav_item_orders")
                )

                NavigationBarItem(
                    selected = currentTab == 1,
                    onClick = { viewModel.selectTab(1) },
                    icon = { Icon(Icons.AutoMirrored.Filled.ListAlt, contentDescription = "Gigs & SEO") },
                    label = { Text("Gigs", fontSize = 11.sp, fontWeight = if (currentTab == 1) FontWeight.Bold else FontWeight.Normal) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = FiverrDarkGreen,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    modifier = Modifier.testTag("nav_item_gigs")
                )

                NavigationBarItem(
                    selected = currentTab == 2,
                    onClick = { viewModel.selectTab(2) },
                    icon = { Icon(Icons.Default.Calculate, contentDescription = "Fee Calculator") },
                    label = { Text("Fee & Profit", fontSize = 11.sp, fontWeight = if (currentTab == 2) FontWeight.Bold else FontWeight.Normal) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = FiverrDarkGreen,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    modifier = Modifier.testTag("nav_item_calculator")
                )

                NavigationBarItem(
                    selected = currentTab == 3,
                    onClick = { viewModel.selectTab(3) },
                    icon = { Icon(Icons.Default.ChatBubbleOutline, contentDescription = "Quick Replies") },
                    label = { Text("Replies", fontSize = 11.sp, fontWeight = if (currentTab == 3) FontWeight.Bold else FontWeight.Normal) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = FiverrDarkGreen,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    modifier = Modifier.testTag("nav_item_replies")
                )

                NavigationBarItem(
                    selected = currentTab == 4,
                    onClick = { viewModel.selectTab(4) },
                    icon = { Icon(Icons.Default.Security, contentDescription = "AI & Safety") },
                    label = { Text("AI & Safety", fontSize = 11.sp, fontWeight = if (currentTab == 4) FontWeight.Bold else FontWeight.Normal) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = FiverrDarkGreen,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    modifier = Modifier.testTag("nav_item_ai_safety")
                )
            }
        }
    ) { innerPadding ->
        val context = LocalContext.current
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                0 -> OrdersDashboardScreen(
                    orders = orders,
                    filterStatus = orderStatusFilter,
                    onFilterChange = { viewModel.setOrderStatusFilter(it) },
                    onSaveOrder = { viewModel.saveOrder(it) },
                    onUpdateStatus = { order, newStatus -> viewModel.updateOrderStatus(order, newStatus) },
                    onDeleteOrder = { viewModel.deleteOrder(it) },
                    onSyncClick = { showAutoSyncDialog = true },
                    onOpenExternalFiverr = { url ->
                        try {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            // Fallback
                        }
                    }
                )

                1 -> GigManagerScreen(
                    gigs = gigs,
                    onSaveGig = { viewModel.saveGig(it) },
                    onDeleteGig = { viewModel.deleteGig(it) },
                    seoTitle = seoTitle,
                    seoTags = seoTags,
                    seoDescription = seoDescription,
                    onUpdateSeoInputs = { t, tg, d -> viewModel.updateSeoInputs(t, tg, d) },
                    seoReport = viewModel.getSeoReport(),
                    onSyncClick = { showSyncDialog = true }
                )

                2 -> FeeCalculatorScreen(
                    calculatorMode = calculatorMode,
                    onModeChange = { viewModel.setCalculatorMode(it) },
                    grossAmount = grossAmount,
                    tipAmount = tipAmount,
                    onGrossChange = { viewModel.updateGross(it) },
                    onAdjustGross = { viewModel.adjustGross(it) },
                    onTipChange = { viewModel.updateTip(it) },
                    reverseNet = reverseNet,
                    onReverseNetChange = { viewModel.updateReverseNet(it) },
                    reverseQuoteResult = viewModel.getReverseQuote(),
                    feeResult = viewModel.getFeeCalculation(),
                    selectedCurrency = selectedCurrency,
                    onSelectCurrency = { viewModel.selectCurrency(it) },
                    withdrawalEstimates = viewModel.getWithdrawalEstimates(),
                    targetGoal = targetGoal,
                    onTargetGoalChange = { viewModel.updateTargetGoal(it) },
                    goalBreakdown = viewModel.getGoalBreakdown()
                )

                3 -> QuickRepliesScreen(
                    quickReplies = quickReplies,
                    selectedCategory = selectedReplyCategory,
                    onCategoryChange = { viewModel.selectReplyCategory(it) },
                    onSaveQuickReply = { viewModel.saveQuickReply(it) },
                    onDeleteQuickReply = { viewModel.deleteQuickReply(it) }
                )

                4 -> AiAndSafetyScreen(
                    aiType = aiType,
                    aiSkill = aiSkill,
                    aiDetails = aiDetails,
                    aiTone = aiTone,
                    onSetAiType = { viewModel.setAiType(it) },
                    onUpdateAiInputs = { s, d, t -> viewModel.updateAiInputs(s, d, t) },
                    onRunGeneration = { viewModel.runGeneration() },
                    isGenerating = isGenerating,
                    aiResult = aiResult,
                    safetyText = safetyText,
                    onUpdateSafetyText = { viewModel.updateSafetyText(it) },
                    safetyReport = viewModel.getSafetyReport()
                )
            }
        }
    }

    if (showAutoSyncDialog) {
        AutoSyncManagerDialog(
            onDismiss = { showAutoSyncDialog = false },
            onSimulateIncomingOrder = { order ->
                viewModel.saveOrder(order)
            }
        )
    }

    if (showSyncDialog) {
        FiverrSyncDialog(
            onDismiss = {
                showSyncDialog = false
                viewModel.clearSyncMessage()
            },
            onSyncUsername = { username ->
                viewModel.syncFiverrProfile(username)
            },
            onImportOrder = { order ->
                viewModel.saveOrder(order)
            },
            isSyncing = isSyncing,
            syncStatusMessage = syncStatusMessage
        )
    }
}
