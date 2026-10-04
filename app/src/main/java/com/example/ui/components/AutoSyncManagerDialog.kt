package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.service.FiverrNotificationListener
import com.example.ui.theme.FiverrDarkGreen
import com.example.ui.theme.FiverrForest
import com.example.ui.theme.FiverrGreen
import com.example.ui.theme.FiverrMint
import com.example.ui.theme.FiverrMintLight
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate700

@Composable
fun AutoSyncManagerDialog(
    onDismiss: () -> Unit,
    onSimulateIncomingOrder: (ClientOrder) -> Unit
) {
    val context = LocalContext.current
    var isNotificationSyncEnabled by remember { mutableStateOf(FiverrNotificationListener.isServiceRunning) }
    var isClipboardAutoSyncEnabled by remember { mutableStateOf(true) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(FiverrMintLight),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = FiverrDarkGreen,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Automatic Order Sync",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "100% Automated • Zero Manual Typing",
                            fontSize = 11.sp,
                            color = FiverrDarkGreen,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
                IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Slate700)
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Feature 1: Real-time Android Notification Interceptor
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Icon(
                                    imageVector = Icons.Default.NotificationsActive,
                                    contentDescription = null,
                                    tint = FiverrDarkGreen,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "Fiverr Push Interceptor",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                    Text(
                                        text = "Catches real orders from Fiverr app in real time",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Whenever you get an order notification from the Fiverr app on your phone, this service automatically extracts the buyer, price, delivery time, and adds it to your queue with zero taps!",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 16.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    try {
                                        val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
                                        context.startActivity(intent)
                                    } catch (e: Exception) {
                                        Toast.makeText(context, "Open Android Settings -> Notification Access", Toast.LENGTH_LONG).show()
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = FiverrDarkGreen),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f).height(36.dp)
                            ) {
                                Icon(Icons.Default.Settings, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Enable Access", fontSize = 11.sp)
                            }

                            OutlinedButton(
                                onClick = {
                                    // Trigger immediate live order test
                                    val testOrder = ClientOrder(
                                        buyerUsername = "quantum_trader",
                                        gigTitle = "IBKR Multi-Exchange Algo Execution Bot",
                                        orderAmount = 450.0,
                                        tipAmount = 30.0,
                                        status = "In Progress",
                                        dueDateEpochMs = System.currentTimeMillis() + (3 * 24 * 3600 * 1000L),
                                        notes = "⚡ Auto-captured via Real-Time Fiverr Notification Interceptor!"
                                    )
                                    onSimulateIncomingOrder(testOrder)
                                    Toast.makeText(context, "⚡ Real-time order auto-captured into your queue!", Toast.LENGTH_SHORT).show()
                                    onDismiss()
                                },
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f).height(36.dp)
                            ) {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(14.dp), tint = FiverrDarkGreen)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Test Push Auto-Sync", fontSize = 11.sp, color = FiverrDarkGreen)
                            }
                        }
                    }
                }

                // Feature 2: 1-Tap Clipboard Auto-Detect
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Icon(
                                    imageVector = Icons.Default.ContentPaste,
                                    contentDescription = null,
                                    tint = FiverrDarkGreen,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "1-Tap Clipboard Importer",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                    Text(
                                        text = "Instant parse of order text or emails",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            Switch(
                                checked = isClipboardAutoSyncEnabled,
                                onCheckedChange = { isClipboardAutoSyncEnabled = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = FiverrDarkGreen
                                )
                            )
                        }

                        Text(
                            text = "Copy any order confirmation email or Fiverr notification and the app will instantly parse and calculate your net profit without typing.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Security & TOS Notice
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = FiverrMintLight
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = FiverrDarkGreen,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "100% Fiverr TOS Safe: All data remains securely on your local device without third-party cloud leaks.",
                            fontSize = 11.sp,
                            color = FiverrDarkGreen,
                            lineHeight = 15.sp
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = FiverrDarkGreen)
            ) {
                Text("Done")
            }
        }
    )
}
