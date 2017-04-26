package space.chensheng.wsmessenger.common.util;

public class StringUtil {
	public static boolean isEmpty(String str) {
		return str == null || str.trim().equals("");
	}
	
	public static boolean isNotEmpty(String str) {
		return !isEmpty(str);
	}
	
	public static int parseToInt(String str, int defaultVal) {
		if (isNotEmpty(str)) {
			try {
				return Integer.valueOf(str);
			} catch (NumberFormatException e) {
				return defaultVal;
			}
		}
		return defaultVal;
	}
	
	public static Integer parseToInteger(String str, Integer defaultVal) {
		if (isNotEmpty(str)) {
			try {
				return Integer.valueOf(str);
			} catch (NumberFormatException e) {
				return defaultVal;
			}
		}
		return defaultVal;
	}
	
	public static long parseToLong(String str, long defaultVal) {
		if(isNotEmpty(str)) {
			try {
				return Long.valueOf(str);
			} catch (NumberFormatException e) {
				return defaultVal;
			}
		}
		return defaultVal;
	}
	
	public static double parseToDouble(String str, double defaultVal) {
		if(isNotEmpty(str)) {
			try {
				return Double.valueOf(str);
			} catch (NumberFormatException e) {
				return defaultVal;
			}
		}
		return defaultVal;
	}
	
	public static int retrieveInt(String str, int defaultVal) {
		if (isNotEmpty(str)) {
			str = str.replaceAll("[^0-9]", "");
			return parseToInt(str, defaultVal);
		}
		return defaultVal;
	}
}
