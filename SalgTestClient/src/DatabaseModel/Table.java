package DatabaseModel;

import java.io.Serializable;
import java.lang.reflect.Field;

public class Table extends DatabaseObject implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// Reflection magic.
	public String toString() {
		String serialization = "";
		this.getClass().asSubclass(this.getClass());
		Field[] fields = this.getClass().getDeclaredFields();

		for (Field field : fields) {
			try {
				serialization += field.getName() + ": " + field.get(this) + "\n";
			} catch (IllegalAccessException exception) {
				System.out.println("WHYY DO YOU HAVE AN INACCESSIBLE FIELD??!?! " + exception);
			}
		}

		return serialization;
	}
}
