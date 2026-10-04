package com.example.domain

import java.text.DecimalFormat

data class FeeResult(
    val grossAmount: Double,
    val tipAmount: Double,
    val totalGross: Double,
    val fiverrSellerFee: Double,
    val netSellerEarnings: Double,
    val buyerFee: Double,
    val totalBuyerPays: Double,
    val effectiveTakeHomePercent: Double
)

data class WithdrawalEstimate(
    val method: String,
    val feeDescription: String,
    val estimatedNetUsd: Double,
    val convertedAmount: Double,
    val currencyCode: String,
    val currencySymbol: String
)

object FeeCalculator {
    private val df = DecimalFormat("#,##0.00")

    fun calculate(grossAmount: Double, tipAmount: Double = 0.0): FeeResult {
        val totalGross = grossAmount + tipAmount
        val fiverrFee = totalGross * 0.20
        val netEarnings = totalGross - fiverrFee

        // Fiverr buyer fee policy:
        // $3.00 on orders up to $75, 5.5% on orders above $75
        val buyerFee = if (grossAmount <= 75.0) 3.00 else grossAmount * 0.055
        val totalBuyerPays = grossAmount + buyerFee + tipAmount

        val effectivePercent = if (totalGross > 0) (netEarnings / totalGross) * 100.0 else 80.0

        return FeeResult(
            grossAmount = grossAmount,
            tipAmount = tipAmount,
            totalGross = totalGross,
            fiverrSellerFee = fiverrFee,
            netSellerEarnings = netEarnings,
            buyerFee = buyerFee,
            totalBuyerPays = totalBuyerPays,
            effectiveTakeHomePercent = effectivePercent
        )
    }

    val currencyRates = listOf(
        Currency("USD", "$", 1.0, "US Dollar"),
        Currency("EUR", "€", 0.92, "Euro"),
        Currency("GBP", "£", 0.78, "British Pound"),
        Currency("CAD", "CA$", 1.36, "Canadian Dollar"),
        Currency("AUD", "AU$", 1.52, "Australian Dollar"),
        Currency("PKR", "Rs ", 278.5, "Pakistani Rupee"),
        Currency("INR", "₹", 83.5, "Indian Rupee"),
        Currency("BDT", "৳", 117.5, "Bangladeshi Taka"),
        Currency("PHP", "₱", 57.5, "Philippine Peso"),
        Currency("NGN", "₦", 1540.0, "Nigerian Naira")
    )

    fun getWithdrawalEstimates(netUsd: Double, selectedCurrency: Currency): List<WithdrawalEstimate> {
        val rate = selectedCurrency.rate

        // Payoneer / Bank Transfer: Flat ~$3 fee or 2% conversion
        val bankNetUsd = (netUsd - 3.0).coerceAtLeast(0.0)
        val paypalNetUsd = (netUsd * 0.98).coerceAtLeast(0.0) // ~2% transaction/currency spread
        val directDepositNetUsd = (netUsd - 1.0).coerceAtLeast(0.0) // US Direct Deposit $1

        return listOf(
            WithdrawalEstimate(
                method = "Bank Transfer (Payoneer)",
                feeDescription = "$3 flat fee + standard exchange rate",
                estimatedNetUsd = bankNetUsd,
                convertedAmount = bankNetUsd * rate,
                currencyCode = selectedCurrency.code,
                currencySymbol = selectedCurrency.symbol
            ),
            WithdrawalEstimate(
                method = "PayPal",
                feeDescription = "Free transfer to PayPal (~2% local conversion)",
                estimatedNetUsd = paypalNetUsd,
                convertedAmount = paypalNetUsd * rate,
                currencyCode = selectedCurrency.code,
                currencySymbol = selectedCurrency.symbol
            ),
            WithdrawalEstimate(
                method = "Direct Deposit (Fiverr Card / ACH)",
                feeDescription = "$1 flat fee per withdrawal (US Accounts)",
                estimatedNetUsd = directDepositNetUsd,
                convertedAmount = directDepositNetUsd * rate,
                currencyCode = selectedCurrency.code,
                currencySymbol = selectedCurrency.symbol
            )
        )
    }

    fun calculateGoal(targetNetMonthly: Double): GoalBreakdown {
        val grossNeeded = targetNetMonthly / 0.80
        val fiverrCut = grossNeeded * 0.20

        return GoalBreakdown(
            targetNet = targetNetMonthly,
            grossNeeded = grossNeeded,
            fiverrFeeTotal = fiverrCut,
            ordersAt25 = kotlin.math.ceil(grossNeeded / 25.0).toInt(),
            ordersAt50 = kotlin.math.ceil(grossNeeded / 50.0).toInt(),
            ordersAt100 = kotlin.math.ceil(grossNeeded / 100.0).toInt(),
            ordersAt250 = kotlin.math.ceil(grossNeeded / 250.0).toInt()
        )
    }

    fun calculateReverseQuote(targetNetTakeHome: Double): ReverseQuoteResult {
        val requiredGrossQuote = if (targetNetTakeHome > 0) targetNetTakeHome / 0.80 else 0.0
        val fiverrCommission = requiredGrossQuote * 0.20
        return ReverseQuoteResult(
            targetNetTakeHome = targetNetTakeHome,
            requiredGrossQuote = requiredGrossQuote,
            fiverrCommission = fiverrCommission
        )
    }

    fun formatMoney(amount: Double, symbol: String = "$"): String {
        return "$symbol${df.format(amount)}"
    }
}

data class Currency(
    val code: String,
    val symbol: String,
    val rate: Double,
    val name: String
)

data class GoalBreakdown(
    val targetNet: Double,
    val grossNeeded: Double,
    val fiverrFeeTotal: Double,
    val ordersAt25: Int,
    val ordersAt50: Int,
    val ordersAt100: Int,
    val ordersAt250: Int
)

data class ReverseQuoteResult(
    val targetNetTakeHome: Double,
    val requiredGrossQuote: Double,
    val fiverrCommission: Double
)
