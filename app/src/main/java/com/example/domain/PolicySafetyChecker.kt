package com.example.domain

enum class RiskLevel {
    SAFE,
    CAUTION,
    DANGER
}

data class PolicyIssue(
    val keyword: String,
    val category: String, // "Off-Platform Contact", "External Payment", "Review Manipulation"
    val risk: RiskLevel,
    val warning: String,
    val safeAlternative: String
)

data class SafetyScanReport(
    val riskLevel: RiskLevel,
    val summary: String,
    val detectedIssues: List<PolicyIssue>,
    val sanitizedSuggestion: String
)

object PolicySafetyChecker {

    private val rules = listOf(
        PolicyIssue(
            keyword = "whatsapp",
            category = "Off-Platform Contact",
            risk = RiskLevel.DANGER,
            warning = "Fiverr strictly bans sharing WhatsApp numbers or asking buyers to message on WhatsApp.",
            safeAlternative = "Keep all communication directly inside the Fiverr chat."
        ),
        PolicyIssue(
            keyword = "skype",
            category = "Off-Platform Contact",
            risk = RiskLevel.DANGER,
            warning = "Sharing Skype ID triggers immediate automated Fiverr warnings.",
            safeAlternative = "Use Fiverr's official built-in Zoom/Video call feature if enabled on your gig."
        ),
        PolicyIssue(
            keyword = "telegram",
            category = "Off-Platform Contact",
            risk = RiskLevel.DANGER,
            warning = "Telegram is the #1 flagged app on Fiverr for phishing and off-platform fraud.",
            safeAlternative = "Share all project files and updates directly through Fiverr order attachments."
        ),
        PolicyIssue(
            keyword = "email",
            category = "Off-Platform Contact",
            risk = RiskLevel.DANGER,
            warning = "Asking for or providing personal emails is prohibited unless strictly required for gig delivery (e.g. analytics setup) and declared on the gig page.",
            safeAlternative = "Request necessary credentials via the official Fiverr Order Requirements page."
        ),
        PolicyIssue(
            keyword = "@gmail",
            category = "Off-Platform Contact",
            risk = RiskLevel.DANGER,
            warning = "Email addresses in messages trigger automated bot flags on Fiverr.",
            safeAlternative = "Deliver your deliverables directly in the order delivery zip/file upload."
        ),
        PolicyIssue(
            keyword = "paypal",
            category = "External Payment",
            risk = RiskLevel.DANGER,
            warning = "Mentioning PayPal or paying outside Fiverr is grounds for immediate account termination.",
            safeAlternative = "Direct the client to purchase through a Fiverr Custom Offer."
        ),
        PolicyIssue(
            keyword = "pay outside",
            category = "External Payment",
            risk = RiskLevel.DANGER,
            warning = "Requesting or accepting off-platform payments violates Fiverr's core Terms of Service.",
            safeAlternative = "Send a Custom Offer directly through the inbox."
        ),
        PolicyIssue(
            keyword = "crypto",
            category = "External Payment",
            risk = RiskLevel.DANGER,
            warning = "Crypto payments are prohibited on Fiverr.",
            safeAlternative = "Accept payments exclusively through Fiverr checkout."
        ),
        PolicyIssue(
            keyword = "phone number",
            category = "Off-Platform Contact",
            risk = RiskLevel.DANGER,
            warning = "Sharing personal phone numbers can get your seller account restricted.",
            safeAlternative = "Communicate exclusively via Fiverr messaging."
        ),
        PolicyIssue(
            keyword = "5 star",
            category = "Review Manipulation",
            risk = RiskLevel.DANGER,
            warning = "Fiverr policy strictly prohibits asking clients for '5-star' ratings or 'positive feedback'.",
            safeAlternative = "Use neutral phrasing: 'Your honest feedback and review are greatly appreciated!'"
        ),
        PolicyIssue(
            keyword = "5-star",
            category = "Review Manipulation",
            risk = RiskLevel.DANGER,
            warning = "Explicitly asking for 5 stars counts as feedback manipulation.",
            safeAlternative = "Ask for 'your honest experience' or 'feedback'."
        ),
        PolicyIssue(
            keyword = "positive review",
            category = "Review Manipulation",
            risk = RiskLevel.CAUTION,
            warning = "Conditioning work or nudging specifically for 'positive' feedback can be flagged.",
            safeAlternative = "Ask if they are satisfied and welcome their honest review."
        ),
        PolicyIssue(
            keyword = "zoom",
            category = "Off-Platform Contact",
            risk = RiskLevel.CAUTION,
            warning = "Third-party Zoom links are prohibited. Only Fiverr's built-in Zoom integration is allowed.",
            safeAlternative = "Click 'Video Call' directly in the Fiverr inbox if your seller tier supports it."
        )
    )

    fun scan(text: String): SafetyScanReport {
        if (text.isBlank()) {
            return SafetyScanReport(
                riskLevel = RiskLevel.SAFE,
                summary = "Message is empty. Type or paste your message to scan.",
                detectedIssues = emptyList(),
                sanitizedSuggestion = text
            )
        }

        val lower = text.lowercase()
        val detected = mutableListOf<PolicyIssue>()

        for (rule in rules) {
            if (lower.contains(rule.keyword)) {
                detected.add(rule)
            }
        }

        val overallRisk = when {
            detected.any { it.risk == RiskLevel.DANGER } -> RiskLevel.DANGER
            detected.any { it.risk == RiskLevel.CAUTION } -> RiskLevel.CAUTION
            else -> RiskLevel.SAFE
        }

        val summary = when (overallRisk) {
            RiskLevel.DANGER -> "⚠️ HIGH RISK! Contains forbidden terms that may cause account warnings or bans."
            RiskLevel.CAUTION -> "⚡ CAUTION: Contains terms that may require specific context to avoid TOS flags."
            RiskLevel.SAFE -> "✅ SAFE: No prohibited Fiverr keywords detected. Message looks policy-compliant!"
        }

        var sanitized = text
        for (issue in detected) {
            if (issue.category == "Review Manipulation") {
                sanitized = sanitized.replace(
                    Regex("(?i)5\\s*-?\\s*stars?|positive review"),
                    "honest feedback"
                )
            }
        }

        return SafetyScanReport(
            riskLevel = overallRisk,
            summary = summary,
            detectedIssues = detected,
            sanitizedSuggestion = sanitized
        )
    }
}
