package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ClientOrder
import com.example.ui.theme.DangerRed
import com.example.ui.theme.FiverrDarkGreen
import com.example.ui.theme.FiverrGreen
import com.example.ui.theme.FiverrMint
import com.example.ui.theme.FiverrMintLight
import com.example.ui.theme.InfoBlue
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate700
import com.example.ui.theme.WarningOrange
import java.util.concurrent.TimeUnit

@Composable
fun OrdersDashboardScreen(
    orders: List<ClientOrder>,
    filterStatus: String,
    onFilterChange: (String) -> Unit,
    onSaveOrder: (ClientOrder) -> Unit,
    onUpdateStatus: (ClientOrder, String) -> Unit,
    onDeleteOrder: (ClientOrder) -> Unit,
    onSyncClick: () -> Unit,
    onOpenExternalFiverr: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var orderToEdit by remember { mutableStateOf<ClientOrder?>(null) }

    val activeOrders = orders.filter { it.status != "Completed" && it.status != "Cancelled" }
    val inRevisionOrders = orders.filter { it.status == "Revision Requested" }
    val totalQueueGross = activeOrders.sumOf { it.orderAmount + it.tipAmount }
    val totalQueueNet = totalQueueGross * 0.80

    val filteredOrders = if (filterStatus == "All") {
        orders
    } else {
        orders.filter { it.status == filterStatus }
    }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                // Statistics Grid Card
                DashboardMetricsCard(
                    activeCount = activeOrders.size,
                    revisionCount = inRevisionOrders.size,
                    grossQueue = totalQueueGross,
                    netQueue = totalQueueNet
                )
            }

            item {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = FiverrMintLight,
                    border = BorderStroke(1.dp, FiverrGreen.copy(alpha = 0.5f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onSyncClick)
                        .testTag("banner_auto_import_fiverr")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = FiverrDarkGreen, modifier = Modifier.size(22.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("⚡ Real-Time Push Auto-Sync", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = FiverrDarkGreen)
                                Text("Auto-captures incoming orders from Fiverr app with zero typing!", fontSize = 11.sp, color = Slate700)
                            }
                        }
                        Button(
                            onClick = onSyncClick,
                            colors = ButtonDefaults.buttonColors(containerColor = FiverrDarkGreen),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Text("Auto-Sync", fontSize = 11.sp)
                        }
                    }
                }
            }

            item {
                // Filter chips
                val filterOptions = listOf("All", "In Progress", "Revision Requested", "Delivered", "Completed")
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    items(filterOptions) { status ->
                        FilterChip(
                            selected = filterStatus == status,
                            onClick = { onFilterChange(status) },
                            label = { Text(status, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = FiverrDarkGreen,
                                selectedLabelColor = Color.White
                            ),
                            modifier = Modifier.testTag("filter_chip_$status")
                        )
                    }
                }
            }

            if (filteredOrders.isEmpty()) {
                item {
                    EmptyOrdersState(
                        onAddClick = { showAddDialog = true },
                        onSyncClick = onSyncClick,
                        onOpenExternalFiverr = onOpenExternalFiverr
                    )
                }
            } else {
                items(filteredOrders, key = { it.id }) { order ->
                    OrderItemCard(
                        order = order,
                        onUpdateStatus = { newStatus -> onUpdateStatus(order, newStatus) },
                        onEditClick = { orderToEdit = order },
                        onDeleteClick = { onDeleteOrder(order) }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }

        // Add Order Floating Action Button
        FloatingActionButton(
            onClick = { showAddDialog = true },
            containerColor = FiverrGreen,
            contentColor = Color.White,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
                .testTag("add_order_fab")
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Add New Order")
        }
    }

    // Add / Edit Order Dialog
    if (showAddDialog || orderToEdit != null) {
        OrderFormDialog(
            initialOrder = orderToEdit,
            onDismiss = {
                showAddDialog = false
                orderToEdit = null
            },
            onSave = { savedOrder ->
                onSaveOrder(savedOrder)
                showAddDialog = false
                orderToEdit = null
            }
        )
    }
}

@Composable
fun DashboardMetricsCard(
    activeCount: Int,
    revisionCount: Int,
    grossQueue: Double,
    netQueue: Double
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                text = "Live Order Queue & Take-Home",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Bold
            )

            // Row 1: Active Orders & In Revision (Spacious 2-column grid)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(FiverrMintLight),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = FiverrDarkGreen, modifier = Modifier.size(18.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "$activeCount",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                color = FiverrDarkGreen,
                                maxLines = 1
                            )
                            Text(
                                text = "Active Orders",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1
                            )
                        }
                    }
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (revisionCount > 0) WarningOrange.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null, tint = if (revisionCount > 0) WarningOrange else Slate700, modifier = Modifier.size(18.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "$revisionCount",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                color = if (revisionCount > 0) WarningOrange else Slate700,
                                maxLines = 1
                            )
                            Text(
                                text = "In Revision",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1
                            )
                        }
                    }
                }
            }

            // Row 2: Gross Queue & Net Earnings (Spacious 2-column grid)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("$", fontWeight = FontWeight.Black, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface)
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "$${String.format("%.0f", grossQueue)}",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1
                            )
                            Text(
                                text = "Gross Queue",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1
                            )
                        }
                    }
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(FiverrMintLight),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("%", fontWeight = FontWeight.Black, fontSize = 16.sp, color = FiverrDarkGreen)
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "$${String.format("%.0f", netQueue)}",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                color = FiverrDarkGreen,
                                maxLines = 1
                            )
                            Text(
                                text = "Net Take-Home (80%)",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = FiverrDarkGreen,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MetricItem(label: String, value: String, highlightColor: Color) {
    Column {
        Text(
            text = value,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = highlightColor
        )
        Text(
            text = label,
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun OrderItemCard(
    order: ClientOrder,
    onUpdateStatus: (String) -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val now = System.currentTimeMillis()
    val remainingMs = order.dueDateEpochMs - now
    val isPastDue = remainingMs < 0 && (order.status == "In Progress" || order.status == "Revision Requested")

    val dueText = when {
        isPastDue -> "Late by ${formatDuration(kotlin.math.abs(remainingMs))}"
        remainingMs > 0 -> "Due in ${formatDuration(remainingMs)}"
        else -> "Delivered on schedule"
    }

    val statusBadgeColor = when (order.status) {
        "In Progress" -> InfoBlue
        "Revision Requested" -> WarningOrange
        "Delivered" -> FiverrGreen
        "Completed" -> FiverrDarkGreen
        else -> DangerRed
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("order_card_${order.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header: Buyer & Status Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f).padding(end = 8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(FiverrMintLight),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = FiverrDarkGreen,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "@${order.buyerUsername}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = order.gigTitle,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = statusBadgeColor.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = order.status,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = statusBadgeColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        maxLines = 1
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Pricing & Deadline details row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Price & Net Breakdown
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f, fill = false).padding(end = 6.dp)
                ) {
                    Text(
                        text = "$${String.format("%.0f", order.orderAmount)}",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1
                    )
                    if (order.tipAmount > 0) {
                        Text(
                            text = " +$${String.format("%.0f", order.tipAmount)} tip",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = FiverrDarkGreen,
                            maxLines = 1
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "(Net: $${String.format("%.0f", order.netEarnings)})",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )
                }

                // Countdown badge
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isPastDue) DangerRed.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (isPastDue) Icons.Default.ErrorOutline else Icons.Default.Alarm,
                            contentDescription = null,
                            tint = if (isPastDue) DangerRed else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = dueText,
                            fontSize = 10.sp,
                            fontWeight = if (isPastDue) FontWeight.Bold else FontWeight.Normal,
                            color = if (isPastDue) DangerRed else MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1
                        )
                    }
                }
            }

            if (order.notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Notes: ${order.notes}",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Status Transition Quick Actions
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    if (order.status == "In Progress" || order.status == "Revision Requested") {
                        Button(
                            onClick = { onUpdateStatus("Delivered") },
                            colors = ButtonDefaults.buttonColors(containerColor = FiverrDarkGreen),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(34.dp).testTag("deliver_order_${order.id}")
                        ) {
                            Icon(imageVector = Icons.Default.Send, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Deliver", fontSize = 11.sp)
                        }
                    }

                    if (order.status == "Delivered") {
                        Button(
                            onClick = { onUpdateStatus("Completed") },
                            colors = ButtonDefaults.buttonColors(containerColor = FiverrGreen),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(34.dp).testTag("complete_order_${order.id}")
                        ) {
                            Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Complete", fontSize = 11.sp)
                        }

                        OutlinedButton(
                            onClick = { onUpdateStatus("Revision Requested") },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(34.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Revision", fontSize = 11.sp)
                        }
                    }
                }

                // Edit & Delete icons
                Row {
                    IconButton(
                        onClick = onEditClick,
                        modifier = Modifier.size(34.dp).testTag("edit_order_${order.id}")
                    ) {
                        Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit", modifier = Modifier.size(16.dp))
                    }
                    IconButton(
                        onClick = onDeleteClick,
                        modifier = Modifier.size(34.dp).testTag("delete_order_${order.id}")
                    ) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = DangerRed, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun OrderFormDialog(
    initialOrder: ClientOrder?,
    onDismiss: () -> Unit,
    onSave: (ClientOrder) -> Unit
) {
    var buyer by remember { mutableStateOf(initialOrder?.buyerUsername ?: "") }
    var gigTitle by remember { mutableStateOf(initialOrder?.gigTitle ?: "") }
    var amountText by remember { mutableStateOf(initialOrder?.orderAmount?.toInt()?.toString() ?: "50") }
    var tipText by remember { mutableStateOf(initialOrder?.tipAmount?.toInt()?.toString() ?: "0") }
    var deliveryDaysText by remember { mutableStateOf("3") }
    var notes by remember { mutableStateOf(initialOrder?.notes ?: "") }
    var status by remember { mutableStateOf(initialOrder?.status ?: "In Progress") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(if (initialOrder == null) "New Fiverr Order" else "Edit Order")
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = buyer,
                    onValueChange = { buyer = it.removePrefix("@") },
                    label = { Text("Buyer Username") },
                    prefix = { Text("@") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("order_input_buyer")
                )

                OutlinedTextField(
                    value = gigTitle,
                    onValueChange = { gigTitle = it },
                    label = { Text("Gig Title / Project") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("order_input_gig")
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = amountText,
                        onValueChange = { amountText = it },
                        label = { Text("Price ($)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f).testTag("order_input_amount")
                    )
                    OutlinedTextField(
                        value = tipText,
                        onValueChange = { tipText = it },
                        label = { Text("Tip ($)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                }

                if (initialOrder == null) {
                    OutlinedTextField(
                        value = deliveryDaysText,
                        onValueChange = { deliveryDaysText = it },
                        label = { Text("Delivery Due In (Days)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Order Notes / Brief") },
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val price = amountText.toDoubleOrNull() ?: 10.0
                    val tip = tipText.toDoubleOrNull() ?: 0.0
                    val days = deliveryDaysText.toDoubleOrNull() ?: 3.0
                    val dueEpoch = initialOrder?.dueDateEpochMs
                        ?: (System.currentTimeMillis() + (days * 24 * 3600 * 1000).toLong())

                    val resultOrder = (initialOrder ?: ClientOrder(
                        buyerUsername = buyer.ifBlank { "fiverr_buyer" },
                        gigTitle = gigTitle.ifBlank { "Custom Project" },
                        orderAmount = price,
                        tipAmount = tip,
                        dueDateEpochMs = dueEpoch,
                        notes = notes,
                        status = status
                    )).copy(
                        buyerUsername = buyer.ifBlank { "fiverr_buyer" },
                        gigTitle = gigTitle.ifBlank { "Custom Project" },
                        orderAmount = price,
                        tipAmount = tip,
                        notes = notes,
                        status = status
                    )
                    onSave(resultOrder)
                },
                colors = ButtonDefaults.buttonColors(containerColor = FiverrDarkGreen),
                modifier = Modifier.testTag("save_order_button")
            ) {
                Text("Save Order")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun EmptyOrdersState(
    onAddClick: () -> Unit,
    onSyncClick: () -> Unit,
    onOpenExternalFiverr: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(FiverrMintLight),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.ShoppingCart,
                contentDescription = null,
                tint = FiverrDarkGreen,
                modifier = Modifier.size(32.dp)
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "No Active Orders in Queue",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Open your Fiverr app or Chrome to manage active orders without captcha issues, or paste order details below to track net earnings & delivery deadlines.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            modifier = Modifier.padding(horizontal = 24.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { onOpenExternalFiverr("https://www.fiverr.com/manage_orders") },
                colors = ButtonDefaults.buttonColors(containerColor = FiverrDarkGreen),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth(0.85f)
            ) {
                Icon(imageVector = Icons.Default.CloudDownload, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Open Fiverr App / Chrome ↗", fontWeight = FontWeight.Bold)
            }

            Row(
                modifier = Modifier.fillMaxWidth(0.85f),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onSyncClick,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Paste Order Text", fontSize = 12.sp)
                }
                OutlinedButton(
                    onClick = onAddClick,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("+ Add Order", fontSize = 12.sp)
                }
            }
        }
    }
}

private fun formatDuration(millis: Long): String {
    val hours = TimeUnit.MILLISECONDS.toHours(millis)
    val days = hours / 24
    val remainingHours = hours % 24
    return when {
        days > 0 -> "${days}d ${remainingHours}h"
        hours > 0 -> "${hours}h"
        else -> "< 1h"
    }
}
