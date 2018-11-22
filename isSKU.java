/**
 * Checks if SKU is valid
 * @param sku stock keeping unit
 * @return true if valid, false otherwise
 */
public static boolean isSKU(String sku) {
	return sku.matches("[A-Z]{2}-(\\d{6})-([\\d{2}]|[A-Z]{2}|[\\d][A-Z]|[A-Z][\\d])$");
}