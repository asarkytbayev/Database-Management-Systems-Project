import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * This class contains functions for printing Product,
 * InventoryRecord, TheOrder, and OrderRecord in the database. 
 * 
 * @author Yitong Hu
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
			System.out.println("Products:");
			int count = 0;
			while (rs.next()) {
				String name = rs.getString(1);
				String description = rs.getString(2);
				String SKU = rs.getString(3);
				System.out.printf("  (%s) %s, %s\n", name, description, SKU);
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
			ResultSet rs = stmt.executeQuery(
				"SELECT CONVERT(varchar(64), number), " // SELECT can be followed by function! convert int to string
				+ "CONVERT(varchar(64), price), "
				+ "productSKU "
				+ "FROM InventoryRecord ORDER BY productSKU"); 
		) {
			System.out.println("InventoryRecord:");
			int count = 0;
			while (rs.next()) {
				String numberStr = rs.getString(1);
				String priceStr = rs.getString(2);
				String SKU = rs.getString(3);
				System.out.printf("  (%s) %s\n", numberStr, priceStr, SKU);
				count++;
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
			ResultSet rs = stmt.executeQuery(
					"SELECT CONVERT(varchar(64), id), "
					+ "CONVERT(varchar(64), customerId), "
					+ "CONVERT(varchar(64), orderDate, 23), " // check
					+ "CONVERT(varchar(64), shipmentDate, 23) " // check
					+ "FROM TheOrder ORDER BY id"); // check if can order by original int id
		) {
			System.out.println("Publishers:");
			int count = 0;
			while (rs.next()) {
				String orderIDStr = rs.getString(1);
				String customerIDStr = rs.getString(2);
				String orderDateStr = rs.getString(3);
				String shipmentDateStr = rs.getString(4);
				System.out.printf("  %s, %s\n", orderIDStr, customerIDStr, orderDateStr, shipmentDateStr);
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
			ResultSet rs = stmt.executeQuery(
					"SELECT CONVERT(varchar(64), orderId), "
					+ "productSKU, "
					+ "CONVERT(varchar(64), number), "
					+ "CONVERT(varchar(64), inventoryPrice) "
					+ "FROM OrderRecord ORDER BY orderId");
		) {
			System.out.println("OrderRecord:");
			int count = 0;
			while (rs.next()) {
				String orderIdStr = rs.getString(1);
				String SKU = rs.getString(2);
				String numberStr = rs.getString(3);
				String priceStr = rs.getString(4);
				System.out.printf("  %s, %s\n", orderIdStr, SKU, numberStr, priceStr);
				count++;
			}
			return count;
		}
	}
}

