package DatabaseModel.TypeMapping;

import com.mysql.jdbc.NotImplemented;

public class TypeMap {

	public static <T> String mapTypeToJDBC(T T) throws NotImplemented {
		if (String.class == T)
			return "VARCHAR";

		throw new NotImplemented();
	}

}
