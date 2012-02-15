package weibo4j;

/**
 * @author SinaWeibo
 * 
 */
public enum Gender {
	MALE, FEMALE;
	public static String valueOf(Gender gender) {
		int ordinal= gender.ordinal();
		if(ordinal==0)
			return "m";
		return "f";
	}
}
