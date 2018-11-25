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
 
| name        | description           | SKU  |
|:-------------:|:-------------:|:-----:|

- InventoryRecord table

| number        | price           | productSKU  |
|:-------------:|:-------------:|:-----:|

- Customer table

| id        | name           | address  |city | state | country| zipCode | 
|:-------------:|:-------------:|:-----:|:-----:|:-----:|:-----:|:-----:|

- TheOrder table

| id        | customerId           | orderDate  |shipmentDate |
|:-------------:|:-------------:|:-----:|:-----:|

- OrderRecord table

| orderId        | productSKU           | number  |inventoryPrice |
|:-------------:|:-------------:|:-----:|:-----:|

**UML image**

**descriptions of the files**

