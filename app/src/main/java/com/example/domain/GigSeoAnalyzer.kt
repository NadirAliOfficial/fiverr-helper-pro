package com.example.domain

data class SeoAnalysisReport(
    val overallScore: Int, // 0 - 100
    val grade: String, // "A+", "A", "B", "C", "Needs Work"
    val titleScore: Int,
    val tagsScore: Int,
    val descriptionScore: Int,
    val titleTips: List<SeoTip>,
    val tagTips: List<SeoTip>,
    val descriptionTips: List<SeoTip>,
    val matchedKeywords: List<String>
)

data class SeoTip(
    val isPositive: Boolean,
    val message: String
)

object GigSeoAnalyzer {

    private val powerWords = setOf(
        "professional", "modern", "minimalist", "custom", "clean", "high quality",
        "expert", "creative", "responsive", "unique", "elegant", "eye catching",
        "fast", "premium", "sleek", "original", "luxury", "top rated"
    )

    fun analyze(
        title: String,
        tagsInput: String,
        description: String
    ): SeoAnalysisReport {
        var tScore = 0
        var tagScore = 0
        var descScore = 0

        val tTips = mutableListOf<SeoTip>()
        val tagListTips = mutableListOf<SeoTip>()
        val descTips = mutableListOf<SeoTip>()

        val trimmedTitle = title.trim()
        val titleLower = trimmedTitle.lowercase()

        // 1. Title Evaluation
        if (titleLower.startsWith("i will")) {
            tScore += 20
            tTips.add(SeoTip(true, "Begins with 'I will' correctly"))
        } else {
            tTips.add(SeoTip(false, "Fiverr gigs must start with 'I will'"))
        }

        val charCount = trimmedTitle.length
        when {
            charCount in 40..70 -> {
                tScore += 35
                tTips.add(SeoTip(true, "Optimal length ($charCount chars, Fiverr sweet spot is 40-70)"))
            }
            charCount in 25..39 -> {
                tScore += 25
                tTips.add(SeoTip(false, "A bit short ($charCount chars). Consider adding 1 high-intent keyword"))
            }
            charCount in 71..80 -> {
                tScore += 25
                tTips.add(SeoTip(false, "A bit long ($charCount chars). Aim for under 70 chars to prevent mobile cut-off"))
            }
            charCount > 80 -> {
                tScore += 10
                tTips.add(SeoTip(false, "Too long ($charCount chars). Fiverr may truncate search results"))
            }
            else -> {
                tScore += 5
                tTips.add(SeoTip(false, "Too short ($charCount chars). Add descriptive niche keywords"))
            }
        }

        // Power words check
        val foundPowerWords = powerWords.filter { titleLower.contains(it) }
        if (foundPowerWords.isNotEmpty()) {
            tScore += 25
            tTips.add(SeoTip(true, "Strong power words found: ${foundPowerWords.joinToString(", ")}"))
        } else {
            tTips.add(SeoTip(false, "Try adding a power word (e.g., 'modern', 'custom', 'clean', 'responsive')"))
        }

        // Repeated word check
        val words = titleLower.split("\\s+".toRegex()).filter { it.length > 3 }
        val duplicates = words.groupingBy { it }.eachCount().filter { it.value > 1 }
        if (duplicates.isEmpty()) {
            tScore += 20
            tTips.add(SeoTip(true, "No spammy keyword repetition in title"))
        } else {
            tScore += 5
            tTips.add(SeoTip(false, "Repeated words: ${duplicates.keys.joinToString(", ")}. Fiverr dislikes duplicate words"))
        }

        // 2. Tag Evaluation
        val rawTags = tagsInput.split(",").map { it.trim() }.filter { it.isNotEmpty() }
        val tagCount = rawTags.size

        when (tagCount) {
            5 -> {
                tagScore += 40
                tagListTips.add(SeoTip(true, "Used all 5 allowed Fiverr search tags! Maximum discoverability"))
            }
            3, 4 -> {
                tagScore += 25
                tagListTips.add(SeoTip(false, "Only $tagCount/5 tags used. Always utilize all 5 tags"))
            }
            1, 2 -> {
                tagScore += 15
                tagListTips.add(SeoTip(false, "Only $tagCount tag(s). Add more tags to appear in diverse searches"))
            }
            else -> {
                tagScore += 5
                tagListTips.add(SeoTip(false, "No search tags entered. Add 5 tags separated by commas"))
            }
        }

        // Match tags with title
        val matchedKeywords = mutableListOf<String>()
        var overlapCount = 0
        rawTags.forEach { tag ->
            val tagNorm = tag.lowercase()
            if (titleLower.contains(tagNorm)) {
                overlapCount++
                matchedKeywords.add(tag)
            }
        }

        if (overlapCount >= 2) {
            tagScore += 35
            tagListTips.add(SeoTip(true, "$overlapCount tags match keywords in your title! Boosts Fiverr search algorithm relevance"))
        } else if (overlapCount == 1) {
            tagScore += 20
            tagListTips.add(SeoTip(false, "1 tag matches title. Aim for 2-3 tags to align with primary title keywords"))
        } else if (rawTags.isNotEmpty()) {
            tagScore += 10
            tagListTips.add(SeoTip(false, "None of your tags match your title words. Align tags with title keywords for synergy"))
        }

        val overlyLongTags = rawTags.filter { it.length > 20 }
        if (overlyLongTags.isEmpty()) {
            tagScore += 25
            tagListTips.add(SeoTip(true, "All tag lengths comply with Fiverr 20-character limit"))
        } else {
            tagListTips.add(SeoTip(false, "Tags exceeding 20 chars: ${overlyLongTags.joinToString(", ")}"))
        }

        // 3. Description Evaluation
        val descWords = description.trim().split("\\s+".toRegex()).filter { it.isNotEmpty() }
        val wordCount = descWords.size

        when {
            wordCount in 120..350 -> {
                descScore += 40
                descTips.add(SeoTip(true, "Solid description length ($wordCount words, recommended 120-300)"))
            }
            wordCount in 60..119 -> {
                descScore += 25
                descTips.add(SeoTip(false, "Brief description ($wordCount words). Expand on your process and benefits"))
            }
            wordCount > 350 -> {
                descScore += 30
                descTips.add(SeoTip(false, "Long description ($wordCount words). Ensure it uses clear bullet points so buyers can scan easily"))
            }
            else -> {
                descScore += 10
                descTips.add(SeoTip(false, "Very short or empty description ($wordCount words). Detailed descriptions convert 3x more"))
            }
        }

        // Check for bullet points / structure
        val hasBullets = description.contains("•") || description.contains("- ") || description.contains("* ")
        if (hasBullets) {
            descScore += 30
            descTips.add(SeoTip(true, "Uses bulleted lists for easy buyer scanning"))
        } else {
            descTips.add(SeoTip(false, "Add bullet points for 'What you get' or 'Why choose me'"))
        }

        // Call to action check
        val lowerDesc = description.lowercase()
        val hasCta = lowerDesc.contains("order") || lowerDesc.contains("contact") || lowerDesc.contains("message") || lowerDesc.contains("reach out")
        if (hasCta) {
            descScore += 30
            descTips.add(SeoTip(true, "Includes clear Call-To-Action for the client"))
        } else {
            descTips.add(SeoTip(false, "Add a clear Call-To-Action (e.g., 'Contact me before ordering' or 'Order now to get started!')"))
        }

        val totalScore = ((tScore * 0.40) + (tagScore * 0.35) + (descScore * 0.25)).toInt().coerceIn(0, 100)

        val grade = when {
            totalScore >= 90 -> "A+"
            totalScore >= 80 -> "A"
            totalScore >= 70 -> "B"
            totalScore >= 55 -> "C"
            else -> "Needs Work"
        }

        return SeoAnalysisReport(
            overallScore = totalScore,
            grade = grade,
            titleScore = tScore.coerceIn(0, 100),
            tagsScore = tagScore.coerceIn(0, 100),
            descriptionScore = descScore.coerceIn(0, 100),
            titleTips = tTips,
            tagTips = tagListTips,
            descriptionTips = descTips,
            matchedKeywords = matchedKeywords
        )
    }
}
