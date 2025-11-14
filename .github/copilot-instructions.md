# CORR4 Bank Application - AI Coding Agent Instructions

## Project Overview
**CORR4** is a JavaFX-based desktop banking application with a three-tier architecture: UI layer (JavaFX), business logic (BankingService), and data persistence (DAO + PostgreSQL).

**Build Command:** `mvn clean javafx:run` (from `Corr4_App` directory)  
**Java Target:** Java 9 (configured in `pom.xml`)  
**Key Framework:** JavaFX 21.0.6 + Maven

---

## Architecture Overview

### Three-Tier Design
1. **UI Layer** (`com.css123group.corr4_app`): JavaFX controllers + FXML views
   - Controllers: `LoginController`, `HomeController`, `BalanceController`, `ProfileController`, etc.
   - FXML files in `src/main/resources/com/css123group/corr4_app/`
   - Main app entry: `BankApp.java` loads `Login.fxml` first

2. **Business Logic** (`corr4_be`): `BankingService` orchestrates operations
   - Handles customer creation, account management, deposits, withdrawals, transfers
   - Uses `BigDecimal` for monetary amounts (NOT float/double)
   - Catches `SQLException` and logs errors; returns boolean for success/failure

3. **Data Access** (`corr4_be`): DAO pattern for database operations
   - `CustomerDAO`, `AccountDAO`, `TransactionDAO`
   - Each DAO uses `DatabaseConnection.getConnection()` for SQL operations
   - Use try-with-resources for PreparedStatements and Connections

### Package Structure
```
com.css123group.corr4_app/          # UI Controllers
corr4_be/                           # Business logic + DAOs + Models
├── BankingService.java            # Orchestrator for all operations
├── DatabaseConnection.java        # PostgreSQL connection manager
├── Customer.java, Account.java, Transaction.java  # Model classes
└── CustomerDAO.java, AccountDAO.java, TransactionDAO.java  # Data access
```

---

## Key Technical Patterns

### 1. Navigation (JavaFX Scene Management)
**Pattern:** BorderPane with dynamic center content loading  
**Example:** `HomeController.loadCenterContent()` swaps FXML views in the center pane
```java
// In HomeController.java
private void loadCenterContent(String fxmlFile) throws IOException {
    Parent content = FXMLLoader.load(getClass().getResource(fxmlFile));
    rootPane.setCenter(content);
}
```
- **Resource path rule:** Use `@` relative to FXML file location or resource classpath prefix
- Controllers must have matching `fx:controller` attribute in FXML

### 2. Monetary Amounts (Always Use BigDecimal)
**Rule:** Never use `float` or `double` for money  
**Example:** `BankingService.createAccount()` uses `BigDecimal` for initial deposits and balances
```java
BigDecimal newBalance = account.getBalance().add(amount);  // NOT account.getBalance() + amount
```

### 3. Database Operations (DAO + PreparedStatement)
**Pattern:** Every DAO method opens its own connection via `DatabaseConnection.getConnection()`  
**Safety Rule:** Always use try-with-resources to auto-close Statement and Connection
```java
try (Connection conn = DatabaseConnection.getConnection();
     PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
    // set parameters with pstmt.setXxx(index, value)
    // pstmt.executeUpdate() or executeQuery()
}
```
- Use `Statement.RETURN_GENERATED_KEYS` when inserting and you need auto-generated IDs
- Extract ResultSet data into model objects via helper method (e.g., `extractCustomerFromResultSet()`)

### 4. Account Number Generation
**Pattern:** `BankingService.generateAccountNumber()` prefixes with account type + timestamp  
- Prefixes: `SAV` (Savings), `CHK` (Checking), `BUS` (Business), `GEN` (Generic)
- Ensures account numbers are unique and descriptive

### 5. Transaction Recording (Immutable Audit Trail)
**Requirement:** Every balance-changing operation must call `transactionDAO.recordTransaction()`  
- **Parameters:** accountId, type (DEPOSIT/WITHDRAWAL/TRANSFER), amount, description, new balance
- Ensures data consistency and audit trail

### 6. Error Handling in BankingService
**Pattern:** Catch `SQLException`, log to stderr, return `boolean` (not exceptions)
```java
try {
    // DAO operation
} catch (SQLException e) {
    System.err.println("Error creating account: " + e.getMessage());
    return false;
}
```
- Controllers check boolean return values and update UI accordingly

---

## Build & Deployment

### Maven Lifecycle
```bash
cd Corr4_App
mvn clean compile javafx:run    # Run the app
mvn clean package               # Build JAR
mvn test                        # Run tests (JUnit 5 configured)
```

### Maven Plugins Configured
- **javafx-maven-plugin (0.0.8):** Enables `mvn javafx:run` and handles JavaFX module path
  - Main class: `com.css123group.corr4_app.BankApp`
- **maven-compiler-plugin (3.13.0):** Targets Java 9

### Database Setup
- **Provider:** PostgreSQL (Supabase)
- **Connection:** `DatabaseConnection.java` hardcodes credentials (DEV ONLY - should use env vars)
- **Required Tables:** `customers`, `accounts`, `transactions` (schema not in repo; infer from DAOs)

---

## Common Development Tasks

### Adding a New UI Screen
1. Create FXML file in `src/main/resources/com/css123group/corr4_app/`
2. Create corresponding Controller class in `com.css123group.corr4_app` package
3. Add fx:controller attribute to FXML root element
4. Add navigation handler in `HomeController.handleXxx()` that calls `loadCenterContent("NewScreen.fxml")`
5. Add Button to HomePage.fxml sidebar with `onAction="#handleXxx"`

### Adding a Database Entity
1. Create model class in `corr4_be` package (e.g., `Card.java`) with getters/setters
2. Create DAO class (e.g., `CardDAO.java`) following `CustomerDAO` pattern
3. Add DAO initialization to `BankingService` constructor
4. Add business logic methods to `BankingService` that use the new DAO
5. Ensure all balance/ledger changes call `transactionDAO.recordTransaction()`

### Adding a Business Operation
1. Implement in `BankingService` (not Controllers)
2. Use existing DAOs; wrap in try-catch, return boolean
3. Call transaction DAO for any balance changes
4. Validate preconditions (e.g., sufficient funds, account existence)
5. Log errors to stderr with context

---

## Project-Specific Conventions

| Aspect | Convention | Example |
|--------|-----------|---------|
| **Monetary Values** | Always `BigDecimal` | `account.getBalance()` returns `BigDecimal`, not `double` |
| **Resource Paths** | Classpath with package hierarchy | `/com/css123group/corr4_app/HomePage.fxml` in resource folder |
| **DAO Methods** | One connection per method, try-with-resources | `getCustomerById(int id)` opens Connection in its try block |
| **Error Returns** | Boolean (not exceptions) from service | `BankingService.createAccount()` returns `boolean` |
| **Transaction Recording** | Mandatory for balance changes | `deposit()` MUST call `transactionDAO.recordTransaction()` |
| **CSS Styling** | External `homepage.css` linked in FXML | All inline styles should be moved to stylesheet |
| **Account Numbers** | Type-prefixed with timestamp | `SAV0001234`, `CHK0005678` |

---

## Debugging Tips

1. **Scene Loading Errors:** Check FXML resource paths in FXMLLoader; ensure `fx:controller` matches actual controller class name and package
2. **Database Connection Fails:** Verify PostgreSQL credentials in `DatabaseConnection.java` and network connectivity
3. **NullPointerException on FXML Elements:** Ensure `@FXML` field names match `fx:id` in FXML; controllers must be instantiated by FXMLLoader (not `new Controller()`)
4. **Account Number Conflicts:** Check `generateAccountNumber()` logic; add uniqueness constraint in database
5. **Missing Transactions in Audit Trail:** Verify all balance-changing methods call `transactionDAO.recordTransaction()`

---

## Dependency Overview

| Dependency | Version | Purpose |
|-----------|---------|---------|
| `javafx-controls`, `javafx-fxml`, etc. | 21.0.6 | UI framework |
| `controlsfx` | 11.2.1 | Enhanced JavaFX controls |
| `formsfx-core` | 11.6.0 | Form-building utilities |
| `ikonli-javafx` | 12.3.1 | Icon library for JavaFX |
| `bootstrapfx-core` | 0.4.0 | Bootstrap-inspired styling |
| `tilesfx` | 21.0.9 | Dashboard tile components |
| `junit-jupiter` | 5.12.1 | Unit testing framework |
| PostgreSQL JDBC Driver | (implicit) | Database connectivity |

---

## Notes for AI Agents

- **Project Maturity:** Active development; class structure stable but database schema not versioned in repo
- **Testing:** JUnit 5 configured but tests not yet included in repo; add tests for new DAOs/services
- **Security:** Credentials hardcoded in `DatabaseConnection.java` (development only); use environment variables for production
- **UI Polish:** CSS files present but basic; coordinate with design team before major style changes
- **Module System:** `module-info.java` present; maintain module exports when adding new public APIs
