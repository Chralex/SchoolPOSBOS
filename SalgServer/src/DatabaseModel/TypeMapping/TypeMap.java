package DatabaseModel.TypeMapping;

import com.mysql.jdbc.NotImplemented;

public class TypeMap {

	/**
	 * Used to map some types to JDBC when the JDBCTypes class cannot figure it out.
	 * @param T
	 * @return The proper JDBC type as a uppercase string.
	 * @throws NotImplemented
	 */
	public static <T> String mapTypeToJDBC(T T) throws NotImplemented {
		if (String.class == T)
			return "VARCHAR";

		throw new NotImplemented();
	}

}
