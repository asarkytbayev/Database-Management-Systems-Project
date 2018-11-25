import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * This program creates a manager database for the ER data model
 * for OrderManager. There are entity tables for Product, Customer,
 * and InventoryRecord, and relationship tables for TheOrder and
 * OrderRecord relations in the ER model. 
 *
 * 
 *
 * @author lily hu
 */
public class OrderManager {
	
	public static void main(String[] args) {
		// the default framework is embedded
	    String protocol = "jdbc:derby:";
	    String dbName = "manager";
		String connStr = protocol + dbName+ ";create=true";
		
		// triggers created by this program
		String dbTriggers[] = {
			"OrderRecordInsert"
		};

	    // tables created by this program
		String dbTables[] = {
			"TheOrder", "OrderRecord",		// relations
			"Product", "Customer", "InventoryRecord" 	 // entities
    	    };

	    // functions created by this program
	    String dbFunctions[] = {
	    		"isSKU" // for check purpose
	    };

		Properties props = new Properties(); // connection properties
        // providing a user name and password is optional in the embedded
        // and derbyclient frameworks
        props.put("user", "user2");
        props.put("password", "user2");

		try (
	        // connect to the database using URL
			Connection conn = DriverManager.getConnection(connStr, props);

	        // statement is channel for sending commands thru connection
	        Statement stmt = conn.createStatement();
		){
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
	            		System.out.println("Dropped function " + fn);
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
            		"	'Helper.isSKU'"; 
            stmt.executeUpdate(createFunction_isSKU);
            System.out.println("Created function isSKU()");
            
//            // create parseNumber function
//            String createFunction_parseNumber =
//            		"CREATE FUNCTION parseNumber(" + 
//            		"	s VARCHAR(64)" +
//            		") RETURNS INT" +
//            		" PARAMETER STYLE JAVA" +
//            		" LANGUAGE JAVA" +
//            		" DETERMINISTIC" +
//            		" NO SQL" +
//            		" EXTERNAL NAME " +
//            		"	'Helper.parseNumber'"; 
//            stmt.executeUpdate(createFunction_parseNumber);
//            System.out.println("Created function parseNumber()");
//            
//            // create parsePrice function
//            String createFunction_parsePrice =
//            		"CREATE FUNCTION parsePrice(" + 
//            		"	s VARCHAR(64)" +
//            		") RETURNS DECIMAL(18,2)" +
//            		" PARAMETER STYLE JAVA" +
//            		" LANGUAGE JAVA" +
//            		" DETERMINISTIC" +
//            		" NO SQL" +
//            		" EXTERNAL NAME " +
//            		"	'Helper.parsePrice'"; 
//            stmt.executeUpdate(createFunction_parsePrice);
//            System.out.println("Created function parsePrice()");
            
            
            // create the Product table with SKU validation
            String createTable_Product =
            		  "CREATE TABLE Product ("
            		+ "  name VARCHAR(32) NOT NULL,"
            		+ "  description VARCHAR(64) NOT NULL,"
            		+ "  SKU CHAR(12) NOT NULL," 
            		+ "  PRIMARY KEY (SKU),"
            		+ "  CHECK (isSKU(SKU))," 
            		+ ")";
            stmt.executeUpdate(createTable_Product);
            System.out.println("Created entity table Product");

            // create the InventoryRecord table
            String createTable_InventoryRecord =
            		  "CREATE TABLE InventoryRecord ("
            		+ "  number INT NOT NULL,"
            		+ "  price DECIMAL(18,2) NOT NULL," 
            		+ "  productSKU CHAR(12) NOT NULL," 
            		+ "  PRIMARY KEY (productSKU),"
            		+ "  FOREIGN KEY (productSKU) REFERENCES Product (SKU) ON DELETE CASCADE,"
            		+ "  CHECK (price > 0)" // check
            		+ ")";
            stmt.executeUpdate(createTable_InventoryRecord);
            System.out.println("Created entity table InventoryRecord"); 

            // create the Customer table
            String createTable_Customer =
            		  "CREATE TABLE Customer ("
            		+ "  id INT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),"
            		+ "  name VARCHAR(20) NOT NULL,"
            		+ "  address VARCHAR(64) NOT NULL,"
            		+ "  city VARCHAR(16) NOT NULL,"
            		+ "  state VARCHAR(16) NOT NULL,"
            		+ "  country VARCHAR(16) NOT NULL,"
            		+ "  zipCode INT NOT NULL,"
            		+ "  PRIMARY KEY (id),"
            		+ ")";
            stmt.executeUpdate(createTable_Customer);
            System.out.println("Created entity table Customer");

            // create the TheOrder relation table
            String createTable_TheOrder =
            		  "CREATE TABLE TheOrder ("
                + "  id INT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),"
            		+ "  customerId INT NOT NULL,"
            		+ "  orderDate DATE NOT NULL,"
            		+ "  shipmentDate DATE," // can be null
            		+ "  PRIMARY KEY (id),"
                + "  FOREIGN KEY (customerId) REFERENCES Customer (id) ON DELETE CASCADE" // check
            		+ ")";
            stmt.executeUpdate(createTable_TheOrder);
            System.out.println("Created relation table TheOrder"); 

            // create the OrderRecord relation table
            String createTable_OrderRecord =
            		  "CREATE TABLE OrderRecord ("
            		+ "  orderId INT NOT NULL,"
            		+ "  productSKU CHAR(12) NOT NULL,"
            		+ "  number INT NOT NULL,"
                + "  inventoryPrice DECIMAL(18,2) NOT NULL," 
        		    + "  PRIMARY KEY (orderId, productSKU),"
                + "  FOREIGN KEY (orderId) REFERENCES Order (id),"
                + "  FOREIGN KEY (productSKU) REFERENCES Product (SKU) ON DELETE CASCADE,"
                + "  FOREIGN KEY (inventoryPrice) REFERENCES InventoryRecord (price) ON DELETE CASCADE"
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
            
            // create trigger for deleting an order record that also updates (adds back) 
            // number of units available for the InventoryRecord. The deletion of 
            // an order record is caused by deletion on TheOrder (suppose the 
            // customer cancels one of his orders // 反向trigger? check 
			String createTrigger_OrderRecordDelete =
         		  "CREATE TRIGGER OrderRecordDelete"
         		+ " AFTER DELETE ON OrderRecord"
         		+ " REFERENCING OLD AS DeletedOrderRecord"
	    			+ " FOR EACH ROW MODE DB2SQL"
	            	+ "   UPDATE InventoryRecord SET InventoryRecord.number = InventoryRecord.number + DeletedOrderRecord.number"
	            	+ "   WHERE InventoryRecord.productSKU = DeletedOrderRecord.productSKU";
			stmt.executeUpdate(createTrigger_OrderRecordDelete);
            System.out.println("Created trigger for deleting OrderRecord");
            
            // create trigger for inserting an order record whose number of item
            // is more than number of units available for the InventoryRecord, 
            // thus resulting in the insertion failure, and the order itself 
            // also gets canceled automatically. // check
            String createTrigger_OrderRecordOverflow =
           		  "CREATE TRIGGER OrderRecordOverflow"
           		+ " BEFORE INSERT ON OrderRecord"
           		+ " REFERENCING NEW AS N_ROW"
  	    			+ " FOR EACH ROW MODE DB2SQL"
  	    			+ " BEGIN"
  	    			+ " IF N_ROW.number > InventoryRecord.number THEN" // check: MySQL syntax
  	    			+ " DELETE FROM TheOrder WHERE id = N_ROW.orderId"
  	            	+ "   " // check: how to cancel the insertion itself?
  	            	+ " END IF;"
  	            	+ " END;";
  			stmt.executeUpdate(createTrigger_OrderRecordOverflow);
            System.out.println("Created trigger for inserting OrderRecord"); // 
              
            
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
