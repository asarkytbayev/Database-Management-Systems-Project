# OrderManager: A Database for Managing Products and Orders

## Table of Contents
* [Overview](#overview)
* [Team](#team)
* [Technology](#technology)
* [Schema](#schema)
* [Deliverables](#deliverables)
* [Build](#build)
* [Tests](#tests)
* [Philosophy](#philosophy)
* [Todo](#todo)



## Overview
OrderManager is an e-commerce program that enables a business to manage information about products that can be sold to customers, to track current inventories of products, and to process orders for products from customers. 

The RDBMS maintains information about products that can be ordered by costumers, tracks inventory levels of each product, and handles orders for product by customers. 





## Team 
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

- **TestOrderManager.java** - This program tests the version of the manager database tables for OrderManager that uses relation table for OrderRecord relation. The sample data is stored in five tab-separated data files. 

- **Helper.java** - This file contains stored functions for parsing, validating, and converting OrderManager types to strings.

- **Util.java** - This file contains functions for printing Product, InventoryRecord, TheOrder, and OrderRecord in the database. 
  
### Data Sets
- **products.txt** - This file contains products data for testing purpose.
  
- **customer1.txt** - This file contains data of the first order from customer Azamat Sarkytbayev for testing purpose.

- **customer2.txt** - This file contains data of order from customer Philip Gust for testing purpose.

- **customer3.txt** - This file contains data of order from customer Yitong Hu for testing purpose.

- **customer4.txt** - This file contains data of the second order from customer Azamat Sarkytbayev for testing purpose.

## Build

### Setup

In order to build OrderManager, you need to have the Java Development Kit (JDK) and the Apache Derby software. See [installation tutorial](https://db.apache.org/derby/papers/DerbyTut/install_software.html) for more info.

Clone or download a copy of OrderManager to your chosen location, open it in local development environment.

After installation, embed Apache Derby in this Java project. In your Java Build Path, add derby.jar and derbyclient.jar files as external libraries from the Derby directory.

### Run

Run OrderManager.java as Java Application.

### Sample Output

```bash
Connected to and created database orderManager

# drop the database triggers, tables, and stored functions, recreate them below
Dropped trigger OrderRecordInsert
Dropped table OrderRecord
Dropped table TheOrder
Dropped table Customer
Dropped table InventoryRecord
Dropped table Product
Dropped stored function isSKU()

Created stored function isSKU()
Created entity table Product
Created entity table InventoryRecord
Created entity table Customer
Created relation table TheOrder
Created relation table OrderRecord
Created trigger for inserting OrderRecord
```


## Tests

### Stored function

The database has one stored function for the purpose of validating product sku type. Before the code that adds values to the database, create tests that invoke the function using a values query with representative value from data file, as well as with invalid value.

### CHECK constraint evaluation

The database has three tables containing CHECK constraint. Product table has a CHECK with SKU column using the isSKU stored function, InventoryRecord table has CHECK with both price column and number column, and TheOrder table has CHECK with both orderDate column and shipmentDate column.

If the value being added to an attribute of a tuple violates the CHECK constraint, the check constraint evaluates to false. The corresponding database update is aborted, and a SQLException is thrown.

```bash
try {
   ...
} catch (SQLException ex) {
   ...
   # print error message
   if (SQLState.LANG_CHECK_CONSTRAINT_VIOLATED.equals(ex.getSQLState())) { 
      System.err.printf("SKU '%s': %s\n", sku, ex.getMessage());
   }
   continue;
}
```
### Auto-increment Handling

Customer table and TheOrder table both use an auto-increment field as a gensym. According to the [MySQL Documentation](https://dev.mysql.com/doc/refman/8.0/en/innodb-auto-increment-handling.html) (the same rule also applies to all database products), it is expected that if a transaction that generated auto-increment values rolls back, those auto-increment values are not reused, thus leaving gaps in the values stored in an auto-increment column of a table.

OrderManager solves the issue by resetting the auto-increment column value. For example, for Customer table, id is the auto-increment field with value customerId failing to be rolled back. Use the SQL syntax "ALTER TABLE Customer ALTER COLUMN id RESTART WITH ":
```bash
try {
   String restart = "ALTER TABLE Customer ALTER COLUMN id RESTART WITH " + customerId;
   stmt.executeUpdate(restart);
   System.out.println("Reset Customer id column");
} catch (SQLException e) {
   e.printStackTrace();
}
```
The same logic is also executed in TheOrder table.

Before any values are added to the database, reset the auto-increment field values in Customer and TheOrder tables for check purpose.

### Populating Product and InventoryRecord tables

* Use one while loop to read all the lines in products.txt
* Commit insertions
* Print Product and InventoryRecord tables

Sample Output
```bash
Products:
SKU             	 Name          Description     
AB-123456-0N    	 Keyboard      Typewriter-style device
DC-835790-AB    	 Monitor       Output display device
EE-345987-30    	 Mouse         Input device moves cursor
OU-436713-X2    	 Cable         Wires covered in a plastic
YW-968406-5T    	 Processor     IC drives the computer

InventoryRecord:
SKU             	 Number        Price           
AB-123456-0N    	 100	          15.56
DC-835790-AB    	 100	          30.00
EE-345987-30    	 100	          3.24
OU-436713-X2    	 100	          5.78
YW-968406-5T    	 100	          399.99
```
### Populating Customer, TheOrder and OrderRecord tables
* Use one while loop to read all the lines in products.txt
* Commit insertions
* Print Product and InventoryRecord tables
Sample Output
```bash
```




```bash

```

## Philosophy


More info: 


More info: 
possible Alternatives
The raw data -> The tidy data set
