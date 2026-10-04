package com.example.ui.components

import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ClientOrder
import com.example.domain.FiverrSyncService
import com.example.ui.theme.FiverrDarkGreen
import com.example.ui.theme.FiverrForest
import com.example.ui.theme.FiverrGreen
import com.example.ui.theme.FiverrMintLight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FiverrSyncDialog(
    onDismiss: () -> Unit,
    onSyncUsername: (String) -> Unit,
    onImportOrder: (ClientOrder) -> Unit,
    isSyncing: Boolean = false,
    syncStatusMessage: String? = null
) {
    var tabIndex by remember { mutableIntStateOf(0) } // 0: Live Fiverr Profile Sync, 1: Paste Order Text
    var usernameInput by remember { mutableStateOf("real_nadirali") }
    var orderTextInput by remember { mutableStateOf("") }
    val context = LocalContext.current

    val quickProfiles = listOf("real_nadirali", "algo_trader", "python_dev", "trading_bot_pro")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(FiverrGreen),
                    contentAlignment = Alignment.Center
                ) {
                    Text("fi", color = Color.White, fontWeight = FontWeight.Black, fontSize = 16.sp)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text("Sync Data from Fiverr", fontWeight = FontWeight.Bold, fontSize = 17.sp)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                SecondaryTabRow(
                    selectedTabIndex = tabIndex,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.clip(RoundedCornerShape(10.dp))
                ) {
                    Tab(
                        selected = tabIndex == 0,
                        onClick = { tabIndex = 0 },
                        text = { Text("Profile & Gigs", fontSize = 12.sp, fontWeight = FontWeight.SemiBold) }
                    )
                    Tab(
                        selected = tabIndex == 1,
                        onClick = { tabIndex = 1 },
                        text = { Text("Paste Order", fontSize = 12.sp, fontWeight = FontWeight.SemiBold) }
                    )
                }

                if (tabIndex == 0) {
                    // Profile & Gigs Live Sync
                    Text(
                        text = "Connect directly with your Fiverr profile. Automatically pulls all active gigs, pricing tiers, and queue orders without manual typing!",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    OutlinedTextField(
                        value = usernameInput,
                        onValueChange = { usernameInput = it },
                        label = { Text("Fiverr Username or Profile URL") },
                        placeholder = { Text("e.g. nadiralikhan or fiverr.com/username") },
                        prefix = { Text("@", fontWeight = FontWeight.Bold) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("input_fiverr_username")
                    )

                    // Quick handle suggestions
                    Text("Popular Seller Handles:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(quickProfiles) { handle ->
                            FilterChip(
                                selected = usernameInput == handle,
                                onClick = { usernameInput = handle },
                                label = { Text("@$handle", fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = FiverrDarkGreen,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }

                    if (syncStatusMessage != null) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = FiverrMintLight,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = FiverrDarkGreen, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(syncStatusMessage, fontSize = 12.sp, color = FiverrForest, fontWeight = FontWeight.Medium)
                            }
                        }
                    }
                } else {
                    // Paste Order Text Auto-Import
                    Text(
                        text = "Paste any text copied from your Fiverr Order page or email. The smart extractor automatically detects the buyer username, gig title, price, and deadline.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    OutlinedTextField(
                        value = orderTextInput,
                        onValueChange = { orderTextInput = it },
                        label = { Text("Order Details / Receipt Text") },
                        placeholder = { Text("e.g. Order #FO991 from @alex_tech for $85.00 Modern Minimalist Logo") },
                        minLines = 3,
                        maxLines = 5,
                        modifier = Modifier.fillMaxWidth().testTag("input_order_paste_text")
                    )

                    OutlinedButton(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clipText = clipboard.primaryClip?.getItemAt(0)?.text?.toString() ?: ""
                            if (clipText.isNotBlank()) {
                                orderTextInput = clipText
                                Toast.makeText(context, "Pasted from clipboard!", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "Clipboard is empty", Toast.LENGTH_SHORT).show()
                            }
                        },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth().testTag("btn_paste_clipboard")
                    ) {
                        Icon(Icons.Default.ContentPaste, contentDescription = null, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Paste from Clipboard", fontSize = 12.sp)
                    }
                }
            }
        },
        confirmButton = {
            if (tabIndex == 0) {
                Button(
                    onClick = { onSyncUsername(usernameInput) },
                    enabled = !isSyncing && usernameInput.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(containerColor = FiverrDarkGreen),
                    modifier = Modifier.testTag("btn_execute_fiverr_sync")
                ) {
                    if (isSyncing) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Fetching...", fontSize = 12.sp)
                    } else {
                        Icon(Icons.Default.CloudDownload, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Fetch & Sync Live Gigs", fontSize = 12.sp)
                    }
                }
            } else {
                Button(
                    onClick = {
                        val parsed = FiverrSyncService.parseFiverrOrderText(orderTextInput)
                        if (parsed != null) {
                            onImportOrder(parsed)
                            Toast.makeText(context, "Imported order from @${parsed.buyerUsername} ($${parsed.orderAmount})!", Toast.LENGTH_LONG).show()
                            onDismiss()
                        } else {
                            Toast.makeText(context, "Could not extract order details. Please check text.", Toast.LENGTH_SHORT).show()
                        }
                    },
                    enabled = orderTextInput.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(containerColor = FiverrDarkGreen),
                    modifier = Modifier.testTag("btn_execute_order_import")
                ) {
                    Icon(Icons.Default.Assignment, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Auto-Import Order", fontSize = 12.sp)
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Close") }
        }
    )
}
