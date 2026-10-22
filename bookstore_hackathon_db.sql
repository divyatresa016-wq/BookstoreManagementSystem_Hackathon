
CREATE DATABASE IF NOT EXISTS bookstore_hackathon_db;
USE bookstore_hackathon_db;

-- 1. Books table
CREATE TABLE IF NOT EXISTS books (
    book_id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    author VARCHAR(255) NOT NULL,
    category VARCHAR(100),
    price DECIMAL(10,2) NOT NULL,
    stock INT NOT NULL DEFAULT 0
);

-- 2. Customers table
CREATE TABLE IF NOT EXISTS customers (
    customer_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE,
    phone VARCHAR(20)
);

-- 3. Purchases table
CREATE TABLE IF NOT EXISTS purchases (
    purchase_id INT AUTO_INCREMENT PRIMARY KEY,
    customer_id INT NOT NULL,
    purchase_date DATE NOT NULL,
    total_amount DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    CONSTRAINT fk_purchase_customer
        FOREIGN KEY (customer_id)
        REFERENCES customers(customer_id)
);

-- 4. Purchase items table
CREATE TABLE IF NOT EXISTS purchase_items (
    item_id INT AUTO_INCREMENT PRIMARY KEY,
    purchase_id INT NOT NULL,
    book_id INT NOT NULL,
    quantity INT NOT NULL,
    unit_price DECIMAL(10,2) NOT NULL,
    CONSTRAINT fk_item_purchase
        FOREIGN KEY (purchase_id)
        REFERENCES purchases(purchase_id),
    CONSTRAINT fk_item_book
        FOREIGN KEY (book_id)
        REFERENCES books(book_id)
);

-- 5. Rentals table
CREATE TABLE IF NOT EXISTS rentals (
    rental_id INT AUTO_INCREMENT PRIMARY KEY,
    customer_id INT NOT NULL,
    book_id INT NOT NULL,
    rental_date DATE NOT NULL,
    due_date DATE NOT NULL,
    return_date DATE DEFAULT NULL,
    rental_days INT NOT NULL,
    rental_charge DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    overdue_fine DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    status ENUM('ACTIVE', 'RETURNED') NOT NULL DEFAULT 'ACTIVE',

    CONSTRAINT fk_rental_customer
        FOREIGN KEY (customer_id)
        REFERENCES customers(customer_id),

    CONSTRAINT fk_rental_book
        FOREIGN KEY (book_id)
        REFERENCES books(book_id),

    CONSTRAINT chk_rental_days
        CHECK (rental_days > 0),

    CONSTRAINT chk_rental_charge
        CHECK (rental_charge >= 0),

    CONSTRAINT chk_overdue_fine
        CHECK (overdue_fine >= 0),

    CONSTRAINT chk_rental_dates
        CHECK (due_date >= rental_date)
);
