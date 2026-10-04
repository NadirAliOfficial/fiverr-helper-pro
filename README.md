# Fiverr Helper Pro 🚀

A comprehensive, production-grade Android utility application for Fiverr freelancers, agency owners, and top-rated sellers. Built with modern Android development standards using **Kotlin**, **Jetpack Compose (Material 3)**, and **Room Database**.

---

## ✨ Key Features

### ⚡ 1. Real-Time Push Notification Interceptor (100% Automated)
- **Zero-Touch Order Sync**: Intercepts notifications from the official Fiverr app using Android's native `NotificationListenerService`.
- **Intelligent Order Parser**: Automatically extracts buyer usernames (`@buyer`), order amounts (`$`), gig titles, and delivery deadlines without any manual typing.
- **Immediate Profit & Countdown**: Instantly calculates your 80% net earnings and starts a delivery countdown timer.

### 🛒 2. Order Queue & Take-Home Profit Dashboard
- **Active Orders Management**: Track orders through custom states (*In Progress*, *Revision Requested*, *Delivered*, *Completed*).
- **80% Net Take-Home Calculation**: Real-time breakdown of gross client order value vs. net seller profit after Fiverr's 20% platform fee.
- **Dynamic Delivery Countdown**: Visual urgency badges (e.g., `< 24h`, `3d 12h`) to ensure you never miss a delivery deadline.

### 📋 3. Gig Manager & SEO Ranking Optimizer
- **Live Fiverr SEO Audit**: Instant evaluation of Gig Titles, 5 Search Tags, and Gig Descriptions.
- **Algorithm Score**: Calculates a 0–100 SEO score based on title length, keyword frequency, and tag optimization.
- **3-Tier Pricing Packages**: Manage Basic, Standard, and Premium package scopes and prices.

### 🧮 4. Fee & Reverse Quote Calculator
- **20% Fiverr Fee Breakdown**: Displays gross order, platform cut, and net seller take-home.
- **Reverse Quote Generator**: Enter your desired net profit, and the app calculates the exact gross quote you should send the buyer.
- **Multi-Currency Support**: Instant conversion across USD ($), EUR (€), GBP (£), CAD ($), AUD ($), and PKR (₨).
- **Bank & PayPal Withdrawal Estimator**: Calculates estimated transfer fees and net deposited funds.

### 💬 5. Quick Replies & TOS Safety Compliance
- **1-Tap Client Response Templates**: Pre-written templates for Order Requirements, Order Delivery, Late Delivery Notice, Revision Responses, and FAQ.
- **Fiverr Policy Violation Scanner**: Real-time scanner checking text for prohibited keywords (*WhatsApp, PayPal, Email, Wire, Telegram, Skype, Phone*) to prevent account strikes and bans.

### 🤖 6. AI Gig Description & Proposal Generator
- Generates high-converting Fiverr gig descriptions, persuasive custom offer proposals, and buyer brief pitches.

---

## 🛠 Tech Stack & Architecture

- **Language**: Kotlin 1.9+
- **UI Framework**: Jetpack Compose with Material Design 3 (M3)
- **Architecture**: MVVM (Model-View-ViewModel) + Clean Architecture
- **State Management**: Kotlin Coroutines & `StateFlow` / `collectAsStateWithLifecycle`
- **Local Persistence**: Room Database (SQLite) with reactive queries
- **Background Processing**: Android `NotificationListenerService`
- **Testing**: Robolectric JVM unit tests & AndroidX test suite

---

## 🚀 Getting Started

### Prerequisites
- Android Studio Hedgehog (2023.1.1) or newer
- JDK 17
- Android SDK 34 (Android 14)

### Build & Run
```bash
# Clone the repository
git clone https://github.com/your-username/fiverr-helper.git

# Build debug APK
gradle assembleDebug

# Run unit and Robolectric tests
gradle :app:testDebugUnitTest
```

---

## 🔒 Privacy & Fiverr Terms of Service
- **100% Local Storage**: All orders, gigs, and templates are stored locally on your device in Room SQLite.
- **Safe Notification Processing**: Uses standard Android notification access APIs; no reverse-engineering of private APIs or scraping required.
