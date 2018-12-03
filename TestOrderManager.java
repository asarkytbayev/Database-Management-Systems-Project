
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
import java.sql.Date;

import org.apache.derby.shared.common.reference.SQLState; 

/**
 * This program tests the version of the manager database tables for OrderManager
 * that uses relation table for OrderRecord relation. The sample
 * data is stored in five tab-separated data files. 
 *
 * @author Yitong Hu
 * @author Azamat Sarkytbayev
 */

public class TestOrderManager {
	
	/**
	 * Runs the program
	 * @param args
	 */
	public static void main(String[] args) {
		// the default framework is embedded
	    String protocol = "jdbc:derby:";
	    String dbName = "orderManager";
		String connStr = protocol + dbName+ ";create=true";

	    // tables tested by this program
		String[] dbTables = {
			"OrderRecord", "TheOrder",
			"Customer", "InventoryRecord", "Product"
	    };

		
		// names of the data files
		String productsFile = "products.txt";
		// customer ordering a single item
		String customerOneFile = "customer1.txt";
		// customer ordering two items
		String customerTwoFile = "customer2.txt";
		// customer ordering three items
		String customerThreeFile = "customer3.txt";
		// customer orders more than in stock - transaction will be rolled back
		String customerFourFile = "customer4.txt";
		// customer with a shipment date earlier than order date - will fail
		String customerFiveFile = "customer5.txt";
		String customerSixFile = "customer6.txt";
		String customerSevenFile = "customer7.txt";
		

		Properties props = new Properties(); // connection properties
        // providing a user name and password is optional in the embedded
        // and derbyclient frameworks
        props.put("user", "user1");
        props.put("password", "user1");

        // result set for queries
        ResultSet rs = null;
        try (
        	// opens data files
			BufferedReader brp = new BufferedReader(new FileReader(new File(productsFile)));
			BufferedReader br1 = new BufferedReader(new FileReader(new File(customerOneFile)));
			BufferedReader br2 = new BufferedReader(new FileReader(new File(customerTwoFile)));
			BufferedReader br3 = new BufferedReader(new FileReader(new File(customerThreeFile)));
			BufferedReader br4 = new BufferedReader(new FileReader(new File(customerFourFile)));
    		BufferedReader br5 = new BufferedReader(new FileReader(new File(customerFiveFile)));
    		BufferedReader br6 = new BufferedReader(new FileReader(new File(customerSixFile)));
    		BufferedReader br7 = new BufferedReader(new FileReader(new File(customerSevenFile)));
			
        	// connects to db
			Connection conn = DriverManager.getConnection(connStr, props);
			Statement stmt = conn.createStatement();
        		
			// insert prepared statements
			PreparedStatement insertRow_Product = conn.prepareStatement("INSERT INTO Product VALUES(?, ?, ?)");
			PreparedStatement insertRow_InventoryRecord = conn.prepareStatement("INSERT INTO InventoryRecord VALUES(?, ?, ?)");
			PreparedStatement insertRow_Customer = conn.prepareStatement("INSERT INTO Customer (name, address, city, state, country, postalCode) VALUES(?, ?, ?, ?, ?, ?)");
			PreparedStatement insertRow_TheOrder = conn.prepareStatement("INSERT INTO TheOrder (customerId, orderDate, shipmentDate) VALUES(?, ?, ?)");
			PreparedStatement insertRow_OrderRecord = conn.prepareStatement("INSERT INTO OrderRecord VALUES(?, ?, ?, ?)");
			PreparedStatement queryRow_Customer = conn.prepareStatement("SELECT id FROM Customer WHERE name = ? AND address = ? AND city = ? AND state = ? AND country = ? AND postalCode = ?");
			PreparedStatement queryRow_TheOrder = conn.prepareStatement("SELECT COUNT(*) FROM TheOrder WHERE id = ? AND customerId = ?");
        ) {
			// connect to the database using URL
			System.out.println("Connected to database " + dbName);
			
			// turn off auto-commit - transactions not committed will be aborted
            conn.setAutoCommit(false);

            // clear data from tables
            for (String tbl : dbTables) {
	            try {
					stmt.executeUpdate("DELETE FROM " + tbl);
					System.out.println("Deleted data from table " + tbl);
	            } catch (SQLException ex) {
					System.out.println("Did not delete data from table " + tbl);
	            }
            }
            
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
            
			// resets the identity column values in Customer & TheOrder tables
			try {
				int value = 1;
				String restart = "ALTER TABLE Customer ALTER COLUMN id RESTART WITH " + value;
				stmt.executeUpdate(restart);
				System.out.println("Reset Customer identity column");
			} catch (SQLException e) {
				System.err.println(e.getMessage());
			}
			
			try {
				int value = 1;
				String restart = "ALTER TABLE TheOrder ALTER COLUMN id RESTART WITH " + value;
				stmt.executeUpdate(restart);
				System.out.println("Reset TheOrder identity column");
			} catch (SQLException e) {
				System.err.println(e.getMessage());
			}			
			
			System.out.println("Inserting data into Product and InventoryRecord tables");
			// populates Product and InventoryRecord tables
            String line;
			while ((line = brp.readLine()) != null) {
				// split input line into fields at tab delimiter
				String[] data = line.split("\\t+");
				if (data.length != 5) continue;

		        // get fields from input data
				String productName = data[0];
				String productDescription = data[1];
		        String sku = data[2];
		        String inventoryStr = data[3];
		        String priceStr = data[4];

				// add Product if does not exist
				try {
					insertRow_Product.setString(1, productName);
					insertRow_Product.setString(2, productDescription);
					insertRow_Product.setString(3, sku);
					insertRow_Product.execute();
				} catch (SQLException ex) {
					// already exists
					System.err.printf("Unable to insert Product %s\n", productName);
					if (SQLState.LANG_CHECK_CONSTRAINT_VIOLATED.equals(ex.getSQLState())) { 
						System.err.printf("SKU '%s': %s\n", sku, ex.getMessage());
					}
					continue;
				}

				// add InventoryRecord if does not exist
				try {
					insertRow_InventoryRecord.setString(1, inventoryStr);
					insertRow_InventoryRecord.setString(2, priceStr);
					insertRow_InventoryRecord.setString(3, sku);
					insertRow_InventoryRecord.execute();
				} catch (SQLException ex) {
					System.err.printf("Unable to insert InventoryRecord sku %s\n", sku);
				}
			}
			System.out.println("Committing insertions into Product and InventoryRecord tables");
			conn.commit();
			Util.printProduct(conn);
			Util.printInventoryRecord(conn);
			System.out.println();
			
			// files to read
			BufferedReader[] brs = new BufferedReader[] { br1, br2, br3, br4, br5, br6, br7 };
			
			boolean isRolledBack = false;
			for (BufferedReader br : brs) {
				// gets orderId from identity field from TheOrder table
				int orderId = 0;
				String findId = "SELECT IDENTITY_VAL_LOCAL() FROM TheOrder";
				rs = stmt.executeQuery(findId);
				if (rs.next()) {
					Integer result = rs.getInt(1);
					if (isRolledBack) {
						orderId = result;
					} else {
						orderId = result + 1;
					}
					System.out.println("new OrderId: " + orderId);
				} else {
					orderId = 1;
					System.out.println("No values in the table TheOrder. new OrderId: " + orderId);
				}
				rs.close();
				
				// gets customerId from identity field from Customer table
				int customerId = 0;
				findId = "SELECT IDENTITY_VAL_LOCAL() FROM Customer";
				rs = stmt.executeQuery(findId);
				if (rs.next()) {
					Integer result = rs.getInt(1);
					if (isRolledBack) {
						customerId = result;
						isRolledBack = false;
					} else {
						customerId = result + 1;
					}
					System.out.println("new CustomerId: " + customerId);
				} else {
					customerId = 1;
					System.out.println("No values in the table Customer. new CustomerId: " + customerId);
				}
				
				// resets the identity field's value 
				try {
					String restart = "ALTER TABLE Customer ALTER COLUMN id RESTART WITH " + customerId;
					stmt.executeUpdate(restart);
					System.out.println("Reset Customer identity column");
				} catch (SQLException e) {
					System.err.println(e.getMessage());
				}
				
				try {
					String restart = "ALTER TABLE TheOrder ALTER COLUMN id RESTART WITH " + orderId;
					stmt.executeUpdate(restart);
					System.out.println("Reset TheOrder identity column");
				} catch (SQLException e) {
					System.err.println(e.getMessage());
				}
				
				Savepoint sp = conn.setSavepoint("Orders");
				while ((line = br.readLine()) != null) {
					// splits by tab
					String[] data = line.split("\\t+");
					if (data.length != 11) {
						continue;
					}
					
					// gets customer info
					String name = data[3];
					String address = data[4];
					String city = data[5];
					String state = data[6];
					String country = data[7];
					String postalCode = data[8];
					
					try {
						// if customer is already in the table, find his/her id
						queryRow_Customer.setString(1, name);
						queryRow_Customer.setString(2, address);
						queryRow_Customer.setString(3, city);
						queryRow_Customer.setString(4, state);
						queryRow_Customer.setString(5, country);
						queryRow_Customer.setString(6, postalCode);
						queryRow_Customer.execute();
						rs = queryRow_Customer.getResultSet();
						if (rs.next()) {
							customerId = rs.getInt(1);
						} else {
							insertRow_Customer.setString(1, name);
							insertRow_Customer.setString(2, address);
							insertRow_Customer.setString(3, city);
							insertRow_Customer.setString(4, state);
							insertRow_Customer.setString(5, country);
							insertRow_Customer.setString(6, postalCode);
							insertRow_Customer.execute();
						}
						rs.close();
					} catch (SQLException e) {
						System.err.println(e.getMessage());
					}
					
					// gets order date and shipment date
					String orderDateStr = data[9];
					String shipmentDateStr = data[10];
					Date orderDate = Date.valueOf(orderDateStr);
					Date shipmentDate = Date.valueOf(shipmentDateStr);
					
					try {
						queryRow_TheOrder.setInt(1, orderId);
						queryRow_TheOrder.setInt(2, customerId);
						queryRow_TheOrder.execute();
						rs = queryRow_TheOrder.getResultSet();
						if (rs.next()) {
							Integer result = rs.getInt(1);
							if (result == 1) {
								// no need to insert the order once again
							} else {
								// add a single TheOrder entry for a given customer's order
								insertRow_TheOrder.setInt(1, customerId);
								insertRow_TheOrder.setDate(2, orderDate);
								insertRow_TheOrder.setDate(3, shipmentDate);
								insertRow_TheOrder.execute();
							}
						}
					} catch (SQLException e) {
						isRolledBack = true;
						System.err.println(e.getMessage()); 
					}
					
					// gets order info
					String SKU = data[0];
					Double price = Double.parseDouble(data[1]);
					Integer number = Integer.parseInt(data[2]);
					
					// inserts into orderRecord
					try {
						insertRow_OrderRecord.setInt(1, orderId);
						insertRow_OrderRecord.setInt(2, number);
						insertRow_OrderRecord.setDouble(3, price);
						insertRow_OrderRecord.setString(4, SKU);
						insertRow_OrderRecord.execute();
					} catch (SQLException e) {
						// if check constraint is violated
						// customer wants to buy more items
						// than in stock
						if (SQLState.LANG_CHECK_CONSTRAINT_VIOLATED.equals(e.getSQLState())) {
							// rollback
							System.err.println("Insufficient stock! Customer " + name + " attempted to buy more items than in stock. Rolling back");
							conn.rollback(sp);
							// process next customer's order
							isRolledBack = true;
							break;
						}
						System.err.println(e.getMessage());
					}
					
				} // end while
				if (rs != null) {
					rs.close();
				}
				conn.releaseSavepoint(sp);
				System.out.println("Committing insertions into Customer, THeOrder and OrderRecord tables.");
				conn.commit();
			}
			
			System.out.println("\nUpdated Inventory Record:");
			Util.printInventoryRecord(conn);
			Util.printCustomers(conn);
			Util.printTheOrder(conn);
			Util.printOrderRecord(conn);
			
			
			// restore auto-commit
			conn.setAutoCommit(true);
			
		} catch (IOException e) {
			System.err.println(e.getMessage());
		} catch (SQLException e) {
			System.err.println(e.getMessage());
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (SQLException e) {
				System.err.println(e.getMessage());
			}
		}
	}
}
