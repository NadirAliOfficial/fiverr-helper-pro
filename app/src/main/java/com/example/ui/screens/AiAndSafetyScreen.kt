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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.domain.GenerationResult
import com.example.domain.GenerationType
import com.example.domain.PolicyIssue
import com.example.domain.RiskLevel
import com.example.domain.SafetyScanReport
import com.example.ui.theme.DangerRed
import com.example.ui.theme.FiverrDarkGreen
import com.example.ui.theme.FiverrGreen
import com.example.ui.theme.FiverrMintLight
import com.example.ui.theme.PurplePro
import com.example.ui.theme.WarningOrange

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiAndSafetyScreen(
    aiType: GenerationType,
    aiSkill: String,
    aiDetails: String,
    aiTone: String,
    onSetAiType: (GenerationType) -> Unit,
    onUpdateAiInputs: (String, String, String) -> Unit,
    onRunGeneration: () -> Unit,
    isGenerating: Boolean,
    aiResult: GenerationResult?,
    safetyText: String,
    onUpdateSafetyText: (String) -> Unit,
    safetyReport: SafetyScanReport,
    modifier: Modifier = Modifier
) {
    var activeSubTab by remember { mutableIntStateOf(0) } // 0: AI Generator, 1: TOS Safety Scanner
    val context = LocalContext.current

    Column(modifier = modifier.fillMaxSize()) {
        SecondaryTabRow(
            selectedTabIndex = activeSubTab,
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Tab(
                selected = activeSubTab == 0,
                onClick = { activeSubTab = 0 },
                text = { Text("AI Proposal & Gig Writer", fontWeight = FontWeight.SemiBold) },
                icon = { Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp)) },
                modifier = Modifier.testTag("tab_ai_writer")
            )
            Tab(
                selected = activeSubTab == 1,
                onClick = { activeSubTab = 1 },
                text = { Text("TOS Message Safety", fontWeight = FontWeight.SemiBold) },
                icon = { Icon(imageVector = Icons.Default.Security, contentDescription = null, modifier = Modifier.size(16.dp)) },
                modifier = Modifier.testTag("tab_safety_scanner")
            )
        }

        if (activeSubTab == 0) {
            // AI Generator Tab
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Select Generation Tool", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(6.dp))

                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(GenerationType.values()) { type ->
                            FilterChip(
                                selected = aiType == type,
                                onClick = { onSetAiType(type) },
                                label = { Text(type.title, fontSize = 12.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = FiverrDarkGreen,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }
                }

                item {
                    OutlinedTextField(
                        value = aiSkill,
                        onValueChange = { onUpdateAiInputs(it, aiDetails, aiTone) },
                        label = { Text("Your Skill / Gig Niche") },
                        placeholder = { Text("e.g. Logo Design, Shopify Store, Python Automation") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("ai_skill_input")
                    )
                }

                item {
                    OutlinedTextField(
                        value = aiDetails,
                        onValueChange = { onUpdateAiInputs(aiSkill, it, aiTone) },
                        label = { Text("Client Brief / Specific Requirements") },
                        placeholder = { Text("Enter client pain points, package details, or specific requests") },
                        minLines = 3,
                        modifier = Modifier.fillMaxWidth().testTag("ai_details_input")
                    )
                }

                item {
                    // Tone Selector
                    val tones = listOf("Professional & Persuasive", "Friendly & Warm", "Concise & Fast", "High Ticket Expert")
                    Text("Response Tone", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(tones) { tone ->
                            FilterChip(
                                selected = aiTone == tone,
                                onClick = { onUpdateAiInputs(aiSkill, aiDetails, tone) },
                                label = { Text(tone, fontSize = 11.sp) }
                            )
                        }
                    }
                }

                item {
                    Button(
                        onClick = onRunGeneration,
                        enabled = !isGenerating && aiSkill.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = FiverrDarkGreen),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("run_ai_btn")
                    ) {
                        if (isGenerating) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Crafting High-Converting Copy...")
                        } else {
                            Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Generate ${aiType.title}")
                        }
                    }
                }

                // AI Result Card
                if (aiResult != null) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth().testTag("ai_result_card"),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = if (aiResult.isAiPowered) PurplePro.copy(alpha = 0.15f) else FiverrMintLight
                                        ) {
                                            Text(
                                                text = if (aiResult.isAiPowered) "Gemini 3.5 AI" else "Smart Freelance Engine",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (aiResult.isAiPowered) PurplePro else FiverrDarkGreen,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                            )
                                        }
                                    }

                                    Button(
                                        onClick = {
                                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                            clipboard.setPrimaryClip(ClipData.newPlainText("Fiverr Copy", aiResult.generatedText))
                                            Toast.makeText(context, "Copied to clipboard!", Toast.LENGTH_SHORT).show()
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = FiverrDarkGreen),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.height(34.dp).testTag("copy_ai_result_btn")
                                    ) {
                                        Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(13.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Copy", fontSize = 11.sp)
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                ) {
                                    Text(
                                        text = aiResult.generatedText,
                                        fontSize = 13.sp,
                                        lineHeight = 19.sp,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.padding(12.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        } else {
            // TOS Safety Scanner Tab
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(6.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "Fiverr Terms of Service Safety Checker",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "Never get banned or warned! Paste your draft message below to detect prohibited words (WhatsApp, Skype, personal emails, off-platform payments, or review manipulation).",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                item {
                    OutlinedTextField(
                        value = safetyText,
                        onValueChange = onUpdateSafetyText,
                        label = { Text("Draft Message / Client Inbox Text") },
                        minLines = 4,
                        maxLines = 8,
                        modifier = Modifier.fillMaxWidth().testTag("safety_text_input")
                    )
                }

                // Safety Status Banner
                item {
                    val bannerBg = when (safetyReport.riskLevel) {
                        RiskLevel.DANGER -> DangerRed.copy(alpha = 0.15f)
                        RiskLevel.CAUTION -> WarningOrange.copy(alpha = 0.15f)
                        RiskLevel.SAFE -> FiverrMintLight
                    }
                    val bannerTextColor = when (safetyReport.riskLevel) {
                        RiskLevel.DANGER -> DangerRed
                        RiskLevel.CAUTION -> WarningOrange
                        RiskLevel.SAFE -> FiverrDarkGreen
                    }
                    val bannerIcon = when (safetyReport.riskLevel) {
                        RiskLevel.DANGER -> Icons.Default.Error
                        RiskLevel.CAUTION -> Icons.Default.Warning
                        RiskLevel.SAFE -> Icons.Default.CheckCircle
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = bannerBg,
                        modifier = Modifier.fillMaxWidth().testTag("safety_status_banner")
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(imageVector = bannerIcon, contentDescription = null, tint = bannerTextColor, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = safetyReport.summary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = bannerTextColor
                                )
                            }
                        }
                    }
                }

                // Detected Issues List
                if (safetyReport.detectedIssues.isNotEmpty()) {
                    item {
                        Text("Detected Violations & Safe Alternatives", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }

                    items(safetyReport.detectedIssues) { issue ->
                        PolicyIssueCard(issue = issue)
                    }

                    item {
                        Button(
                            onClick = {
                                onUpdateSafetyText(safetyReport.sanitizedSuggestion)
                                Toast.makeText(context, "Applied safe alternatives!", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = FiverrDarkGreen),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Apply Safe Rephrasing")
                        }
                    }
                }

                item {
                    // Quick prohibited cheat-sheet
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text("Common Fiverr Ban Triggers to Avoid:", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("• Never say: WhatsApp, Skype, Telegram, Phone, Call, Discord", fontSize = 12.sp, color = DangerRed)
                            Text("• Never say: PayPal, Pay outside, Crypto, Bank transfer outside Fiverr", fontSize = 12.sp, color = DangerRed)
                            Text("• Never say: 'Leave 5 stars' or 'Give positive rating' (Only ask for 'honest feedback')", fontSize = 12.sp, color = WarningOrange)
                            Text("• Only share contact details when mandatory for the gig and declared on the gig page.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }
}

@Composable
fun PolicyIssueCard(issue: PolicyIssue) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = DangerRed.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "Trigger: \"${issue.keyword}\"",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = DangerRed,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                Text(
                    text = issue.category,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(text = issue.warning, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Safe Alternative: ${issue.safeAlternative}",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = FiverrDarkGreen
            )
        }
    }
}
