package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.Currency
import com.example.domain.FeeCalculator
import com.example.domain.FeeResult
import com.example.domain.GoalBreakdown
import com.example.domain.ReverseQuoteResult
import com.example.domain.WithdrawalEstimate
import com.example.ui.theme.DangerRed
import com.example.ui.theme.FiverrDarkGreen
import com.example.ui.theme.FiverrForest
import com.example.ui.theme.FiverrGreen
import com.example.ui.theme.FiverrMintLight
import com.example.ui.theme.GoldStar
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate700
import com.example.ui.theme.WarningOrange

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeeCalculatorScreen(
    calculatorMode: Int,
    onModeChange: (Int) -> Unit,
    grossAmount: String,
    tipAmount: String,
    onGrossChange: (String) -> Unit,
    onAdjustGross: (Double) -> Unit,
    onTipChange: (String) -> Unit,
    reverseNet: String,
    onReverseNetChange: (String) -> Unit,
    reverseQuoteResult: ReverseQuoteResult,
    feeResult: FeeResult,
    selectedCurrency: Currency,
    onSelectCurrency: (Currency) -> Unit,
    withdrawalEstimates: List<WithdrawalEstimate>,
    targetGoal: String,
    onTargetGoalChange: (String) -> Unit,
    goalBreakdown: GoalBreakdown,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val presets = listOf("15", "30", "50", "100", "250", "500", "1000")
    var currencyDropdownExpanded by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(6.dp))
            // Mode Switcher Tabs
            SecondaryTabRow(
                selectedTabIndex = calculatorMode,
                containerColor = MaterialTheme.colorScheme.surface,
                modifier = Modifier.clip(RoundedCornerShape(12.dp))
            ) {
                Tab(
                    selected = calculatorMode == 0,
                    onClick = { onModeChange(0) },
                    text = { Text("Gross → Net", fontWeight = FontWeight.SemiBold, fontSize = 11.sp, maxLines = 1) },
                    icon = { Icon(Icons.Default.Calculate, contentDescription = null, modifier = Modifier.size(15.dp)) },
                    modifier = Modifier.testTag("tab_gross_to_net")
                )
                Tab(
                    selected = calculatorMode == 1,
                    onClick = { onModeChange(1) },
                    text = { Text("Target → Quote", fontWeight = FontWeight.SemiBold, fontSize = 11.sp, maxLines = 1) },
                    icon = { Icon(Icons.Default.SwapHoriz, contentDescription = null, modifier = Modifier.size(15.dp)) },
                    modifier = Modifier.testTag("tab_target_to_quote")
                )
            }
        }

        if (calculatorMode == 0) {
            // MODE 0: GROSS PROJECT AMOUNT TO NET EARNINGS
            item {
                // Main Highlight Net Earnings Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("fee_card_result"),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "YOU TAKE HOME (80% NET)",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                )
                                Text(
                                    text = "$${String.format("%.2f", feeResult.netSellerEarnings)}",
                                    fontSize = 34.sp,
                                    fontWeight = FontWeight.Black,
                                    color = FiverrDarkGreen
                                )
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(FiverrDarkGreen)
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "Fiverr Cut: 20%",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "-$${String.format("%.2f", feeResult.fiverrSellerFee)}",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DangerRed
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Visual Proportional Bar (80% Take Home vs 20% Fiverr Commission)
                        Column {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(12.dp)
                                    .clip(RoundedCornerShape(6.dp))
                            ) {
                                Box(
                                    modifier = Modifier
                                        .weight(0.80f)
                                        .fillMaxSize()
                                        .background(FiverrDarkGreen)
                                )
                                Box(
                                    modifier = Modifier
                                        .weight(0.20f)
                                        .fillMaxSize()
                                        .background(DangerRed.copy(alpha = 0.85f))
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "● You Keep (80%): $${String.format("%.0f", feeResult.netSellerEarnings)}",
                                    fontSize = 11.sp,
                                    color = FiverrForest,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1
                                )
                                Text(
                                    text = "● Fiverr 20%: -$${String.format("%.0f", feeResult.fiverrSellerFee)}",
                                    fontSize = 11.sp,
                                    color = DangerRed,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))
                        Spacer(modifier = Modifier.height(10.dp))

                        // Quick Copy Breakdown Button
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Gross: $${String.format("%.2f", feeResult.totalGross)}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )

                            OutlinedButton(
                                onClick = {
                                    val summary = "Fiverr Order Breakdown:\nGross: $${String.format("%.2f", feeResult.grossAmount)}\nFiverr Fee (20%): -$${String.format("%.2f", feeResult.fiverrSellerFee)}\nNet Freelancer Earnings: $${String.format("%.2f", feeResult.netSellerEarnings)}"
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    clipboard.setPrimaryClip(ClipData.newPlainText("Fiverr Fee Breakdown", summary))
                                    Toast.makeText(context, "Fee breakdown copied!", Toast.LENGTH_SHORT).show()
                                },
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.height(32.dp).testTag("btn_copy_fee_breakdown")
                            ) {
                                Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(13.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Copy Breakdown", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }

            // Input Fields: Gross Project Amount & Tip
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Gross Project Amount",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        // Gross Input with Stepper buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = grossAmount,
                                onValueChange = onGrossChange,
                                label = { Text("Project Price ($)") },
                                prefix = { Text("$", fontWeight = FontWeight.Bold) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("calc_gross_input")
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            IconButton(
                                onClick = { onAdjustGross(-10.0) },
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                Icon(Icons.Default.Remove, contentDescription = "Decrease 10")
                            }

                            Spacer(modifier = Modifier.width(6.dp))

                            IconButton(
                                onClick = { onAdjustGross(10.0) },
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(FiverrMintLight)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Increase 10", tint = FiverrDarkGreen)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Presets
                        Text("Quick Project Presets:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(4.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(presets) { p ->
                                FilterChip(
                                    selected = grossAmount == p,
                                    onClick = { onGrossChange(p) },
                                    label = { Text("$$p", fontSize = 12.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = FiverrDarkGreen,
                                        selectedLabelColor = Color.White
                                    ),
                                    modifier = Modifier.testTag("preset_$p")
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
                        Spacer(modifier = Modifier.height(12.dp))

                        // Optional Tip Input
                        OutlinedTextField(
                            value = tipAmount,
                            onValueChange = onTipChange,
                            label = { Text("Optional Client Tip ($)") },
                            prefix = { Text("$", fontWeight = FontWeight.Bold) },
                            supportingText = {
                                Text("Fiverr also takes 20% on tips. You keep 80% of any tip.")
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("calc_tip_input")
                        )
                    }
                }
            }

            // Buyer Total Checkout Cost Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CreditCard, contentDescription = null, tint = FiverrDarkGreen, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "What the Buyer Pays at Checkout",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Project Base Price:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("$${String.format("%.2f", feeResult.grossAmount)}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Fiverr Buyer Service Fee:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("+$${String.format("%.2f", feeResult.buyerFee)}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }

                        if (feeResult.tipAmount > 0) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Client Tip Added:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("+$${String.format("%.2f", feeResult.tipAmount)}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Total Buyer Checkout:", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text(
                                text = "$${String.format("%.2f", feeResult.totalBuyerPays)}",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        } else {
            // MODE 1: REVERSE QUOTE CALCULATOR (Target Net -> Required Gross Quote)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "REQUIRED GROSS QUOTE TO CLIENT",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                        Text(
                            text = "$${String.format("%.2f", reverseQuoteResult.requiredGrossQuote)}",
                            fontSize = 34.sp,
                            fontWeight = FontWeight.Black,
                            color = FiverrDarkGreen
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "To keep exactly $${String.format("%.2f", reverseQuoteResult.targetNetTakeHome)} in your pocket after Fiverr's 20% cut (-$${String.format("%.2f", reverseQuoteResult.fiverrCommission)}).",
                            fontSize = 12.sp,
                            lineHeight = 17.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Desired Net Earnings (In Your Pocket)", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = reverseNet,
                            onValueChange = onReverseNetChange,
                            label = { Text("Desired Net Take-Home ($)") },
                            prefix = { Text("$", fontWeight = FontWeight.Bold) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("reverse_net_input")
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        val reversePresets = listOf("50", "100", "200", "500", "1000")
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(reversePresets) { p ->
                                FilterChip(
                                    selected = reverseNet == p,
                                    onClick = { onReverseNetChange(p) },
                                    label = { Text("$$p", fontSize = 12.sp) }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("Formula: Gross Quote = Target Net ÷ 0.80", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "$${String.format("%.2f", reverseQuoteResult.targetNetTakeHome)} ÷ 0.80 = $${String.format("%.2f", reverseQuoteResult.requiredGrossQuote)}",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }

        // Withdrawal & Currency Conversion Estimator
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Payout & Currency Converter",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                            Text(
                                text = "Select your local withdrawal currency",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        // Currency Selector Dropdown
                        ExposedDropdownMenuBox(
                            expanded = currencyDropdownExpanded,
                            onExpandedChange = { currencyDropdownExpanded = !currencyDropdownExpanded }
                        ) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier
                                    .menuAnchor(MenuAnchorType.PrimaryNotEditable, true)
                                    .clickable { currencyDropdownExpanded = true }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                                    .testTag("currency_selector")
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "${selectedCurrency.code} (${selectedCurrency.symbol})",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = currencyDropdownExpanded)
                                }
                            }

                            ExposedDropdownMenu(
                                expanded = currencyDropdownExpanded,
                                onDismissRequest = { currencyDropdownExpanded = false }
                            ) {
                                FeeCalculator.currencyRates.forEach { currency ->
                                    DropdownMenuItem(
                                        text = { Text("${currency.code} - ${currency.name} (${currency.symbol})") },
                                        onClick = {
                                            onSelectCurrency(currency)
                                            currencyDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    withdrawalEstimates.forEach { estimate ->
                        WithdrawalRow(estimate = estimate)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }

        // Monthly Income Target Calculator
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.TrendingUp, contentDescription = null, tint = FiverrDarkGreen)
                        Text(
                            text = "Monthly Freelance Goal Planner",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = targetGoal,
                        onValueChange = onTargetGoalChange,
                        label = { Text("Desired Net Take-Home ($ / month)") },
                        prefix = { Text("$", fontWeight = FontWeight.Bold) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("goal_input")
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "To pocket $${String.format("%.0f", goalBreakdown.targetNet)}/month after Fiverr's 20% cut:",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "• Total Gross Orders Needed: $${String.format("%.2f", goalBreakdown.grossNeeded)}",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "• Fiverr Service Fees (20%): $${String.format("%.2f", goalBreakdown.fiverrFeeTotal)}",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Volume milestones needed:",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.sp
                            )
                            Text("• ${goalBreakdown.ordersAt50} orders at $50/order", fontSize = 12.sp)
                            Text("• ${goalBreakdown.ordersAt100} orders at $100/order", fontSize = 12.sp)
                            Text("• ${goalBreakdown.ordersAt250} orders at $250/order", fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun WithdrawalRow(estimate: WithdrawalEstimate) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = estimate.method,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
                Text(
                    text = estimate.feeDescription,
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${estimate.currencySymbol}${String.format("%,.0f", estimate.convertedAmount)}",
                    fontWeight = FontWeight.Black,
                    fontSize = 15.sp,
                    color = FiverrDarkGreen
                )
                Text(
                    text = "≈ $${String.format("%.2f", estimate.estimatedNetUsd)} USD",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

