# OrderManager: A Database for Managing Products and Orders

## Table of Contents
* [Description](#description)
* [Team](#team)
* [Schema](#schema)


## Description
OrderManager is an e-commerce program that enables a business to manage information about products that can be sold to customers, to track current inventories of products, and to process orders for products from customers. 

The RDBMS maintains information about products that can be ordered by costumers, tracks inventory levels of each product, and handles orders for product by customers. 
[Learn more](http://www.ccis.northeastern.edu/home/pgust/classes/cs5200/2018/Fall/projects.html)


## Team
Azamat Sarkytbayev    sarkytbayev.a@husky.neu.edu

Yitong Hu   hu.yit@husky.neu.edu


## Schema
**tables**

| Tables        | Classification           | 
|:-------------:|:-------------:|
| Product       | entity | 
| InventoryRecord      | entity      | 
| Customer | entity      |  
| TheOrder | relation     |    
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

**descriptions of the files**

