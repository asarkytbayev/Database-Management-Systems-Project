
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;



/**
 * This program creates a manager database for the ER data model
 * for OrderManager. 
 * There are entity tables for Product, Customer, TheOrder
 * and InventoryRecord, and relationship tables for 
 * OrderRecord relations in the ER model. 
 *
 * 
 * @author Azamat Sarkytbayev
 * @author Yitong Hu
 */
public class OrderManager {
	
	public static void main(String[] args) {
		// the default framework is embedded
	    String protocol = "jdbc:derby:";
	    String dbName = "orderManager";
		String connStr = protocol + dbName+ ";create=true";
		
		// triggers created by this program
		String[] dbTriggers = {
			"OrderRecordInsert"
		};

	    // tables created by this program
		String[] dbTables = {
			"OrderRecord", "TheOrder",
			"Customer", "InventoryRecord", "Product"
	    };

	    // stored functions created by this program
	    String[] dbFunctions = {
	    		"isSKU" // for check purpose
	    };

		Properties props = new Properties(); // connection properties
        // providing a user name and password is optional in the embedded
        // and derbyclient frameworks
        props.put("user", "user1");
        props.put("password", "user1");

		try (
	        // connect to the database using URL
			Connection conn = DriverManager.getConnection(connStr, props);

	        // statement is channel for sending commands thru connection
	        Statement stmt = conn.createStatement();
		) {
	        System.out.println("Connected to and created database " + dbName);
	        
	        // drop the database triggers and recreate them below
            for (String tgr : dbTriggers) {
	            try {
	            		stmt.executeUpdate("DROP TRIGGER " + tgr);
	            		System.out.println("Dropped trigger " + tgr);
	            } catch (SQLException ex) {
	            		System.out.println("Did not drop trigger " + tgr);
	            }
            }

            // drop the database tables and recreate them below
            for (String tbl : dbTables) {
	            try {
	            		stmt.executeUpdate("DROP TABLE " + tbl);
	            		System.out.println("Dropped table " + tbl);
	            } catch (SQLException ex) {
	            		System.out.println("Did not drop table " + tbl);
	            }
            }

            // drop the database functions and recreate them below
            for (String fn : dbFunctions) {
	            try {
	            		stmt.executeUpdate("DROP FUNCTION " + fn);
	            		System.out.println("Dropped stored function " + fn + "()");
	            } catch (SQLException ex) {
	            		System.out.println("Did not drop function " + fn);
	            }
            }
            
            // create isSKU function
            String createFunction_isSKU =
            		"CREATE FUNCTION isSKU(" + 
            		"	sku CHAR(12)" +
            		") RETURNS BOOLEAN" +
            		" PARAMETER STYLE JAVA" +
            		" LANGUAGE JAVA" +
            		" DETERMINISTIC" +
            		" NO SQL" +
            		" EXTERNAL NAME " +
            		"	'edu.northeastern.cs_5200.Helper.isSKU'";
            stmt.executeUpdate(createFunction_isSKU);
            System.out.println("Created stored function isSKU()");
            
            // create the Product table with SKU validation
            String createTable_Product =
            		  "CREATE TABLE Product ("
            		+ "  name VARCHAR(32) NOT NULL,"
            		+ "  description VARCHAR(64) NOT NULL,"
            		+ "  SKU CHAR(12) NOT NULL,"
					+ "  CONSTRAINT PK_SKU"
            		+ "  PRIMARY KEY (SKU),"
					+ "  CONSTRAINT CHK_SKU"
            		+ "  CHECK (isSKU(SKU))" 
            		+ ")";
            stmt.executeUpdate(createTable_Product);
            System.out.println("Created entity table Product");

            // create the InventoryRecord table
            String createTable_InventoryRecord =
            		  "CREATE TABLE InventoryRecord ("
            		+ "  number INT NOT NULL,"
            		+ "  price DECIMAL(18,2) NOT NULL," 
            		+ "  productSKU CHAR(12) NOT NULL,"
					+ "  CONSTRAINT PK_PRODUCT_SKU"
            		+ "  PRIMARY KEY (productSKU),"
					+ "  CONSTRAINT FK_PRODUCT_SKU"
            		+ "  FOREIGN KEY (productSKU) REFERENCES Product (SKU) ON DELETE CASCADE,"
            		+ "  CHECK (price >= 0.0 and number >= 0)" 
            		+ ")";
            stmt.executeUpdate(createTable_InventoryRecord);
            System.out.println("Created entity table InventoryRecord"); 

            // create the Customer table
            String createTable_Customer =
            		  "CREATE TABLE Customer ("
            		+ "  id INT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),"
            		+ "  name VARCHAR(32) NOT NULL,"
            		+ "  address VARCHAR(64) NOT NULL,"
            		+ "  city VARCHAR(16) NOT NULL,"
            		+ "  state VARCHAR(16) NOT NULL,"
            		+ "  country VARCHAR(16) NOT NULL,"
            		+ "  postalCode VARCHAR(8) NOT NULL,"
					+ "  CONSTRAINT PK_CUSTOMER_ID"
            		+ "  PRIMARY KEY (id),"
					+ "  CONSTRAINT U_DUPLICATES"
					+ "  UNIQUE(name, address, city, state, country, postalCode)"
            		+ ")";
            stmt.executeUpdate(createTable_Customer);
            System.out.println("Created entity table Customer");

            // create the TheOrder table
            String createTable_TheOrder =
            		  "CREATE TABLE TheOrder ("
            		+ "  id INT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),"
            		+ "  customerId INT NOT NULL,"
            		+ "  orderDate DATE NOT NULL,"
            		+ "  shipmentDate DATE," // can be null
					+ "  CONSTRAINT PK_ORDER_ID"
            		+ "  PRIMARY KEY (id),"
					+ "  CONSTRAINT FK_CUSTOMER_ID"
					+ "  FOREIGN KEY (customerId) REFERENCES Customer (id),"
					+ "  CONSTRAINT CHK_DATES"
					+ "  CHECK (shipmentDate is null or orderDate <= shipmentDate)"
            		+ ")";
            stmt.executeUpdate(createTable_TheOrder);
            System.out.println("Created relation table TheOrder"); 

            // create the OrderRecord relation table
            String createTable_OrderRecord =
            		  "CREATE TABLE OrderRecord ("
            		+ "  orderId INT NOT NULL,"
            		+ "  number INT NOT NULL,"
            		+ "  inventoryPrice DECIMAL(18,2) NOT NULL," 
            		+ "  productSKU CHAR(12) NOT NULL,"
					+ "  CONSTRAINT PK_ORDER_RECORD_ID"
        		    + "  PRIMARY KEY (orderId, productSKU),"
					+ "  CONSTRAINT FK_ORDER_ID"
					+ "  FOREIGN KEY (orderId) REFERENCES TheOrder (id),"
					+ "  FOREIGN KEY (productSKU) REFERENCES Product (SKU) ON DELETE CASCADE"
            		+ ")";
            stmt.executeUpdate(createTable_OrderRecord);
            System.out.println("Created relation table OrderRecord"); 
            
            
            // create trigger for inserting an order record that also deletes
            // number of units available for the InventoryRecord
			String createTrigger_OrderRecordInsert =
         		  "CREATE TRIGGER OrderRecordInsert"
         		+ " AFTER INSERT ON OrderRecord"
         		+ " REFERENCING NEW AS N_ROW"
	    			+ " FOR EACH ROW MODE DB2SQL"
	            	+ "   UPDATE InventoryRecord SET InventoryRecord.number = InventoryRecord.number - N_ROW.number"
	            	+ "   WHERE InventoryRecord.productSKU = N_ROW.productSKU";
			stmt.executeUpdate(createTrigger_OrderRecordInsert);
            System.out.println("Created trigger for inserting OrderRecord");
            
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
