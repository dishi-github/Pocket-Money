# Pocket Bank: A Personal Bank for Young Kids 🏦

**Pocket Bank** is a visual and educational Android application designed to help children manage their finances, learn the value of saving, and understand how interest works. It functions like a real bank account where parents act as the "Central Bank" to set allowances and interest rates.

---

## Key Features ✨

### 💰 Automated Banking
- **Bank Ledger**: A clean, easy-to-read transaction history for Credits, Debits, and Interest.
- **Monthly Allowance**: Automatically credits a set pocket money amount on the 1st of every month.
- **Compound Interest**: The app calculates monthly interest based on the previous month's closing balance, teaching kids the power of compounding.

### 🎁 Smart Spending (Wishlist)
- **Goal Setting**: Kids can add items they want to buy (e.g., LEGO, games) with their prices.
- **Affordability Tracking**: The app shows a ❌ if they can't afford it and a ✅ when they have enough.
- **"Buy Now!" Button**: Encourages purposeful spending by allowing them to purchase items directly from their saved funds.

### 🛡️ Security & Privacy
- **PIN Lock**: Optional 4-digit PIN to keep the account private.
- **Local Storage**: All data is stored securely on the device. No internet or cloud required.

### ⚙️ Parental Controls
- **Custom Interest Rates**: Parents can set annual interest rates (e.g., 6%) from the profile.
- **Allowance Management**: Easily update the monthly pocket money amount.
- **Personalization**: Customize with the child's name and a photo selected from the gallery.

---

## How it Works 📈

### Interest Calculation
The app uses a simplified "Bank-Accurate" formula:
- **Formula**: `Monthly Interest = Math.ceil((Previous Month Balance * Annual Rate) / 1200)`
- **Rounding**: Interest is always rounded **up** to the nearest whole number to keep it rewarding and simple for children.
- **Timing**: Interest is credited on the 1st of every month *before* the new allowance is added.

---

## Installation & Setup 🚀

1. **Download**: Click the links below to download the ready-to-use APK files directly from this repository:
   - [**Download Release APK (v1.1)**](https://github.com/dishi-github/Pocket-Money/raw/main/releases/PocketMoney-1.1-release.apk) - *Recommended for general use.*
   - [**Download Debug APK (v1.1)**](https://github.com/dishi-github/Pocket-Money/raw/main/releases/PocketMoney-1.1-debug.apk) - *For testing and development.*

2. **Initial Setup**:
   - Open the app and set an initial balance.
   - Go to **Profile** to set the child's name and photo.
   - Configure the **Monthly Amount** and **Interest Rate**.
3. **Usage**:
   - Use **Credit Money** for gifts or earned rewards.
   - Use **Debit Money** for daily expenses.
   - Use the **Wishlist** for long-term goals.

---

## Tech Stack 🛠️

- **Language**: Java
- **UI**: Native Android (XML-less, dynamic View layout)
- **Database**: SQLite
- **Architecture**: Single Activity (for speed and simplicity)
- **Minimum SDK**: API 24 (Android 7.0+)

---

## Build Instructions

```bash
# Clone the repository
git clone https://github.com/dishi-github/Pocket-Money.git

# Navigate to project root
cd Pocket-Money

# Build Debug APK
./gradlew assembleDebug
```

---
*Created to promote financial literacy and responsible spending habits in children.*
