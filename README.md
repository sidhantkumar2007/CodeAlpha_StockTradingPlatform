# 📈 Stock Trading Platform

A Java Swing desktop application simulating a real-time stock trading platform.

---

## Overview

This is a **paper trading simulator** — a risk-free environment where users start with a virtual balance of **$100,000** and can buy/sell stocks, track their portfolio, and watch live simulated market price changes — all through a sleek dark-themed GUI.

---

## Features

- **Live Market Table** — Displays 8 real-world stocks with current price, percentage change, and trend indicators (▲ / ▼).
- **Quick Trade Panel** — Select a stock symbol, enter a quantity, and execute BUY or SELL orders instantly.
- **Portfolio Tracker** — Shows all holdings with quantity, average cost, current price, and profit/loss per position.
- **Transaction History** — Full log of every trade with timestamp, type, symbol, quantity, price, and total value.
- **Stats Dashboard** — Real-time top bar showing Cash Balance, Total Portfolio Value, Profit/Loss, and Return %.
- **Market Simulator** — Click "Simulate Market" to randomly fluctuate all stock prices (±5%), or let the auto-timer do it every 8 seconds.
- **Average Cost Tracking** — Accurately calculates weighted average cost across multiple purchases of the same stock.
- **Dark UI Theme** — Professional dark color scheme built entirely with Java Swing.

---

## Stocks Available

| Symbol | Company         | Starting Price |
|--------|-----------------|----------------|
| AAPL   | Apple Inc.      | $189.50        |
| GOOGL  | Alphabet Inc.   | $141.80        |
| MSFT   | Microsoft Corp. | $420.25        |
| AMZN   | Amazon.com      | $182.60        |
| TSLA   | Tesla Inc.      | $248.90        |
| META   | Meta Platforms  | $512.30        |
| NVDA   | NVIDIA Corp.    | $875.40        |
| NFLX   | Netflix Inc.    | $628.70        |

---

## Project Structure

```
StockTradingPlatformGUI.java              # Main source file
StockTradingPlatformGUI.class             # Compiled main class
StockTradingPlatformGUI$Stock.class       # Static inner Stock data model
StockTradingPlatformGUI$Transaction.class # Static inner Transaction data model
StockTradingPlatformGUI$1.class  through
StockTradingPlatformGUI$11.class          # Anonymous inner classes (renderers, listeners, timers)
```

---

## Requirements

- **Java 17 or higher**
- No external libraries — standard Java SE only (`javax.swing`, `java.awt`, `java.time`, `java.util`)

---

## How to Run

### Option 1 — Run from pre-compiled `.class` files

Place all `.class` files in the same directory and run:

```bash
java StockTradingPlatformGUI
```

### Option 2 — Compile and run from source

```bash
javac StockTradingPlatformGUI.java
java StockTradingPlatformGUI
```

---

## How to Use

1. **View the market** — The left panel shows all 8 stocks with live prices and trend indicators.
2. **Buy a stock** — Select a symbol from the dropdown, enter a quantity, and click **BUY**.
3. **Sell a stock** — Select a symbol you own, enter a quantity, and click **SELL**.
4. **Simulate price changes** — Click **🔄 Simulate Market** to manually trigger price fluctuations, or wait 8 seconds for auto-update.
5. **Track your portfolio** — The top-right panel shows all your holdings and per-position P/L.
6. **Review trades** — The bottom-right panel logs every transaction with full details.
7. **Monitor performance** — The stats bar at the top always shows your current balance, total value, and overall return.

---

## Trading Rules

- Starting virtual balance: **$100,000**
- You cannot buy more shares than your cash balance allows.
- You cannot sell more shares than you currently hold.
- Market prices auto-update every **8 seconds** with random fluctuations of up to ±5%.

---

## Author

**Sidhant Kumar**
🎓 Java Programming Intern @ CodeAlpha
🐙 GitHub: [sidhantkumar2007](https://github.com/sidhantkumar2007)
📌 Project: CodeAlpha Internship — Task 2
