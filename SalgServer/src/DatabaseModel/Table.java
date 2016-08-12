package DatabaseModel;

import DatabaseModel.TypeMapping.TypeMap;
import DatabaseModel.Annotations.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.sql.JDBCType;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.mysql.jdbc.NotImplemented;

public class Table extends DatabaseObject {

	public String type = "TABLE";
	
	// Reflection magic.
	public String toString() {
		String serialization = "";
		this.getClass().asSubclass(this.getClass());
		Field[] fields = this.getClass().getDeclaredFields();
		
		for (Field field : fields) {
			try {
				serialization += field.getName() + ": " + field.get(this) + "\n";
			}
			catch (IllegalAccessException exception) {
				System.out.println("WHYY DO YOU HAVE AN INACCESSIBLE FIELD??!?! " + exception);
			}
		}
		
		return serialization;
	}
	
	public void Drop(DatabaseConnection db) throws SQLException {
		System.out.println("DROP TABLE " + this.getClass().asSubclass(this.getClass()).getSimpleName());
		PreparedStatement statement = db.connection.prepareStatement("DROP TABLE " + this.getClass().asSubclass(this.getClass()).getSimpleName());
		statement.executeUpdate();
	}
	
	public void Create(DatabaseConnection db) throws SQLException {
		String sql = "CREATE TABLE " + this.getClass().getSimpleName() + "(";
		
		Field[] fields = this.getClass().getDeclaredFields();
		
		int iterations = fields.length;
		
		for (Field field : fields) {
			String JDBCTypeName = "";
			
			try {
				JDBCTypeName = TypeMap.mapTypeToJDBC(field.getType());
			}
			catch (NotImplemented exception) {
				JDBCTypeName = JDBCType.valueOf(field.getType().getSimpleName().toUpperCase()).getName();
			}
			finally {
				JDBCTypeName = JDBCType.valueOf(JDBCTypeName).getName();
			}
			
			String sqlSize = "";
			String sqlConstraints = "";

			if (JDBCTypeName == "VARCHAR")
				sqlSize = "(256)";
			
			Annotation[] annotations = field.getAnnotations();
			
			boolean nullable = false;
			
			if (annotations.length != 0) {
				for (Annotation annotation : annotations) {
					if (annotation instanceof PrimaryKey) {
						sqlConstraints += " PRIMARY KEY";
						nullable = PrimaryKey.nullable;
					}
					else if (annotation instanceof AutoIncrement) {
						sqlConstraints += " AUTO_INCREMENT";
					}
					else if (annotation instanceof Nullable) {
						nullable = true;
					}
					else if (annotation instanceof Precision) {
						sqlSize = "("  + ((Precision)annotation).integers() + "," + ((Precision)annotation).decimals() + ")";
					}
				}
			}
			
			sql += field.getName() + " " + JDBCTypeName + sqlSize +  " " + (nullable ? "NULL" : "") + sqlConstraints;
			
			if (--iterations != 0)
				sql += ",";
		}
		
		sql += ")";
		
		System.out.println(sql);
		PreparedStatement statement = db.connection.prepareStatement(sql);
		statement.executeUpdate(sql);
	}
}
