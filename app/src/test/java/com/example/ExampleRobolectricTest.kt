package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.domain.FeeCalculator
import com.example.domain.GigSeoAnalyzer
import com.example.domain.PolicySafetyChecker
import com.example.domain.RiskLevel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Fiverr Helper", appName)
  }

  @Test
  fun `verify fiverr fee calculation`() {
    // For a $50 order + $10 tip = $60 gross
    // 20% Fiverr fee = $12
    // Net seller take-home = $48
    val result = FeeCalculator.calculate(50.0, 10.0)
    assertEquals(60.0, result.totalGross, 0.001)
    assertEquals(12.0, result.fiverrSellerFee, 0.001)
    assertEquals(48.0, result.netSellerEarnings, 0.001)
  }

  @Test
  fun `verify seo analyzer identifies short title`() {
    val report = GigSeoAnalyzer.analyze(
      title = "I will design logo",
      tagsInput = "logo, design",
      description = "Short desc"
    )
    assertTrue(report.titleScore > 0)
    assertTrue(report.titleTips.any { it.message.contains("short") || it.message.contains("Sweet spot") || it.message.contains("chars") })
  }

  @Test
  fun `verify policy safety checker flags prohibited contact`() {
    val report = PolicySafetyChecker.scan("Please message me on whatsapp or skype")
    assertEquals(RiskLevel.DANGER, report.riskLevel)
    assertTrue(report.detectedIssues.any { it.keyword == "whatsapp" })
    assertTrue(report.detectedIssues.any { it.keyword == "skype" })
  }

  @Test
  fun `verify reverse quote calculator for target net earnings`() {
    // To take home $100 net, seller must quote $125 (since $125 * 0.80 = $100)
    val reverse = FeeCalculator.calculateReverseQuote(100.0)
    assertEquals(125.0, reverse.requiredGrossQuote, 0.001)
    assertEquals(25.0, reverse.fiverrCommission, 0.001)
    assertEquals(100.0, reverse.targetNetTakeHome, 0.001)
  }

  @Test
  fun `verify fiverr order text parsing extracts buyer and amount`() {
    val sampleText = "New Fiverr Order #FO12345 from @sarah_growth for $140.00 Modern Brand Logo"
    val parsed = com.example.domain.FiverrSyncService.parseFiverrOrderText(sampleText)
    org.junit.Assert.assertNotNull(parsed)
    assertEquals("sarah_growth", parsed?.buyerUsername)
    assertEquals(140.0, parsed?.orderAmount ?: 0.0, 0.001)
  }

  @Test
  fun `verify real_nadirali sync returns trading bot gigs`() = kotlinx.coroutines.runBlocking {
    val result = com.example.domain.FiverrSyncService.syncFromUsernameOrUrl("real_nadirali")
    assertTrue(result.success)
    assertEquals("real_nadirali", result.username)
    assertEquals(5.0, result.rating, 0.001)
    assertTrue(result.activeGigs.any { it.title.contains("trading bot", ignoreCase = true) })
    // Verify no dummy orders are generated (real data only)
    assertTrue(result.detectedOrders.isEmpty())
  }
}
