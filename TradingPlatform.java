package CodeAlpha_StockTradingPlatform;
import java.io.*;
import java.util.*;

class Stock {
    private String symbol;
    private String name;
    private double price;
    private double change;

    public Stock(String symbol, String name, double price) {
        this.symbol = symbol;
        this.name = name;
        this.price = price;
        this.change = 0.0;
    }

    public String getSymbol() { return symbol; }
    public String getName() { return name; }
    public double getPrice() { return price; }
    public double getChange() { return change; }

    public void updatePrice(double newPrice) {
        this.change = ((newPrice - this.price) / this.price) * 100;
        this.price = newPrice;
    }
}

class Portfolio {
    private Map<String, Integer> holdings;
    private double cashBalance;

    public Portfolio(double initialBalance) {
        this.holdings = new HashMap<>();
        this.cashBalance = initialBalance;
    }

    public Map<String, Integer> getHoldings() { return holdings; }
    public double getCashBalance() { return cashBalance; }

    public boolean buyStock(Stock stock, int quantity) {
        double cost = stock.getPrice() * quantity;
        if (cost <= cashBalance) {
            cashBalance -= cost;
            holdings.put(stock.getSymbol(), holdings.getOrDefault(stock.getSymbol(), 0) + quantity);
            return true;
        }
        return false;
    }

    public boolean sellStock(Stock stock, int quantity) {
        int currentQuantity = holdings.getOrDefault(stock.getSymbol(), 0);
        if (currentQuantity >= quantity) {
            cashBalance += stock.getPrice() * quantity;
            holdings.put(stock.getSymbol(), currentQuantity - quantity);
            if (holdings.get(stock.getSymbol()) == 0) {
                holdings.remove(stock.getSymbol());
            }
            return true;
        }
        return false;
    }

    public double calculatePortfolioValue(Map<String, Stock> market) {
        double totalValue = cashBalance;
        for (Map.Entry<String, Integer> entry : holdings.entrySet()) {
            Stock stock = market.get(entry.getKey());
            if (stock != null) {
                totalValue += stock.getPrice() * entry.getValue();
            }
        }
        return totalValue;
    }
}

class Transaction {
    private String type;
    private String stockSymbol;
    private int quantity;
    private double price;
    private Date timestamp;

    public Transaction(String type, String stockSymbol, int quantity, double price) {
        this.type = type;
        this.stockSymbol = stockSymbol;
        this.quantity = quantity;
        this.price = price;
        this.timestamp = new Date();
    }

    @Override
    public String toString() {
        return String.format("%s: %s %d shares at INR %.2f on %s",
                type, stockSymbol, quantity, price, timestamp);
    }
}

class User {
    private String username;
    private Portfolio portfolio;
    private List<Transaction> transactionHistory;

    public User(String username, double initialBalance) {
        this.username = username;
        this.portfolio = new Portfolio(initialBalance);
        this.transactionHistory = new ArrayList<>();
    }

    public String getUsername() { return username; }
    public Portfolio getPortfolio() { return portfolio; }
    public List<Transaction> getTransactionHistory() { return transactionHistory; }

    public void addTransaction(Transaction transaction) {
        transactionHistory.add(transaction);
    }
}

class StockMarket {
    private Map<String, Stock> stocks;
    private Random random;

    public StockMarket() {
        stocks = new HashMap<>();
        random = new Random();
        initializeMarket();
    }

    private void initializeMarket() {
        stocks.put("AAPL", new Stock("AAPL", "Apple Inc.", 150.0));
        stocks.put("GOOGL", new Stock("GOOGL", "Alphabet Inc.", 2800.0));
        stocks.put("MSFT", new Stock("MSFT", "Microsoft Corp.", 300.0));
        stocks.put("AMZN", new Stock("AMZN", "Amazon.com Inc.", 3500.0));
    }

    public void updateMarketPrices() {
        for (Stock stock : stocks.values()) {
            double change = (random.nextDouble() - 0.5) * 10;
            stock.updatePrice(stock.getPrice() + change);
        }
    }

    public Map<String, Stock> getStocks() { return stocks; }

    public void displayMarketData() {
        System.out.println("\n=== Market Data ===");
        for (Stock stock : stocks.values()) {
            System.out.printf("%s (%s): INR %.2f (%.2f%%)%n",
                    stock.getSymbol(), stock.getName(), stock.getPrice(), stock.getChange());
        }
    }
}

class TradingPlatform {
    private Map<String, User> users;
    private StockMarket market;
    private static final String DATA_FILE = "portfolio_data.txt";

    public TradingPlatform() {
        users = new HashMap<>();
        market = new StockMarket();
        loadPortfolioData();
    }

    public void registerUser(String username, double initialBalance) {
        users.put(username, new User(username, initialBalance));
    }

    public boolean buyStock(String username, String stockSymbol, int quantity) {
        User user = users.get(username);
        Stock stock = market.getStocks().get(stockSymbol);
        if (user != null && stock != null) {
            if (user.getPortfolio().buyStock(stock, quantity)) {
                user.addTransaction(new Transaction("BUY", stockSymbol, quantity, stock.getPrice()));
                savePortfolioData();
                return true;
            }
        }
        return false;
    }

    public boolean sellStock(String username, String stockSymbol, int quantity) {
        User user = users.get(username);
        Stock stock = market.getStocks().get(stockSymbol);
        if (user != null && stock != null) {
            if (user.getPortfolio().sellStock(stock, quantity)) {
                user.addTransaction(new Transaction("SELL", stockSymbol, quantity, stock.getPrice()));
                savePortfolioData();
                return true;
            }
        }
        return false;
    }

    public void displayPortfolio(String username) {
        User user = users.get(username);
        if (user != null) {
            System.out.println("\n=== Portfolio for " + username + " ===");
            System.out.printf("Cash Balance: INR %.2f%n", user.getPortfolio().getCashBalance());
            System.out.println("Holdings:");
            for (Map.Entry<String, Integer> entry : user.getPortfolio().getHoldings().entrySet()) {
                Stock stock = market.getStocks().get(entry.getKey());
                if (stock != null) {
                    System.out.printf("%s: %d shares (Value: INR %.2f)%n",
                            entry.getKey(), entry.getValue(), entry.getValue() * stock.getPrice());
                }
            }
            System.out.printf("Total Portfolio Value: INR %.2f%n",
                    user.getPortfolio().calculatePortfolioValue(market.getStocks()));
            System.out.println("\nTransaction History:");
            for (Transaction transaction : user.getTransactionHistory()) {
                System.out.println(transaction);
            }
        }
    }

    public void displayMarketData() {
        market.displayMarketData();
    }

    public void updateMarketPrices() {
        market.updateMarketPrices();
    }

    private void savePortfolioData() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(DATA_FILE))) {
            for (User user : users.values()) {
                writer.println("USER:" + user.getUsername());
                writer.println("CASH:" + user.getPortfolio().getCashBalance());
                writer.println("HOLDINGS:");
                for (Map.Entry<String, Integer> holding : user.getPortfolio().getHoldings().entrySet()) {
                    writer.println(holding.getKey() + ":" + holding.getValue());
                }
                writer.println("TRANSACTIONS:");
                for (Transaction transaction : user.getTransactionHistory()) {
                    writer.println(transaction.toString());
                }
                writer.println("END_USER");
            }
        } catch (IOException e) {
            System.out.println("Error saving portfolio data: " + e.getMessage());
        }
    }

    private void loadPortfolioData() {
        try (BufferedReader reader = new BufferedReader(new FileReader(DATA_FILE))) {
            String line;
            User currentUser = null;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("USER:")) {
                    String username = line.substring(5);
                    currentUser = new User(username, 0);
                    users.put(username, currentUser);
                } else if (line.startsWith("CASH:") && currentUser != null) {
                    currentUser.getPortfolio().getHoldings().clear();
                    currentUser.getPortfolio().buyStock(new Stock("CASH", "Cash", 1.0), 
                            (int) Double.parseDouble(line.substring(5)));
                } else if (line.startsWith("HOLDINGS:") && currentUser != null) {
                    while (!(line = reader.readLine()).startsWith("TRANSACTIONS:")) {
                        String[] parts = line.split(":");
                        if (parts.length == 2) {
                            currentUser.getPortfolio().buyStock(
                                    market.getStocks().getOrDefault(parts[0], new Stock(parts[0], parts[0], 0)),
                                    Integer.parseInt(parts[1]));
                        }
                    }
                } else if (line.startsWith("TRANSACTIONS:") && currentUser != null) {
                    while (!(line = reader.readLine()).startsWith("END_USER")) {
                        // Note: Simplified transaction loading; in production, parse properly
                        currentUser.addTransaction(new Transaction("LOAD", "UNKNOWN", 0, 0.0));
                    }
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("No existing portfolio data found.");
        } catch (IOException e) {
            System.out.println("Error loading portfolio data: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        TradingPlatform platform = new TradingPlatform();
        Scanner scanner = new Scanner(System.in);

        platform.registerUser("john_doe", 10000.0);

        while (true) {
            System.out.println("\n=== Stock Trading Platform ===");
            System.out.println("1. Display Market Data");
            System.out.println("2. Buy Stock");
            System.out.println("3. Sell Stock");
            System.out.println("4. View Portfolio");
            System.out.println("5. Update Market Prices");
            System.out.println("6. Exit");
            System.out.print("Choose an option: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    platform.displayMarketData();
                    break;
                case 2:
                    System.out.print("Enter stock symbol: ");
                    String buySymbol = scanner.nextLine();
                    System.out.print("Enter quantity: ");
                    int buyQuantity = scanner.nextInt();
                    if (platform.buyStock("john_doe", buySymbol, buyQuantity)) {
                        System.out.println("Purchase successful!");
                    } else {
                        System.out.println("Purchase failed!");
                    }
                    break;
                case 3:
                    System.out.print("Enter stock symbol: ");
                    String sellSymbol = scanner.nextLine();
                    System.out.print("Enter quantity: ");
                    int sellQuantity = scanner.nextInt();
                    if (platform.sellStock("john_doe", sellSymbol, sellQuantity)) {
                        System.out.println("Sale successful!");
                    } else {
                        System.out.println("Sale failed!");
                    }
                    break;
                case 4:
                    platform.displayPortfolio("john_doe");
                    break;
                case 5:
                    platform.updateMarketPrices();
                    System.out.println("Market prices updated!");
                    break;
                case 6:
                    System.out.println("Exiting...");
                    scanner.close();
                    return;
                default:
                    System.out.println("Invalid option!");
            }
        }
    }
}