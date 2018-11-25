/**
 * This class contains stored function for validating sku type to string.
 * 
 * @author Yitong Hu
 *
 */

public class Helper {	
	/**
	 * stored function
	 * Determines whether sku is a valid SKU
	 * 
	 * @param sku the sku
	 * @return true if sku is a valid SKU
	 */
	public static boolean isSKU(String sku) {
		return sku.matches("[A-Z]{2}-(\\d{6})-([\\d{2}]|[A-Z]{2}|[\\d][A-Z]|[A-Z][\\d])$");
	}
	
	/**
	 * This function parses a number string into an unsigned decimal integer. The characters 
	 * in the string must all be decimal digits. 
	 *
	 * @param s the number string
	 * @return the unsigned integer value represented by the argument in decimal
	 * @throws NumberFormatException if the string does not contain a parsable unsigned integer 
	 */
	static public int parseNumber(String s) throws NumberFormatException { // need to parse
		return Integer.parseUnsignedInt(s);
	}
	
	/**
	 * This function parses a price string into a double. 
	 * 
	 * @param s the price string
	 * @return the double value represented by the string argument
	 * @throws NumberFormatException if the string does not contain a parsable double
	 */
	static public double parsePrice(String s) throws NumberFormatException { // need to parse
		return Math.round(Double.parseDouble(s) * 100) / 100; // round up the double to 2 decimal places
	}
	
	/**
	 * This function parses a date string into a Date. 
	 * 
	 * @param s the date string
	 * @return the Date value represented by the string argument
	 * @throws IllegalArgumentException if the date string given is not in the JDBC date escape format (yyyy-[m]m-[d]d) 
	 */
	static public java.sql.Date parseDate(String s) throws IllegalArgumentException { // need to parse 
		return java.sql.Date.valueOf(s);
	}
}
