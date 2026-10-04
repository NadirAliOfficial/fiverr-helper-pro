package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.QuestionAnswer
import androidx.compose.material.icons.filled.Tune
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.QuickReply
import com.example.ui.theme.DangerRed
import com.example.ui.theme.FiverrDarkGreen
import com.example.ui.theme.FiverrGreen
import com.example.ui.theme.FiverrMintLight

@Composable
fun QuickRepliesScreen(
    quickReplies: List<QuickReply>,
    selectedCategory: String,
    onCategoryChange: (String) -> Unit,
    onSaveQuickReply: (QuickReply) -> Unit,
    onDeleteQuickReply: (QuickReply) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var buyerName by remember { mutableStateOf("") }
    var gigName by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var deliveryDays by remember { mutableStateOf("") }
    var showVariablesPanel by remember { mutableStateOf(false) }

    var showAddDialog by remember { mutableStateOf(false) }
    var replyToEdit by remember { mutableStateOf<QuickReply?>(null) }

    val categories = listOf("All", "First Contact", "Offer Sent", "Requirements", "Delivery", "Revision", "Safety / TOS")

    val filteredReplies = if (selectedCategory == "All") {
        quickReplies
    } else {
        quickReplies.filter { it.category == selectedCategory }
    }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(6.dp))
                // Variable Replacement Bar
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Tune,
                                    contentDescription = null,
                                    tint = FiverrDarkGreen,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Auto-Fill Placeholders",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }

                            TextButton(onClick = { showVariablesPanel = !showVariablesPanel }) {
                                Text(
                                    text = if (showVariablesPanel) "Hide" else "Customize",
                                    fontSize = 12.sp,
                                    color = FiverrDarkGreen
                                )
                                Icon(
                                    imageVector = if (showVariablesPanel) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                    contentDescription = null,
                                    tint = FiverrDarkGreen,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        AnimatedVisibility(visible = showVariablesPanel) {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 8.dp)) {
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    OutlinedTextField(
                                        value = buyerName,
                                        onValueChange = { buyerName = it },
                                        label = { Text("Buyer Name") },
                                        placeholder = { Text("e.g. Alex") },
                                        singleLine = true,
                                        modifier = Modifier.weight(1f).testTag("var_buyer_name")
                                    )
                                    OutlinedTextField(
                                        value = gigName,
                                        onValueChange = { gigName = it },
                                        label = { Text("Gig / Service") },
                                        placeholder = { Text("e.g. Logo Design") },
                                        singleLine = true,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    OutlinedTextField(
                                        value = price,
                                        onValueChange = { price = it },
                                        label = { Text("Price ($)") },
                                        placeholder = { Text("50") },
                                        singleLine = true,
                                        modifier = Modifier.weight(1f)
                                    )
                                    OutlinedTextField(
                                        value = deliveryDays,
                                        onValueChange = { deliveryDays = it },
                                        label = { Text("Delivery Days") },
                                        placeholder = { Text("3") },
                                        singleLine = true,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Categories row
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(vertical = 2.dp)
                ) {
                    items(categories) { cat ->
                        FilterChip(
                            selected = selectedCategory == cat,
                            onClick = { onCategoryChange(cat) },
                            label = { Text(cat, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = FiverrDarkGreen,
                                selectedLabelColor = Color.White
                            ),
                            modifier = Modifier.testTag("reply_cat_$cat")
                        )
                    }
                }
            }

            if (filteredReplies.isEmpty()) {
                item {
                    Text(
                        text = "No templates in this category.",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 30.dp)
                    )
                }
            } else {
                items(filteredReplies, key = { it.id }) { reply ->
                    val resolvedText = remember(reply.messageText, buyerName, gigName, price, deliveryDays) {
                        reply.messageText
                            .replace("{buyer_name}", buyerName.ifBlank { "there" })
                            .replace("{gig_name}", gigName.ifBlank { "your project" })
                            .replace("{price}", price.ifBlank { "50" })
                            .replace("{delivery_days}", deliveryDays.ifBlank { "3" })
                    }

                    QuickReplyCard(
                        reply = reply,
                        formattedMessage = resolvedText,
                        onCopy = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("Fiverr Quick Reply", resolvedText))
                            Toast.makeText(context, "Copied to clipboard!", Toast.LENGTH_SHORT).show()
                        },
                        onEdit = { replyToEdit = reply },
                        onDelete = { onDeleteQuickReply(reply) }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }

        FloatingActionButton(
            onClick = { showAddDialog = true },
            containerColor = FiverrGreen,
            contentColor = Color.White,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
                .testTag("add_reply_fab")
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Add Template")
        }
    }

    if (showAddDialog || replyToEdit != null) {
        QuickReplyDialog(
            initialReply = replyToEdit,
            onDismiss = {
                showAddDialog = false
                replyToEdit = null
            },
            onSave = { saved ->
                onSaveQuickReply(saved)
                showAddDialog = false
                replyToEdit = null
            }
        )
    }
}

@Composable
fun QuickReplyCard(
    reply: QuickReply,
    formattedMessage: String,
    onCopy: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("reply_card_${reply.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = reply.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = FiverrMintLight,
                        modifier = Modifier.padding(top = 2.dp)
                    ) {
                        Text(
                            text = reply.category,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = FiverrDarkGreen,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Button(
                        onClick = onCopy,
                        colors = ButtonDefaults.buttonColors(containerColor = FiverrDarkGreen),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(34.dp).testTag("copy_reply_${reply.id}")
                    ) {
                        Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(13.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Copy", fontSize = 11.sp)
                    }

                    if (!reply.isPreset) {
                        IconButton(onClick = onEdit, modifier = Modifier.size(34.dp)) {
                            Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit", modifier = Modifier.size(15.dp))
                        }
                        IconButton(onClick = onDelete, modifier = Modifier.size(34.dp)) {
                            Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = DangerRed, modifier = Modifier.size(15.dp))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            ) {
                Text(
                    text = formattedMessage,
                    fontSize = 12.sp,
                    lineHeight = 17.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(10.dp)
                )
            }
        }
    }
}

@Composable
fun QuickReplyDialog(
    initialReply: QuickReply?,
    onDismiss: () -> Unit,
    onSave: (QuickReply) -> Unit
) {
    var title by remember { mutableStateOf(initialReply?.title ?: "") }
    var category by remember { mutableStateOf(initialReply?.category ?: "First Contact") }
    var message by remember { mutableStateOf(initialReply?.messageText ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (initialReply == null) "New Quick Reply" else "Edit Quick Reply") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Template Title") },
                    modifier = Modifier.fillMaxWidth().testTag("reply_dialog_title")
                )
                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("Category") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = message,
                    onValueChange = { message = it },
                    label = { Text("Message Text (use {buyer_name}, {gig_name})") },
                    minLines = 4,
                    modifier = Modifier.fillMaxWidth().testTag("reply_dialog_message")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val res = (initialReply ?: QuickReply(
                        title = title,
                        category = category,
                        messageText = message
                    )).copy(
                        title = title.ifBlank { "Custom Quick Reply" },
                        category = category,
                        messageText = message
                    )
                    onSave(res)
                },
                colors = ButtonDefaults.buttonColors(containerColor = FiverrDarkGreen),
                modifier = Modifier.testTag("save_reply_btn")
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
