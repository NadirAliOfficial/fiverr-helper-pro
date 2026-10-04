package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.model.ClientOrder
import com.example.data.model.Gig
import com.example.data.model.QuickReply
import com.example.data.repository.FiverrRepository
import com.example.domain.AiGeneratorEngine
import com.example.domain.Currency
import com.example.domain.FeeCalculator
import com.example.domain.FeeResult
import com.example.domain.GenerationResult
import com.example.domain.GenerationType
import com.example.domain.GigSeoAnalyzer
import com.example.domain.GoalBreakdown
import com.example.domain.PolicySafetyChecker
import com.example.domain.SafetyScanReport
import com.example.domain.SeoAnalysisReport
import com.example.domain.WithdrawalEstimate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FiverrViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: FiverrRepository

    init {
        val database = AppDatabase.getDatabase(application, viewModelScope)
        repository = FiverrRepository(
            gigDao = database.gigDao(),
            orderDao = database.orderDao(),
            quickReplyDao = database.quickReplyDao()
        )
    }

    // Navigation Tab (0: Dashboard/Orders, 1: Gigs & SEO, 2: Calculator, 3: Quick Replies, 4: AI & TOS)
    private val _currentTab = MutableStateFlow(0)
    val currentTab: StateFlow<Int> = _currentTab.asStateFlow()

    fun selectTab(tab: Int) {
        _currentTab.value = tab
    }

    // Live Fiverr Account Sync State
    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> = _isSyncing.asStateFlow()

    private val _syncStatusMessage = MutableStateFlow<String?>(null)
    val syncStatusMessage: StateFlow<String?> = _syncStatusMessage.asStateFlow()

    private val _connectedUsername = MutableStateFlow("real_nadirali")
    val connectedUsername: StateFlow<String> = _connectedUsername.asStateFlow()

    fun syncFiverrProfile(input: String) {
        viewModelScope.launch {
            _isSyncing.value = true
            _syncStatusMessage.value = "Connecting to Fiverr..."
            val result = com.example.domain.FiverrSyncService.syncFromUsernameOrUrl(input)
            if (result.success) {
                _connectedUsername.value = result.username
                result.activeGigs.forEach { gig ->
                    repository.insertGig(gig)
                }
                result.detectedOrders.forEach { order ->
                    repository.insertOrder(order)
                }
                _syncStatusMessage.value = result.message
            } else {
                _syncStatusMessage.value = result.message
            }
            _isSyncing.value = false
        }
    }

    fun clearSyncMessage() {
        _syncStatusMessage.value = null
    }

    // Gigs
    val gigs: StateFlow<List<Gig>> = repository.allGigs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun saveGig(gig: Gig) {
        viewModelScope.launch {
            if (gig.id == 0L) {
                repository.insertGig(gig)
            } else {
                repository.updateGig(gig)
            }
        }
    }

    fun deleteGig(gig: Gig) {
        viewModelScope.launch {
            repository.deleteGig(gig)
        }
    }

    // Orders
    val orders: StateFlow<List<ClientOrder>> = repository.allOrders
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _orderStatusFilter = MutableStateFlow("All")
    val orderStatusFilter: StateFlow<String> = _orderStatusFilter.asStateFlow()

    fun setOrderStatusFilter(status: String) {
        _orderStatusFilter.value = status
    }

    fun saveOrder(order: ClientOrder) {
        viewModelScope.launch {
            if (order.id == 0L) {
                repository.insertOrder(order)
            } else {
                repository.updateOrder(order)
            }
        }
    }

    fun updateOrderStatus(order: ClientOrder, newStatus: String) {
        viewModelScope.launch {
            repository.updateOrder(order.copy(status = newStatus))
        }
    }

    fun deleteOrder(order: ClientOrder) {
        viewModelScope.launch {
            repository.deleteOrder(order)
        }
    }

    // Quick Replies
    val quickReplies: StateFlow<List<QuickReply>> = repository.allQuickReplies
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedReplyCategory = MutableStateFlow("All")
    val selectedReplyCategory: StateFlow<String> = _selectedReplyCategory.asStateFlow()

    fun selectReplyCategory(category: String) {
        _selectedReplyCategory.value = category
    }

    fun saveQuickReply(quickReply: QuickReply) {
        viewModelScope.launch {
            if (quickReply.id == 0L) {
                repository.insertQuickReply(quickReply)
            } else {
                repository.updateQuickReply(quickReply)
            }
        }
    }

    fun deleteQuickReply(quickReply: QuickReply) {
        viewModelScope.launch {
            repository.deleteQuickReply(quickReply)
        }
    }

    // Fee Calculator State
    private val _calculatorMode = MutableStateFlow(0) // 0: Gross -> Net, 1: Desired Net -> Quote
    val calculatorMode: StateFlow<Int> = _calculatorMode.asStateFlow()

    private val _grossAmount = MutableStateFlow("100")
    val grossAmount: StateFlow<String> = _grossAmount.asStateFlow()

    private val _tipAmount = MutableStateFlow("0")
    val tipAmount: StateFlow<String> = _tipAmount.asStateFlow()

    private val _reverseNet = MutableStateFlow("100")
    val reverseNet: StateFlow<String> = _reverseNet.asStateFlow()

    private val _selectedCurrency = MutableStateFlow(FeeCalculator.currencyRates[0])
    val selectedCurrency: StateFlow<Currency> = _selectedCurrency.asStateFlow()

    private val _targetGoal = MutableStateFlow("1000")
    val targetGoal: StateFlow<String> = _targetGoal.asStateFlow()

    fun setCalculatorMode(mode: Int) {
        _calculatorMode.value = mode
    }

    fun updateGross(amount: String) {
        _grossAmount.value = amount
    }

    fun adjustGross(delta: Double) {
        val current = _grossAmount.value.toDoubleOrNull() ?: 0.0
        val updated = (current + delta).coerceAtLeast(5.0)
        _grossAmount.value = updated.toInt().toString()
    }

    fun updateTip(amount: String) {
        _tipAmount.value = amount
    }

    fun updateReverseNet(amount: String) {
        _reverseNet.value = amount
    }

    fun selectCurrency(currency: Currency) {
        _selectedCurrency.value = currency
    }

    fun updateTargetGoal(goal: String) {
        _targetGoal.value = goal
    }

    fun getFeeCalculation(): FeeResult {
        val gross = _grossAmount.value.toDoubleOrNull() ?: 0.0
        val tip = _tipAmount.value.toDoubleOrNull() ?: 0.0
        return FeeCalculator.calculate(gross, tip)
    }

    fun getReverseQuote(): com.example.domain.ReverseQuoteResult {
        val desired = _reverseNet.value.toDoubleOrNull() ?: 100.0
        return FeeCalculator.calculateReverseQuote(desired)
    }

    fun getWithdrawalEstimates(): List<WithdrawalEstimate> {
        val calc = getFeeCalculation()
        return FeeCalculator.getWithdrawalEstimates(calc.netSellerEarnings, _selectedCurrency.value)
    }

    fun getGoalBreakdown(): GoalBreakdown {
        val target = _targetGoal.value.toDoubleOrNull() ?: 1000.0
        return FeeCalculator.calculateGoal(target)
    }

    // SEO Analyzer State
    private val _seoTitle = MutableStateFlow("I will develop automated trading bot for IBKR, MT4, MT5 or crypto")
    val seoTitle: StateFlow<String> = _seoTitle.asStateFlow()

    private val _seoTags = MutableStateFlow("trading bot, mt5, mt4, ibkr, python trading")
    val seoTags: StateFlow<String> = _seoTags.asStateFlow()

    private val _seoDescription = MutableStateFlow("Looking for a robust automated trading bot for Interactive Brokers (IBKR), MetaTrader 4/5, or crypto exchanges? I build institutional-grade algorithmic trading systems with clean architecture, risk management, and API execution.")
    val seoDescription: StateFlow<String> = _seoDescription.asStateFlow()

    fun updateSeoInputs(title: String, tags: String, description: String) {
        _seoTitle.value = title
        _seoTags.value = tags
        _seoDescription.value = description
    }

    fun getSeoReport(): SeoAnalysisReport {
        return GigSeoAnalyzer.analyze(
            title = _seoTitle.value,
            tagsInput = _seoTags.value,
            description = _seoDescription.value
        )
    }

    // Policy Safety Checker
    private val _safetyText = MutableStateFlow("Hello! Thanks for your order. Let's discuss on WhatsApp or send me your email so I can send the zip file.")
    val safetyText: StateFlow<String> = _safetyText.asStateFlow()

    fun updateSafetyText(text: String) {
        _safetyText.value = text
    }

    fun getSafetyReport(): SafetyScanReport {
        return PolicySafetyChecker.scan(_safetyText.value)
    }

    // AI Generator State
    private val _aiType = MutableStateFlow(GenerationType.GIG_DESCRIPTION)
    val aiType: StateFlow<GenerationType> = _aiType.asStateFlow()

    private val _aiSkill = MutableStateFlow("Automated Trading Bot (IBKR, MT4/MT5, Python & Crypto)")
    val aiSkill: StateFlow<String> = _aiSkill.asStateFlow()

    private val _aiDetails = MutableStateFlow("Automated execution bot connecting broker APIs with risk management, trailing stop, and webhook signals.")
    val aiDetails: StateFlow<String> = _aiDetails.asStateFlow()

    private val _aiTone = MutableStateFlow("Professional & Persuasive")
    val aiTone: StateFlow<String> = _aiTone.asStateFlow()

    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating.asStateFlow()

    private val _aiResult = MutableStateFlow<GenerationResult?>(null)
    val aiResult: StateFlow<GenerationResult?> = _aiResult.asStateFlow()

    fun setAiType(type: GenerationType) {
        _aiType.value = type
    }

    fun updateAiInputs(skill: String, details: String, tone: String) {
        _aiSkill.value = skill
        _aiDetails.value = details
        _aiTone.value = tone
    }

    fun runGeneration() {
        viewModelScope.launch {
            _isGenerating.value = true
            val result = AiGeneratorEngine.generate(
                type = _aiType.value,
                skillOrNiche = _aiSkill.value,
                details = _aiDetails.value,
                tone = _aiTone.value
            )
            _aiResult.value = result
            _isGenerating.value = false
        }
    }
}
