/**
 * This class contains stored function for validating sku type to string.
 * 
 * @author Yitong Hu
 * @author Azamat Sarkytbayev
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
		return sku.matches("[A-Z]{2}-(\\d{6})-(\\d{2}|[A-Z]{2}|[\\d][A-Z]|[A-Z][\\d])$");
	}
}
