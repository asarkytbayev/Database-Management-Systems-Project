# OrderManager: A Database for Managing Products and Orders

## Table of Contents
* [Quick Overview](#quick-overview)
* [Team Members](#team-members)
* [Technology](#technology)
* [Schema](#schema)
* [Deliverables](#deliverables)
* [Building OrderManager](#building-ordermanager)
* [Running the Tests](#running-the-tests)
* [Design Philosophy](#design-philosophy)




## Quick Overview
OrderManager is an e-commerce program that enables a business to manage information about products that can be sold to customers, to track current inventories of products, and to process orders for products from customers. 

The RDBMS maintains information about products that can be ordered by costumers, tracks inventory levels of each product, and handles orders for product by customers. 

Docs



## Team Members
* asarkytbayev - Azamat Sarkytbayev &lt;sarkytbayev.a@husky.neu.edu&gt;
* yitonghu - Yitong Hu &lt;hu.yit@husky.neu.edu&gt;

## Technology
* JavaDB
* SQL

## Schema
**Tables**

| Tables        | Classification           | 
|:-------------:|:-------------:|
| Product       | entity | 
| InventoryRecord      | entity      | 
| Customer | entity      |  
| TheOrder | entity     |    
| OrderRecord | relation     |   


- Product table
 
| Column Name  | Data Type   | 
|:-------------:|:-------------:|
| name       | VARCHAR(32) | 
| description      | VARCHAR(64)      | 
| SKU | CHAR(12)      |  


- InventoryRecord table

| Column Name  | Data Type   | 
|:-------------:|:-------------:|
| number       | INT | 
| price      | DECIMAL(18,2)    | 
| productSKU | CHAR(12)      | 

- Customer table

| Column Name  | Data Type   | 
|:-------------:|:-------------:|
| id       | INT | 
| name      | VARCHAR(20)    | 
| address | VARCHAR(64)     | 
| city       | VARCHAR(16) | 
| state      | VARCHAR(16)   | 
| country | VARCHAR(16)      |
| zipCode | INT    | 

- TheOrder table

| Column Name  | Data Type   | 
|:-------------:|:-------------:|
| id       | INT | 
| customerId      | INT   | 
| orderDate | DATE      | 
| shipmentDate | DATE     | 

- OrderRecord table

| Column Name  | Data Type   | 
|:-------------:|:-------------:|
| orderId       | INT | 
| productSKU      | CHAR(12)   | 
| number | INT      | 
| inventoryPrice | DECIMAL(18,2)     | 

**UML image**

## Deliverables

### Modules

- **OrderManager.java** - This program creates a manager database for the ER data model for OrderManager. There are entity tables for Product, Customer, TheOrder, and InventoryRecord, and relationship table for OrderRecord relation in the ER model.

- **TestOrderManager.java** - This program tests the version of the manager database tables for OrderManager that uses relation tables for the TheOrder and OrderRecord relations. The sample data is stored in two tab-separated data files. The columns of file data1 are: name, description, SKU, number, price. The columns of file data2 are: productName, SKU, price, customerName, address, city, state, country, postalCode, orderDate, shipmentDate, number bought. 

- **Helper.java** - This file contains stored functions for parsing, validating, and converting OrderManager types to strings.

- **Util.java** - This file contains functions for printing Product, InventoryRecord, TheOrder, and OrderRecord in the database. 
  
### Data Sets
- **products.txt** - This file contains products data for testing purpose.
  
- **customer1.txt** - This file contains data of the first order from customer Azamat Sarkytbayev for testing purpose.

- **customer2.txt** - This file contains data of order from customer Philip Gust for testing purpose.

- **customer3.txt** - This file contains data of order from customer Yitong Hu for testing purpose.

- **customer4.txt** - This file contains data of the second order from customer Azamat Sarkytbayev for testing purpose.

## Building OrderManager

## Running the Tests

## Design Philosophy
