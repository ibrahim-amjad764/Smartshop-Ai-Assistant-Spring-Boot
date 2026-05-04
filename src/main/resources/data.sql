-- Create database
CREATE DATABASE IF NOT EXISTS smartshop;
USE smartshop;

-- Create tables
CREATE TABLE IF NOT EXISTS product (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    brand VARCHAR(100) NOT NULL,
    model VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS store (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    api_url VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS offer (
    id INT AUTO_INCREMENT PRIMARY KEY,
    product_id INT NOT NULL,
    store_id INT NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    url VARCHAR(255),
    available BOOLEAN DEFAULT TRUE,
    fetched_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (product_id) REFERENCES product(id),
    FOREIGN KEY (store_id) REFERENCES store(id)
);

-- Insert sample products
INSERT INTO product (title, brand, model) VALUES
('iPhone 15', 'Apple', 'A3100'),
('iPhone 14', 'Apple', 'A2900'),
('iPhone 13', 'Apple', 'A2700'),
('Galaxy S24', 'Samsung', 'SM-G991B'),
('Galaxy S23', 'Samsung', 'SM-G990B'),
('Galaxy S22', 'Samsung', 'SM-G980B'),
('Pixel 8', 'Google', 'G8-001'),
('Pixel 7', 'Google', 'G7-002'),
('OnePlus 12', 'OnePlus', 'OP12-001'),
('OnePlus 11', 'OnePlus', 'OP11-002');

-- Insert sample stores
INSERT INTO store (name, api_url) VALUES
('Daraz', 'https://daraz.pk/api'),
('MegaMall', 'https://megamall.pk/api'),
('ShopHive', 'https://shophive.pk/api'),
('TechBazaar', 'https://techbazaar.pk/api');

-- Insert sample offers
INSERT INTO offer (product_id, store_id, price, url, available, fetched_at) VALUES
(1, 1, 245000, 'https://daraz.pk/iphone15', true, NOW()),
(2, 1, 185000, 'https://daraz.pk/iphone14', true, NOW()),
(3, 1, 150000, 'https://daraz.pk/iphone13', true, NOW()),
(4, 2, 220000, 'https://megamall.pk/samsungS24', true, NOW()),
(5, 2, 200000, 'https://megamall.pk/samsungS23', true, NOW()),
(6, 2, 180000, 'https://megamall.pk/samsungS22', true, NOW()),
(7, 3, 150000, 'https://shophive.pk/pixel8', true, NOW()),
(8, 3, 120000, 'https://shophive.pk/pixel7', true, NOW()),
(9, 4, 140000, 'https://techbazaar.pk/op12', true, NOW()),
(10, 4, 130000, 'https://techbazaar.pk/op11', true, NOW());

-- Optional: more variations for testing budget filters
INSERT INTO offer (product_id, store_id, price, url, available, fetched_at) VALUES
(1, 2, 240000, 'https://megamall.pk/iphone15', true, NOW()),
(2, 3, 190000, 'https://shophive.pk/iphone14', true, NOW()),
(3, 4, 155000, 'https://techbazaar.pk/iphone13', true, NOW());
