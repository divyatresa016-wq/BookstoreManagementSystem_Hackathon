# Bookstore Management System

A Java-based Bookstore Management System built using **Java Swing, JDBC, and MySQL**. The application provides a graphical interface for managing books, customers, purchases, and book rentals.

## Features

### 1. Book Management
- Add, view, search, update, and delete book records.
- Manage book details such as title, author, category, price, and stock.
- Validate inputs before saving records.

### 2. Customer Management
- Add and manage customer information.
- View customer records and update customer details.
- Store customer names, email addresses, and phone numbers.

### 3. Purchase Management
- Manage book purchases.
- Maintain purchase records and purchase items.
- Calculate purchase totals and update book stock.

### 4. Rental Management
- Rent available books to registered customers for a specified period.
- Calculate rental charges based on the actual number of days the book is kept, with a minimum charge of ₹20.
- Calculate overdue fines at ₹10 per day after the due date.
- Record rental dates, due dates, return dates, charges, fines, and rental status.
- Update book availability when a book is rented or returned.
- Prevent a rental from being returned more than once.

## Technologies Used

- **Java** — application logic and object-oriented programming.
- **Java Swing** — graphical user interface.
- **FlatLaf** — interface styling.
- **MySQL** — relational database.
- **JDBC** — communication between Java and MySQL.
- **Eclipse IDE** — development environment.

## Object-Oriented Design

The project uses object-oriented programming concepts such as encapsulation, classes, objects, and separation of responsibilities across model, DAO, database, and view packages.

The **Singleton design pattern** is used for database connection management through the `DBConnection` class.

## Requirements

- Java Development Kit (JDK 21)
- MySQL Server 8.0
- MySQL Connector/J JDBC driver
- Eclipse IDE 
- FlatLaf library

## Database Setup

1. Start MySQL Server.
2. Open MySQL Workbench or another MySQL client.
3. Open and execute `bookstore_hackathon_db.sql`.
4. Confirm that the database `bookstore_hackathon_db` and its five tables are created:
   - `books`
   - `customers`
   - `purchases`
   - `purchase_items`
   - `rentals`

## Configuration

Configure the database connection in `database/DBConnection.java` to use:

- **Database:** `bookstore_hackathon_db`
- **Host:** `127.0.0.1`
- **Port:** `3306`

Set the database password using the environment variable expected by `DBConnection.java`. 
Ensure that MySQL Connector/J and FlatLaf are available on the project's classpath or module path.

## Running the Application

1. Download this repository.
2. Import the project into Eclipse.
3. Configure the JDK and required libraries.
4. Set up the MySQL database and connection credentials.
5. Run the main class containing the application's `main()` method.
6. Use the dashboard to manage books, customers, purchases, and rentals.

## Rental Pricing

| Item | Rate |
|---|---:|
| Rental charge | ₹20 per actual day kept |
| Minimum rental charge | ₹20 |
| Overdue fine | ₹10 per day after the due date |

The selected rental duration determines the due date. The final rental charge is calculated when the book is returned.



