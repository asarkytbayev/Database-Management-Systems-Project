import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.sql.Savepoint;

import org.apache.derby.shared.common.reference.SQLState; // !  check

/**
 * This program tests the version of the manager database tables for OrderManager
 * that uses relation tables for the TheOrder and OrderRecord relations. The sample
 * data is stored in two tab-separated data files. The columns of file data1 are:
 * name, description, SKU, number, price. The columns of file data2 are: 
 * productName, SKU, price, customerName, address, city, state, country, zipCode, 
 * orderDate, shipmentDate, number bought. 
 *
 * @author Yitong Hu
 */

public class TestOrderManager {

	public static void main(String[] args) {
		// the default framework is embedded
	    String protocol = "jdbc:derby:";
	    String dbName = "publication1";
		String connStr = protocol + dbName+ ";create=true";

	    // tables tested by this program
		String dbTables[] = {
			"TheOrder", "OrderRecord",		// relations
			"Product", "Customer", "InventoryRecord" 	 // entities
    	    };

		// name of data file
		String fileName1 = "data1.txt"; 
		String fileName2 = "data2.txt"; 

		Properties props = new Properties(); // connection properties
        // providing a user name and password is optional in the embedded
        // and derbyclient frameworks
        props.put("user", "user2");
        props.put("password", "user2");

        // result set for queries
        ResultSet rs = null;
        try (
        		// open data file
    			BufferedReader br1 = new BufferedReader(new FileReader(new File(fileName1)));
        		BufferedReader br2 = new BufferedReader(new FileReader(new File(fileName2)));

    			// connect to database
    			Connection  conn = DriverManager.getConnection(connStr, props);
    			Statement stmt = conn.createStatement();
        		
    			// insert prepared statements
        		PreparedStatement insertRow_Product = conn.prepareStatement(
    					"INSERT INTO Product VALUES(?, ?, ?)");
        		PreparedStatement insertRow_InventoryRecord = conn.prepareStatement(
    					"INSERT INTO InventoryRecord VALUES(parseNumber(?), parsePrice(?), ?)");
        		PreparedStatement insertRow_Customer = conn.prepareStatement(
    					"INSERT INTO Customer VALUES(?, ?, ?, ?, ?, ?)");
        		PreparedStatement insertRow_TheOrder = conn.prepareStatement(
    					"INSERT INTO TheOrder VALUES(?, ?, ?)");
        		PreparedStatement insertRow_OrderRecord = conn.prepareStatement(
    					"INSERT INTO OrderRecord VALUES(?, ?, parseNumber(?), parsePrice(?))");
        	) {
        		// connect to the database using URL
            System.out.println("Connected to database " + dbName);

            // clear data from tables
            for (String tbl : dbTables) {
	            try {
	            		stmt.executeUpdate("DELETE FROM " + tbl);
	            		System.out.println("Truncated table " + tbl);
	            } catch (SQLException ex) {
	            		System.out.println("Did not truncate table " + tbl);
	            }
            }
            
            // turn off auto-commit
            // transactions not committed will be aborted.
            conn.setAutoCommit(false);
            
            // testing stored functions
            System.out.println("\nTesting stored functions");
            
            // test isSKU function
            // invalid SKU
		    try {
		      rs = stmt.executeQuery("VALUES isSKU('AB-1456-0N')");
		      rs.next(); 
		  	  boolean is_sku = rs.getBoolean(1);
			  if (is_sku) {
				System.out.printf("SKU 'AB-1456-0N' is valid\n");
			  } else {
				System.out.printf("SKU 'AB-1456-0N' is not valid\n");
			  }
			  rs.close();
		    } catch (SQLException ex) {
		      System.out.printf("For SKU 'AB-1456-0N', error calling isSKU: %s\n", ex.getMessage());
		    }
		    // valid SKU
		    try {
		        rs = stmt.executeQuery("VALUES isSKU('AB-123456-0N')");
			    rs.next(); 
			  	boolean is_sku = rs.getBoolean(1);
				if (is_sku) {
					System.out.printf("SKU 'AB-123456-0N' is valid\n");
				} else {
					System.out.printf("SKU 'AB-123456-0N' is not valid\n");
				}
				rs.close();
		    } catch (SQLException ex) {
		      System.out.printf("For SKU 'AB-123456-0N', error calling isSKU: %s\n", ex.getMessage());
		    }
		    	
		    // test parseNumber function
		    // parse invalid number string
            try {
              rs = stmt.executeQuery("VALUES parseNumber('d78')"); 
              rs.next(); // this step throws exception
              int number = rs.getInt(1);
			  System.out.printf("value of number string 'd78' is %d\n", number);
          	  rs.close();
            } catch (SQLException ex) {
              System.out.printf("For number 'd78', error calling parseNumber: %s\n", ex.getMessage());
            }
            // parse valid number string: representative value from the data file
            PreparedStatement invoke_parseNumber = conn.prepareStatement("VALUES parseNumber(?)");
            try {
            	  invoke_parseNumber.setString(1, "231");
      		  rs = invoke_parseNumber.executeQuery();
      		  if (rs.next()) {
      			int number = rs.getInt(1);
      			System.out.printf("value of number string '231' is %d\n", number); 
      		  }
      		  rs.close();
              invoke_parseNumber.close();
            } catch (SQLException ex) {
              System.out.printf("For number '231', error calling parseNumber: %s\n", ex.getMessage());
            }
		    
            // test parsePrice function
		    // parse invalid price string
            try {
              rs = stmt.executeQuery("VALUES parsePrice('34.21bh')"); 
              rs.next(); // this step throws exception
              double price = rs.getDouble(1);
			  System.out.printf("value of price string '34.21bh' is %d\n", price);
          	  rs.close();
            } catch (SQLException ex) {
              System.out.printf("For price '34.21bh', error calling parsePrice: %s\n", ex.getMessage());
            }
            // parse valid price string: representative value from the data file
            PreparedStatement invoke_parsePrice = conn.prepareStatement("values parsePrice(?)");
            try {
            	  invoke_parsePrice.setString(1, "123.90");
      		  rs = invoke_parsePrice.executeQuery();
      		  if (rs.next()) {
      			double price = rs.getDouble(1); // ! 
      			System.out.printf("value of price string '123.90' is: %.2f\n", price);  // !
      		  }
        		  rs.close();
        		  invoke_parsePrice.close();
            } catch (SQLException ex) {
              System.out.printf("For price '123.90', error calling parsePrice: %s\n", ex.getMessage());
            }
            // alternative
//            try {
//      		  rs = stmt.executeQuery("VALUES parsePrice('123.90')");
//      		  rs.next();
//      		  double price = rs.getDouble(1);  
//      		  System.out.printf("value of price string '123.90' is: %.2f\n", price);  
//        		  rs.close();
//            } catch (SQLException ex) {
//              System.out.printf("For price '123.90', error calling parsePrice: %s\n", ex.getMessage());
//            }
            
            
            String line;
            
			while ((line = br1.readLine()) != null) {
				// split input line into fields at tab delimiter
				String[] data = line.split("\t");
				if (data.length != 5) continue;

		        // get fields from input data
				String productName = data[0];
				String productDescription = data[1];
		        String sku = data[2];
		        String inventoryString = data[3];
		        String priceString = data[4];

				// add Product if does not exist
				try {
					insertRow_Product.setString(1, productName);
					insertRow_Product.setString(2, productDescription);
					insertRow_Product.setString(3, sku);
					insertRow_Product.execute();
				} catch (SQLException ex) {
					// already exists
					 System.err.printf("Unable to insert Product %s\n", productName);
					 continue;
				}

				// add InventoryRecord if does not exist
				try {
					insertRow_InventoryRecord.setString(1, inventoryString);
					insertRow_InventoryRecord.setString(2, priceString);
					insertRow_InventoryRecord.setString(3, sku);
					insertRow_InventoryRecord.execute();
				} catch (SQLException ex) {
					if (SQLState.LANG_CHECK_CONSTRAINT_VIOLATED.equals(ex.getSQLState())) { // check
						System.err.printf("SKU '%s': %s\n", sku, ex.getMessage());
					} else {
						// already exists
						System.err.printf("Unable to insert InventoryRecord sku %s\n", sku);
						continue;
					}
				}
			}
            
			while ((line = br2.readLine()) != null) {
				// split input line into fields at tab delimiter
				String[] data = line.split("\t");
				if (data.length != 12) continue; 
				
				// get fields from input data
				String productName = data[0];
				String sku = data[1];
				String price = data[2];
				String customerName = data[3];
				String address = data[4];
				String city = data[5];
				String state = data[6];
				String country = data[7];
				String zipCode = data[8];
				String orderDate = data[9];
				String shipmentDate = data[10];
				String numberBought = data[11];
				
				// add Customer if does not exist
				try {
					insertRow_Customer.setString(1, customerName);
					insertRow_Customer.setString(2, address);
					insertRow_Customer.setString(3, city);
					insertRow_Customer.setString(4, state);
					insertRow_Customer.setString(5, country);
					insertRow_Customer.setString(6, zipCode);
					insertRow_Customer.execute();
				} catch (SQLException ex) {
					// already exists
					 System.err.printf("Unable to insert Customer %s\n", customerName); 
				}
				
				// add TheOrder if does not exist
				try {
					rs = stmt.executeQuery("SELECT id FROM Customer");
	    				if (rs.next()) {
	    					int customerId = rs.getInt(1);
	    					insertRow_TheOrder.setInt(1, customerId); // check
	    					insertRow_TheOrder.setString(2, orderDate); 
	    					insertRow_TheOrder.setString(3, shipmentDate); 
	    					insertRow_TheOrder.execute();
	    				}
	    				rs.close();
				} catch (SQLException ex) { 
					// already exists
					// System.err.printf("Unable to insert TheOrder\n");
				}
				
				// add OrderRecord if does not exist
				try {
					rs = stmt.executeQuery("SELECT id FROM TheOrder");
	    				if (rs.next()) {
	    					int orderId = rs.getInt(1);
	    					insertRow_OrderRecord.setInt(1, orderId); // check
	    					insertRow_OrderRecord.setString(2, sku);
	    					insertRow_OrderRecord.setString(3, numberBought);
	    					insertRow_OrderRecord.setString(4, price);
	    					insertRow_OrderRecord.execute();
	    				}
	    				rs.close();
				} catch (SQLException ex) { 
					// already exists
					// System.err.printf("Unable to insert OrderRecord\n"); 
				}
			}
			
			
			System.out.println("Committing insertions.");
			conn.commit();
			// print number of rows in tables
			for (String tbl : dbTables) {
				rs = stmt.executeQuery("SELECT count(*) FROM " + tbl);
				if (rs.next()) {
					int count = rs.getInt(1);
					System.out.printf("Table %s : count: %d\n", tbl, count);
				}
			}
			rs.close();
		
			
			// scenario 1: delete product chili jam: if never stock 
			System.out.println("\nDeleting product chili jam");
			stmt.executeUpdate("DELETE FROM Product WHERE name = 'chili jam'");
			Util.printProduct(conn);
			Util.printInventoryRecord(conn);
			Util.printTheOrder(conn);
			Util.printOrderRecord(conn);
			
			System.out.println("Rolling back delete product chili jam.");
			conn.rollback();
			Util.printProduct(conn);
			Util.printInventoryRecord(conn);
			Util.printTheOrder(conn);
			Util.printOrderRecord(conn);
			
			Savepoint sp1 = conn.setSavepoint("Inserts");
    
			// scenario 2: delete order: if customer cancels the order proactively // check
			rs = stmt.executeQuery("SELECT id FROM TheOrder");
			rs.next();
			int orderId = rs.getInt(1);
			
			System.out.printf("\nAza deletes one of his orders, order id is: %d\n", orderId); // check  
			stmt.executeUpdate("DELETE FROM TheOrder WHERE id = " + orderId); // check
			Util.printInventoryRecord(conn);
			Util.printTheOrder(conn);
			Util.printOrderRecord(conn);
			
			System.out.printf("Rolling back delete Aza's order, order id is: %d\n", orderId); // check
			conn.rollback(sp1);
			Util.printInventoryRecord(conn);
			Util.printTheOrder(conn);
			Util.printOrderRecord(conn);
			
			// scenario 3: if the commodity ordered by customer is out of inventory, check: insert one more time?
			
			
			System.out.println("Committing transaction.");
			conn.commit();
			
			// restore auto-commit
			conn.setAutoCommit(true);
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
