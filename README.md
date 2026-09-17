# StockRoom

StockRoom is a simple inventory management website built with Java Spring Boot and SQL. It helps users add products, view stock quantities, check product statistics, and manage inventory in Indian rupees.

## Features

- User registration and login
- Forgot-password flow
- Add products with name, SKU, category, supplier, quantity, reorder level, and price
- Search and filter products
- Update stock quantities
- Delete products
- Product quantity and category statistics
- INR currency display
- CSV inventory export
- H2 SQL database with separate SQL files

## Technology

- Java 17+
- Spring Boot 3.4
- Spring MVC and Thymeleaf
- Spring Data JPA
- H2 SQL database
- HTML, CSS, and JavaScript
- Maven

## Run Locally

Make sure Java and Maven are installed, then run:

```powershell
mvn spring-boot:run
```

Or run the packaged application:

```powershell
mvn package
java -jar target/inventory-studio-1.0-SNAPSHOT.jar
```

Open the application at:

```text
http://localhost:8080
```

## Database

The application uses a file-based H2 database at `data/inventory-db`.

- `src/main/resources/schema.sql` creates the database tables.
- `src/main/resources/data.sql` adds sample inventory records.
- `src/main/resources/application.properties` contains the database configuration.

The H2 console is available at:

```text
http://localhost:8080/h2-console
```

Use this JDBC URL in the console:

```text
jdbc:h2:file:./data/inventory-db
```



## Main Routes

| Route | Purpose |
| --- | --- |
| `/login` | Sign in |
| `/register` | Create an account |
| `/forgot-password` | Reset a password |
| `/inventory` | Inventory dashboard |
| `/api/inventory` | Inventory REST API |
| `/api/auth` | Authentication REST API |

## Project Structure

```text
src/main/java/     Spring Boot application and API controllers
src/main/resources/ SQL scripts, templates, CSS, and JavaScript
data/              Local database files created at runtime
```

The local `data/` and Maven `target/` folders are excluded from Git using `.gitignore`.
