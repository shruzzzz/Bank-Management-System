# Ledger Bank — Bank Management System (Java / Spring Boot)

A working web-based bank management system: user registration, login, multiple
accounts per user, deposits, withdrawals, transfers between accounts, and
transaction history — all backed by a real database.

## Tech stack
- **Java 17**
- **Spring Boot 3** (Web MVC + Spring Data JPA)
- **Thymeleaf** for server-rendered HTML pages
- **H2** file-based database (no separate database server to install — a file
  called `data/bankdb.mv.db` is created automatically the first time you run it)
- **Maven** for build/dependency management

## Project structure
```
src/main/java/com/bank/management/
  BankManagementApplication.java   # entry point, seeds a demo user on first run
  model/                           # User, Account, Transaction (JPA entities)
  repository/                      # Spring Data JPA repositories
  service/                         # UserService, AccountService (business logic)
  controller/                      # AuthController, DashboardController, TransactionController
src/main/resources/
  application.properties           # database + server config
  templates/                       # Thymeleaf HTML pages
  static/css/style.css              # styling
```

## 1. Prerequisites
1. **Java 17 or newer** — check with `java -version` in a terminal.
   Download from https://adoptium.net if needed.
2. **VS Code** with these two extensions:
   - **Extension Pack for Java** (by Microsoft) — includes Maven support, so
     you do **not** need to install Maven separately.
   - **Spring Boot Extension Pack** (by VMware/Pivotal) — adds a "Run" button
     and Spring-aware navigation.

## 2. Open the project in VS Code
1. Unzip the project folder.
2. In VS Code: **File → Open Folder…** and select the `bank-management-system` folder.
3. Wait a moment — VS Code will detect the `pom.xml` and download the Maven
   dependencies automatically (needs an internet connection the first time).

## 3. Run it
Pick whichever is easiest for you:

- **Easiest:** open `BankManagementApplication.java`, and click the small
  **Run** ▷ link that appears above the `main` method.
- **Or** open a VS Code terminal (`` Ctrl+` ``) and run:
  ```
  ./mvnw spring-boot:run
  ```
  (On Windows use `mvnw.cmd spring-boot:run`. If you don't have the wrapper,
  install Maven and run `mvn spring-boot:run` instead.)

Either way, once you see `Started BankManagementApplication` in the console,
open **http://localhost:8080** in your browser.

## 4. Log in
A demo account is created automatically the first time the app runs:
- **Username:** `demo`
- **Password:** `demo123`
- Comes pre-loaded with $5,000 in a savings account (`ACC1000001`).

Or click **Open an account** on the login page to register your own.

## 5. What you can do
- **Deposit** money into an account
- **Withdraw** money (blocked if it would overdraw the account)
- **Transfer** to any other account by account number
- **View statement** — full transaction history with running balance
- Every user can hold multiple accounts; each transaction is tied to the
  exact account, so balances always reconcile.

## 6. Inspecting the database directly (optional)
While the app is running, visit **http://localhost:8080/h2-console**.
- JDBC URL: `jdbc:h2:file:./data/bankdb`
- User: `sa`, no password
This lets you run raw SQL against the `users`, `accounts`, and `transactions`
tables — handy for a class demo or debugging.

## 7. Switching to MySQL instead of H2 (optional)
If your assignment requires MySQL specifically:
1. In `pom.xml`, replace the H2 dependency with:
   ```xml
   <dependency>
       <groupId>com.mysql</groupId>
       <artifactId>mysql-connector-j</artifactId>
       <scope>runtime</scope>
   </dependency>
   ```
2. In `application.properties`, replace the datasource lines with:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/bankdb?createDatabaseIfNotExist=true
   spring.datasource.username=root
   spring.datasource.password=yourpassword
   spring.jpa.database-platform=org.hibernate.dialect.MySQLDialect
   ```
3. Make sure a local MySQL server is running, then start the app as before.

## Notes on this being a learning project
Passwords are stored in plain text and there's no CSRF protection or proper
password hashing — that's intentional to keep the code readable for a student
project, but call it out explicitly if you present this: a production bank app
would use Spring Security with BCrypt-hashed passwords at minimum. Happy to add
that in if this is going further than a class project.
