package com.example.domain

import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

enum class GenerationType(val title: String) {
    GIG_DESCRIPTION("Gig Description & Tags"),
    PROPOSAL_PITCH("Custom Offer Proposal"),
    REVISION_REPLY("Revision Response"),
    REVIEW_RESPONSE("Client Review Reply")
}

data class GenerationResult(
    val generatedText: String,
    val isAiPowered: Boolean,
    val error: String? = null
)

object AiGeneratorEngine {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    suspend fun generate(
        type: GenerationType,
        skillOrNiche: String,
        details: String,
        tone: String = "Professional & Persuasive"
    ): GenerationResult = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Throwable) {
            ""
        }

        val prompt = buildPrompt(type, skillOrNiche, details, tone)

        // If Gemini API key is configured and not default placeholder
        if (apiKey.isNotBlank() && apiKey != "MY_GEMINI_API_KEY") {
            try {
                val responseText = callGeminiApi(apiKey, prompt)
                if (responseText.isNotBlank()) {
                    return@withContext GenerationResult(
                        generatedText = responseText,
                        isAiPowered = true
                    )
                }
            } catch (e: Exception) {
                // Fallback to high-converting smart template engine on network/key issue
            }
        }

        // Offline Smart Template Engine
        val smartText = generateSmartOfflineTemplate(type, skillOrNiche, details, tone)
        GenerationResult(
            generatedText = smartText,
            isAiPowered = false
        )
    }

    private fun buildPrompt(
        type: GenerationType,
        skill: String,
        details: String,
        tone: String
    ): String {
        return when (type) {
            GenerationType.GIG_DESCRIPTION -> """
                You are a Top-Rated Fiverr seller coach and copywriter.
                Write an SEO-optimized Fiverr Gig Description and 5 Search Tags for the service: "$skill".
                Additional details: "$details".
                Tone: $tone.
                Format clearly with:
                - Catchy Fiverr Gig Title (starting with "I will", 50-70 characters)
                - High-converting Gig Description (hook, why choose me, packages overview, what is included, CTA)
                - 5 Best Search Tags (comma separated)
                - 2 Gig FAQs with answers
                Strictly comply with Fiverr Terms of Service (no off-platform contact mentions).
            """.trimIndent()

            GenerationType.PROPOSAL_PITCH -> """
                You are a freelance sales specialist on Fiverr.
                Write a winning, concise custom offer pitch / inbox reply to a client requesting: "$details".
                Seller Skill / Service: "$skill".
                Tone: $tone.
                Include:
                - Friendly, professional opening
                - Proof of understanding client pain points
                - Clear scope, milestone breakdown & estimated turnaround
                - Reassurance of revisions until 100% satisfied
                - Low-friction Call-To-Action to get started
            """.trimIndent()

            GenerationType.REVISION_REPLY -> """
                Write a professional, calm, and reassuring Fiverr message responding to a client who requested revisions: "$details".
                Service: "$skill". Tone: $tone.
                Acknowledge feedback, explain the exact adjustments you'll make, give expected ETA, and reassure them of your commitment to excellence.
            """.trimIndent()

            GenerationType.REVIEW_RESPONSE -> """
                Write a warm, gracious, and professional Fiverr public review response to a client.
                Client context / rating details: "$details".
                Service provided: "$skill". Tone: $tone.
                Thank them, highlight how pleasant it was collaborating, and invite them back for future projects.
            """.trimIndent()
        }
    }

    private fun callGeminiApi(apiKey: String, prompt: String): String {
        val endpoint = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"
        val jsonBody = JSONObject().apply {
            val contents = JSONArray().apply {
                put(JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().put("text", prompt))
                    })
                })
            }
            put("contents", contents)
        }

        val request = Request.Builder()
            .url(endpoint)
            .post(jsonBody.toString().toRequestBody("application/json".toMediaType()))
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) return ""
            val bodyString = response.body?.string() ?: return ""
            val json = JSONObject(bodyString)
            val candidates = json.optJSONArray("candidates") ?: return ""
            if (candidates.length() == 0) return ""
            val firstCandidate = candidates.getJSONObject(0)
            val content = firstCandidate.optJSONObject("content") ?: return ""
            val parts = content.optJSONArray("parts") ?: return ""
            if (parts.length() == 0) return ""
            return parts.getJSONObject(0).optString("text", "")
        }
    }

    private fun generateSmartOfflineTemplate(
        type: GenerationType,
        skill: String,
        details: String,
        tone: String
    ): String {
        val niche = if (skill.isBlank()) "Professional Services" else skill
        val context = if (details.isBlank()) "tailored to your exact business goals" else details

        return when (type) {
            GenerationType.GIG_DESCRIPTION -> """
★ RECOMMENDED GIG TITLE:
I will deliver modern, high quality $niche for your brand

★ SEARCH TAGS (5/5 for maximum Fiverr SEO):
${niche.lowercase().replace(" ", "_")}, ${niche.lowercase()}, professional, modern, fast_delivery

★ GIG DESCRIPTION:
Are you looking for top-tier $niche that stands out and drives real results? You have come to the right place!

I specialize in providing high-impact $niche $context. With a relentless focus on quality, clear communication, and timely delivery, I ensure your project succeeds from day one.

✦ WHAT YOU WILL GET:
• High quality deliverables customized to your requirements
• All necessary source & export files included
• Fast turnaround with active communication
• 100% satisfaction guarantee with dedicated revisions

✦ WHY WORK WITH ME?
✔ Dedicated full-time Fiverr specialist
✔ Fast response time & VIP customer support
✔ Clean, modern, and professional standards

✦ PACKAGES OVERVIEW:
• Basic: Essential starter setup with rapid 2-day delivery
• Standard (Most Popular): Complete solution with full assets & priority revisions
• Premium VIP: Complete all-inclusive package with source files & VIP turnaround

Ready to take your project to the next level?
👉 Click "Continue" to select your package, or message me right now to discuss custom requirements!
            """.trimIndent()

            GenerationType.PROPOSAL_PITCH -> """
Hi there!

Thank you for detailing your project. I would love to help you with $niche!

Having reviewed your requirements regarding $context, here is how I will approach your delivery:
1. Thorough analysis of your vision and immediate kickoff
2. Delivering a solid first draft well ahead of deadline for your review
3. Refining any details until it matches your exact expectations

I can get started right away and deliver the complete solution with top quality.

Feel free to accept the attached offer or send me a quick message if you have any questions before starting! Looking forward to delivering great results for you.
            """.trimIndent()

            GenerationType.REVISION_REPLY -> """
Hi there,

Thank you for reviewing the delivery and sharing your constructive feedback!

I completely understand your requested adjustments regarding:
$context

I am on it right now and will refine the work to align with your expectations. You will receive the updated revision within a few hours.

Your satisfaction is my top priority—thank you for your patience and collaboration!
            """.trimIndent()

            GenerationType.REVIEW_RESPONSE -> """
Thank you so much for the fantastic feedback!

It was an absolute pleasure working with you on your $niche project. Your clear instructions and prompt communication made this collaboration smooth and enjoyable.

Wishing you tremendous success, and I look forward to working together again on your future projects!
            """.trimIndent()
        }
    }
}
