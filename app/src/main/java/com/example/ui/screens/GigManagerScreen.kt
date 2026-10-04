package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Gig
import com.example.domain.GigSeoAnalyzer
import com.example.domain.SeoAnalysisReport
import com.example.ui.theme.DangerRed
import com.example.ui.theme.FiverrDarkGreen
import com.example.ui.theme.FiverrForest
import com.example.ui.theme.FiverrGreen
import com.example.ui.theme.FiverrMint
import com.example.ui.theme.FiverrMintLight
import com.example.ui.theme.GoldStar
import com.example.ui.theme.InfoBlue
import com.example.ui.theme.Slate700
import com.example.ui.theme.WarningOrange

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GigManagerScreen(
    gigs: List<Gig>,
    onSaveGig: (Gig) -> Unit,
    onDeleteGig: (Gig) -> Unit,
    seoTitle: String,
    seoTags: String,
    seoDescription: String,
    onUpdateSeoInputs: (String, String, String) -> Unit,
    seoReport: SeoAnalysisReport,
    onSyncClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var subTab by remember { mutableIntStateOf(0) } // 0: My Gigs, 1: Live SEO Optimizer
    var showAddDialog by remember { mutableStateOf(false) }
    var gigToEdit by remember { mutableStateOf<Gig?>(null) }

    Column(modifier = modifier.fillMaxSize()) {
        // Tab Row
        SecondaryTabRow(
            selectedTabIndex = subTab,
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Tab(
                selected = subTab == 0,
                onClick = { subTab = 0 },
                text = { Text("My Gigs (${gigs.size})", fontWeight = FontWeight.SemiBold) },
                modifier = Modifier.testTag("tab_my_gigs")
            )
            Tab(
                selected = subTab == 1,
                onClick = { subTab = 1 },
                text = { Text("Live SEO Optimizer", fontWeight = FontWeight.SemiBold) },
                modifier = Modifier.testTag("tab_live_seo")
            )
        }

        Box(modifier = Modifier.fillMaxSize()) {
            if (subTab == 0) {
                // My Gigs List
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item { Spacer(modifier = Modifier.height(6.dp)) }

                    item {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = FiverrMintLight,
                            border = BorderStroke(1.dp, FiverrGreen.copy(alpha = 0.5f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(onClick = onSyncClick)
                                .testTag("banner_auto_import_gigs")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                    Icon(Icons.Default.CloudDownload, contentDescription = null, tint = FiverrDarkGreen, modifier = Modifier.size(22.dp))
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text("Auto-Import Live Fiverr Gigs", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = FiverrDarkGreen)
                                        Text("Pulls your active gigs, packages & SEO keywords in 1-click", fontSize = 11.sp, color = Slate700)
                                    }
                                }
                                Button(
                                    onClick = onSyncClick,
                                    colors = ButtonDefaults.buttonColors(containerColor = FiverrDarkGreen),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.height(32.dp)
                                ) {
                                    Text("Sync", fontSize = 11.sp)
                                }
                            }
                        }
                    }

                    if (gigs.isEmpty()) {
                        item {
                            EmptyGigsView(
                                onAddClick = { showAddDialog = true },
                                onSyncClick = onSyncClick
                            )
                        }
                    } else {
                        items(gigs, key = { it.id }) { gig ->
                            GigCard(
                                gig = gig,
                                onEdit = { gigToEdit = gig },
                                onDelete = { onDeleteGig(gig) },
                                onAnalyzeSeo = {
                                    onUpdateSeoInputs(gig.title, gig.tags, gig.description)
                                    subTab = 1
                                }
                            )
                        }
                    }

                    item { Spacer(modifier = Modifier.height(80.dp)) }
                }

                FloatingActionButton(
                    onClick = { showAddDialog = true },
                    containerColor = FiverrGreen,
                    contentColor = Color.White,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(20.dp)
                        .testTag("add_gig_fab")
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add New Gig")
                }
            } else {
                // Live SEO Optimizer View
                SeoOptimizerView(
                    title = seoTitle,
                    tags = seoTags,
                    description = seoDescription,
                    onUpdateInputs = onUpdateSeoInputs,
                    report = seoReport
                )
            }
        }
    }

    if (showAddDialog || gigToEdit != null) {
        GigFormDialog(
            initialGig = gigToEdit,
            onDismiss = {
                showAddDialog = false
                gigToEdit = null
            },
            onSave = { savedGig ->
                onSaveGig(savedGig)
                showAddDialog = false
                gigToEdit = null
            }
        )
    }
}

@Composable
fun GigCard(
    gig: Gig,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onAnalyzeSeo: () -> Unit
) {
    val report = remember(gig.title, gig.tags, gig.description) {
        GigSeoAnalyzer.analyze(gig.title, gig.tags, gig.description)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("gig_card_${gig.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Category & Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Text(
                        text = gig.category,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // SEO Grade badge
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (report.overallScore >= 70) FiverrMintLight else WarningOrange.copy(alpha = 0.15f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "SEO: ${report.grade} (${report.overallScore})",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (report.overallScore >= 70) FiverrDarkGreen else WarningOrange
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (gig.status == "Active") FiverrMintLight else MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Text(
                            text = gig.status,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (gig.status == "Active") FiverrDarkGreen else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = gig.title,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 20.sp
            )

            if (gig.tags.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Tags: ${gig.tags}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
            Spacer(modifier = Modifier.height(10.dp))

            // 3 Packages Summary
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PackageMiniBox(
                    tier = "Basic",
                    price = gig.basicPrice,
                    days = gig.basicDeliveryDays,
                    modifier = Modifier.weight(1f)
                )
                PackageMiniBox(
                    tier = "Standard",
                    price = gig.standardPrice,
                    days = gig.standardDeliveryDays,
                    isPopular = true,
                    modifier = Modifier.weight(1f)
                )
                PackageMiniBox(
                    tier = "Premium",
                    price = gig.premiumPrice,
                    days = gig.premiumDeliveryDays,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Actions: SEO check, edit, delete
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onAnalyzeSeo,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(34.dp)
                ) {
                    Icon(imageVector = Icons.Default.Search, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Check SEO", fontSize = 11.sp)
                }

                Row {
                    IconButton(onClick = onEdit, modifier = Modifier.size(34.dp)) {
                        Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit", modifier = Modifier.size(16.dp))
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(34.dp)) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = DangerRed, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun PackageMiniBox(
    tier: String,
    price: Double,
    days: Int,
    isPopular: Boolean = false,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = if (isPopular) FiverrMintLight else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        border = if (isPopular) androidx.compose.foundation.BorderStroke(1.dp, FiverrGreen) else null
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = tier,
                fontSize = 11.sp,
                fontWeight = if (isPopular) FontWeight.Bold else FontWeight.Medium,
                color = if (isPopular) FiverrDarkGreen else MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "$${price.toInt()}",
                fontSize = 14.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "${days}d",
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun SeoOptimizerView(
    title: String,
    tags: String,
    description: String,
    onUpdateInputs: (String, String, String) -> Unit,
    report: SeoAnalysisReport
) {
    var editTitle by remember(title) { mutableStateOf(title) }
    var editTags by remember(tags) { mutableStateOf(tags) }
    var editDesc by remember(description) { mutableStateOf(description) }

    val scoreColor = when {
        report.overallScore >= 80 -> FiverrDarkGreen
        report.overallScore >= 65 -> WarningOrange
        else -> DangerRed
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            // Score Header Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Fiverr Algorithm Health Score",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Text(
                            text = "Rank higher on Fiverr search results with optimal keywords, tags & structure.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(68.dp)
                            .clip(CircleShape)
                            .background(scoreColor.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${report.overallScore}",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Black,
                                color = scoreColor
                            )
                            Text(
                                text = report.grade,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = scoreColor
                            )
                        }
                    }
                }
            }
        }

        // Live Inputs
        item {
            OutlinedTextField(
                value = editTitle,
                onValueChange = {
                    editTitle = it
                    onUpdateInputs(editTitle, editTags, editDesc)
                },
                label = { Text("Gig Title (starts with 'I will')") },
                supportingText = {
                    Text("${editTitle.length} characters (Ideal: 40-70 chars)")
                },
                modifier = Modifier.fillMaxWidth().testTag("seo_input_title")
            )
        }

        item {
            OutlinedTextField(
                value = editTags,
                onValueChange = {
                    editTags = it
                    onUpdateInputs(editTitle, editTags, editDesc)
                },
                label = { Text("5 Search Tags (comma separated)") },
                supportingText = {
                    val count = editTags.split(",").filter { it.isNotBlank() }.size
                    Text("$count/5 tags used (Exact 5 tags recommended)")
                },
                modifier = Modifier.fillMaxWidth().testTag("seo_input_tags")
            )
        }

        item {
            OutlinedTextField(
                value = editDesc,
                onValueChange = {
                    editDesc = it
                    onUpdateInputs(editTitle, editTags, editDesc)
                },
                label = { Text("Gig Description") },
                supportingText = {
                    val words = editDesc.trim().split("\\s+".toRegex()).filter { it.isNotBlank() }.size
                    Text("$words words (Recommended: 120-300 words with bullet points)")
                },
                minLines = 3,
                maxLines = 6,
                modifier = Modifier.fillMaxWidth().testTag("seo_input_description")
            )
        }

        // Actionable Feedback Tips
        item {
            Text(
                text = "SEO Feedback & Recommendations",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }

        items(report.titleTips) { tip ->
            SeoTipItem(tip = tip, category = "Title")
        }

        items(report.tagTips) { tip ->
            SeoTipItem(tip = tip, category = "Tags")
        }

        items(report.descriptionTips) { tip ->
            SeoTipItem(tip = tip, category = "Description")
        }

        item {
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun SeoTipItem(tip: com.example.domain.SeoTip, category: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        color = if (tip.isPositive) FiverrMintLight else WarningOrange.copy(alpha = 0.1f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (tip.isPositive) Icons.Default.Check else Icons.Default.Info,
                contentDescription = null,
                tint = if (tip.isPositive) FiverrDarkGreen else WarningOrange,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = "[$category] ${tip.message}",
                    fontSize = 12.sp,
                    color = if (tip.isPositive) FiverrForest else WarningOrange,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun GigFormDialog(
    initialGig: Gig?,
    onDismiss: () -> Unit,
    onSave: (Gig) -> Unit
) {
    var title by remember { mutableStateOf(initialGig?.title ?: "I will ") }
    var category by remember { mutableStateOf(initialGig?.category ?: "Graphics & Design") }
    var tags by remember { mutableStateOf(initialGig?.tags ?: "") }
    var basicPrice by remember { mutableStateOf(initialGig?.basicPrice?.toInt()?.toString() ?: "15") }
    var basicDays by remember { mutableStateOf(initialGig?.basicDeliveryDays?.toString() ?: "2") }
    var standardPrice by remember { mutableStateOf(initialGig?.standardPrice?.toInt()?.toString() ?: "45") }
    var standardDays by remember { mutableStateOf(initialGig?.standardDeliveryDays?.toString() ?: "3") }
    var premiumPrice by remember { mutableStateOf(initialGig?.premiumPrice?.toInt()?.toString() ?: "95") }
    var premiumDays by remember { mutableStateOf(initialGig?.premiumDeliveryDays?.toString() ?: "5") }
    var description by remember { mutableStateOf(initialGig?.description ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (initialGig == null) "Create New Gig" else "Edit Gig") },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Gig Title (e.g. I will design...)") },
                        modifier = Modifier.fillMaxWidth().testTag("gig_form_title")
                    )
                }
                item {
                    OutlinedTextField(
                        value = category,
                        onValueChange = { category = it },
                        label = { Text("Category") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = tags,
                        onValueChange = { tags = it },
                        label = { Text("5 Tags (comma separated)") },
                        modifier = Modifier.fillMaxWidth().testTag("gig_form_tags")
                    )
                }
                item {
                    Text("Packages (Price & Delivery Days)", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        OutlinedTextField(
                            value = basicPrice,
                            onValueChange = { basicPrice = it },
                            label = { Text("Basic $") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = basicDays,
                            onValueChange = { basicDays = it },
                            label = { Text("Days") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        OutlinedTextField(
                            value = standardPrice,
                            onValueChange = { standardPrice = it },
                            label = { Text("Std $") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = standardDays,
                            onValueChange = { standardDays = it },
                            label = { Text("Days") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        OutlinedTextField(
                            value = premiumPrice,
                            onValueChange = { premiumPrice = it },
                            label = { Text("Prem $") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = premiumDays,
                            onValueChange = { premiumDays = it },
                            label = { Text("Days") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                item {
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Gig Description") },
                        minLines = 3,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val result = (initialGig ?: Gig(
                        title = title,
                        category = category,
                        tags = tags
                    )).copy(
                        title = title,
                        category = category,
                        tags = tags,
                        basicPrice = basicPrice.toDoubleOrNull() ?: 15.0,
                        basicDeliveryDays = basicDays.toIntOrNull() ?: 2,
                        standardPrice = standardPrice.toDoubleOrNull() ?: 45.0,
                        standardDeliveryDays = standardDays.toIntOrNull() ?: 3,
                        premiumPrice = premiumPrice.toDoubleOrNull() ?: 95.0,
                        premiumDeliveryDays = premiumDays.toIntOrNull() ?: 5,
                        description = description,
                        updatedAt = System.currentTimeMillis()
                    )
                    onSave(result)
                },
                colors = ButtonDefaults.buttonColors(containerColor = FiverrDarkGreen),
                modifier = Modifier.testTag("save_gig_btn")
            ) {
                Text("Save Gig")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun EmptyGigsView(
    onAddClick: () -> Unit,
    onSyncClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 36.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(68.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = Icons.Default.Star, contentDescription = null, tint = FiverrDarkGreen, modifier = Modifier.size(34.dp))
        }
        Spacer(modifier = Modifier.height(14.dp))
        Text("No Gigs Added Yet", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Text(
            text = "Pull your active gigs directly from your Fiverr profile, or create one manually.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 32.dp, vertical = 4.dp)
        )
        Spacer(modifier = Modifier.height(14.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Button(onClick = onSyncClick, colors = ButtonDefaults.buttonColors(containerColor = FiverrDarkGreen)) {
                Icon(imageVector = Icons.Default.CloudDownload, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Sync from Fiverr")
            }
            OutlinedButton(onClick = onAddClick) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Add Manually")
            }
        }
    }
}
