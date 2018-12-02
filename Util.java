
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * This class contains functions for printing Product,
 * InventoryRecord, TheOrder, and OrderRecord in the database. 
 * 
 * @author Yitong Hu
 * @author Azamat Sarkytbayev
 *
 */

public class Util {
	/**
	 * Print Product table.
	 * @param conn the connection
	 * @return number of products
	 * @throws SQLException if a database operation fails
	 */
	static int printProduct(Connection conn) throws SQLException {
		try (
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(
					"SELECT name, description, SKU FROM Product ORDER BY SKU");
		) {
			System.out.println("\nProducts:");
			System.out.printf("%-16s\t%-16s\t%-16s\n", "SKU", "Name", "Description");
			int count = 0;
			while (rs.next()) {
				String name = rs.getString(1);
				String description = rs.getString(2);
				String SKU = rs.getString(3);
				System.out.printf("%-16s\t%-16s\t%-16s\n", SKU, name, description);
				count++;
			}
			return count;
		}
	}
	
	/**
	 * Print InventoryRecord table.
	 * @param conn the connection
	 * @return number of inventory records
	 * @throws SQLException if a database operation fails
	 */
	static int printInventoryRecord(Connection conn) throws SQLException {
		try (
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT productSKU, number, price FROM InventoryRecord ORDER BY productSKU");
		) {
			System.out.println("\nInventoryRecord:");
			System.out.printf("%-16s\t%-16s\t%-16s\n", "SKU", "Number", "Price");
			int count = 0;
			while (rs.next()) {
				String SKU = rs.getString(1);
				Integer number = rs.getInt(2);
				Double price = rs.getDouble(3);
				System.out.printf("%-16s\t%-16d\t%-16.2f\n", SKU, number, price);
				count++;
			}
			return count;
		}		
	}
	
	/**
	 * Prints Customer table
	 * @param conn the connection
	 * @return number of customers
	 * @throws SQLException if db operation fails
	 */
	static int printCustomers(Connection conn) throws SQLException {
		try ( 
			Statement stmt = conn.createStatement();
		    // results
		    ResultSet rs = stmt.executeQuery("SELECT id, name, address, city, state, country, postalCode FROM Customer ORDER BY id");
		) {
			System.out.println("\nCustomers:");
			System.out.printf("%-4s\t%-16s\t%-32s\t%-12s\t%-4s\t%-8s\t%-8s\n", "ID", "Name", "Address", "City", "State", "Country", "Postal Code");
			int count = 0;
			while (rs.next()) {
				Integer id = rs.getInt(1);
				String name = rs.getString(2);
				String address = rs.getString(3);
				String city = rs.getString(4);
				String state = rs.getString(5);
				String country = rs.getString(6);
				String postalCode = rs.getString(7);
				System.out.printf("%-4d\t%-16s\t%-32s\t%-12s\t%-4s\t%-8s\t%-8s\n", id, name, address, city, state, country, postalCode);
				++count;
			}
			return count;
		}
	}	
	/**
	 * Print TheOrder table.
	 * @param conn the connection
	 * @return number of orders
	 * @throws SQLException if a database operation fails
	 */
	static int printTheOrder(Connection conn) throws SQLException {
		try (
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT id, customerId, orderDate, shipmentDate FROM TheOrder ORDER BY id");
		) {
			System.out.println("\nTheOrder:");
			System.out.printf("%-4s\t%-16s\t%-16s\t%-16s\n", "ID", "Customer ID", "Order Date", "Shipment Date");
			int count = 0;
			while (rs.next()) {
				Integer orderId = rs.getInt(1);
				Integer customerId = rs.getInt(2);
				Date orderDate = rs.getDate(3);
				Date shipmentDate = rs.getDate(4);
				System.out.printf("%-4d\t%-16d\t%-16s\t%-16s\n", orderId, customerId, orderDate, shipmentDate);
				count++;
			}
			return count;
		}
	}
	
	/**
	 * Print OrderRecord table.
	 * @param conn the connection
	 * @return number of order records
	 * @throws SQLException if a database operation fails
	 */
	static int printOrderRecord(Connection conn) throws SQLException {
		try (
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT orderId, number, inventoryPrice, productSKU FROM OrderRecord ORDER BY orderId")
		) {
			System.out.println("\nOrderRecord:");
			System.out.printf("%-16s\t%-16s\t%-16s\t%-16s\n", "Order ID", "Number", "Price", "SKU");
			int count = 0;
			while (rs.next()) {
				Integer id = rs.getInt(1);
				Integer quantity = rs.getInt(2);
				Double price = rs.getDouble(3);
				String SKU = rs.getString(4);
				System.out.printf("%-16d\t%-16d\t%-16.2f\t%-16s\n", id, quantity, price, SKU);
				count++;
			}
			return count;
		}
	}
}

