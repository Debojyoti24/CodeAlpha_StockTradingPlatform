# Trading Platform

A simple stock trading platform implemented in Java.

## Installation

To use the Trading Platform, follow these steps:

1. Clone the repository:
   ```
   git clone https://github.com/your-username/trading-platform.git
   ```
2. Compile the Java files:
   ```
   javac *.java
   ```

## Usage

To run the Trading Platform, execute the following command:

```
java TradingPlatform
```

The program will start and display the following menu:

```
=== Stock Trading Platform ===
1. Display Market Data
2. Buy Stock
3. Sell Stock
4. View Portfolio
5. Update Market Prices
6. Exit
Choose an option:
```

You can interact with the platform by selecting the corresponding option.

## API

The Trading Platform provides the following API:

1. `registerUser(String username, double initialBalance)`: Registers a new user with the given username and initial balance.
2. `buyStock(String username, String stockSymbol, int quantity)`: Allows the user to buy a specified quantity of a stock.
3. `sellStock(String username, String stockSymbol, int quantity)`: Allows the user to sell a specified quantity of a stock.
4. `displayPortfolio(String username)`: Displays the user's portfolio, including cash balance, holdings, and transaction history.
5. `displayMarketData()`: Displays the current market data, including stock prices and price changes.
6. `updateMarketPrices()`: Updates the market prices for all stocks.

## License

This project is licensed under the [MIT License](LICENSE).

## Testing

To run the tests for the Trading Platform, execute the following command:

```
javac *.java
java TradingPlatform
```

The program will start and you can interact with the platform to test its functionality.
