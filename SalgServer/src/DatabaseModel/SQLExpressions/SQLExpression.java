package DatabaseModel.SQLExpressions;

import java.lang.reflect.Field;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;

import DatabaseModel.DatabaseObject;
import DatabaseModel.Table;

public class SQLExpression<T extends DatabaseObject> {
	
	public boolean hasWhereCondition = false;
	
	public String fromExpression = "FROM ";
	public String selectExpression = "SELECT *";
	public String whereExpression = "WHERE ";
	public Class<T> databaseObject;
	public ArrayList<Field> selectFields = new ArrayList<Field>(); 
	
	/**
	 * Create an SQLExpression that targets a DatabaseObject class.
	 * @param databaseObject - The 
	 */
	public SQLExpression(Class<T> databaseObject) {
		this.databaseObject = databaseObject;
		fromExpression += wrapClass(databaseObject);
	}

	/**
	 * Create a custom select expression by providing fields.
	 * @param fields - The fields to select.
	 * @return the same SQLExpression used for chaining. 
	 */
	public SQLExpression<T> select(Field[] fields) {
		selectExpression = "SELECT ";
		int iterations = fields.length;
		for (Field field : fields) {
			selectExpression += wrapField(field);
			selectFields.add(field);
			
			if (--iterations != 0) {
				selectExpression += ", ";
			}
		}
		
		return this;
	}
	
	/**
	 * Create or append a custom where expression by providing fields and an instance of a type containing the field values.
	 * @param T - Instance of a type that contains the field values.
	 * @param fields - The target fields that will be appended in the where expression, values are contained in the type instance.
	 * @return the same SQLExpression used for chaining.
	 */
	public SQLExpression<T> where (T T, Field[] fields) {
		for (Field field : fields) { 
			whereExpression +=  (hasWhereCondition == true ? " AND " : "") + wrapField(field) + " = " + wrapValue(T, field);
			hasWhereCondition = true;
		}
		
		return this;
	}
	
	/**
	 * Append to a custom where expression with an OR statement.
	 * @param T - Instance of a type that contains the field values.
	 * @param fields - The target fields that will be appended in the where expression, values are contained in the type instance.
	 * @return the same SQLExpression used for chaining.
	 */
	public SQLExpression<T> or (T T, Field[] fields) {
		for (Field field : fields) { 
			whereExpression += (hasWhereCondition == true ? " OR " : "") + wrapField(field) + " = " + wrapValue(T, field);
			hasWhereCondition = true;
		}
		
		return this;
	}
	
	/**
	 * Append to a custom where expression with an AND statement.
	 * @param T - Instance of a type that contains the field values.
	 * @param fields - The target fields that will be appended in the where expression, values are contained in the type instance.
	 * @return the same SQLExpression used for chaining.
	 */
	public SQLExpression<T> and (T T, Field[] fields) {
		for (Field field : fields) { 
			whereExpression += (hasWhereCondition == true ? " AND " : "") + wrapField(field) + " = " + wrapValue(T, field);
			hasWhereCondition = true;
		}
		
		return this;
	}
	
	public <V extends Table> SQLExpression<T> in (V[] values, Field[] fields)
	{
		HashMap<String, ArrayList<Object>> fieldValues = new HashMap<String, ArrayList<Object>>();
		
		for (V value : values) {
			for (Field field : fields) {
				try {
					if (fieldValues.containsKey(field.getName()) == false) {
						ArrayList<Object> objValues = new ArrayList<Object>();
						Object instanceValue = value.getClass().getField(field.getName()).get(value);
						if (instanceValue != null) {
							objValues.add(instanceValue);
							fieldValues.put(field.getName(), objValues);
						}
					}
					else {
						Object instanceValue = value.getClass().getField(field.getName()).get(value);
						if (instanceValue != null)
							fieldValues.get(field.getName()).add(instanceValue);
					}
				} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException
						| SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}
		

		for (String field : fieldValues.keySet()) {
			this.whereExpression += (this.hasWhereCondition ? " AND " : "") + "`" + field + "` IN ( ";
			ArrayList<Object> valueObjects = fieldValues.get(field);
			int iterations = valueObjects.size(); 
			for (Object object : valueObjects) {
				this.whereExpression += wrapValue(object);

				if (--iterations != 0)
					this.whereExpression += ",";
				else
					this.whereExpression += ")";
			}
			this.hasWhereCondition = true;
		}

		return this; // Chaining.
	}
	/**
	 * Used for SQL-field parameterization.
	 * @param field
	 * @return Parameterized SQL-column name.
	 */
	public static String wrapField(Field field) {
		return "`" + field.getName() + "`";
	}
	
	/**
	 * Used for SQL-DatabaseObject parameterization.
	 * @param definition
	 * @return Parameterized SQL-DatabaseObject name.
	 */
	private static String wrapClass(Class<?> definition) {
		return "`" + definition.getSimpleName() + "`";
	}
	
	/**
	 * Used for SQL-DatabaseObject parameterization.
	 * @return Parameterized SQL-DatabaseObject name.
	 */
	public String wrapClass() {
		return "`" + databaseObject.getSimpleName() + "`";
	}
	
	/**
	 * Parameterize a field value before using it in a query.
	 * @param T - The instance that contains the field with the value to parameterize.
	 * @param field - The field that contains the value to parameterize.
	 * @return Parameterized SQL-value.
	 */
	public static <T extends DatabaseObject> String wrapValue(T T, Field field) {
		Class<?> type = field.getType();
		String value = "";
		
		try {
			value = field.get(T).toString();
		}
		catch (Exception e) {
			System.out.println(e);
			return value;
		}
		
		if (type == String.class) {
			return "\"" + value + "\"";
		}
		else if (type == Date.class) {
			return "\"" + value + "\"";
		}
		else if (type == java.util.Date.class) {
		    try {
				return "\"" + new java.sql.Date(((java.util.Date)field.get(T)).getTime()).toString() + "\"";
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		else if (type == Timestamp.class) {
			return "\"" + value + "\"";
		}
		
		return value;
	}

	/**
	 * Parameterize a field value before using it in a query.
	 * @param Object object - The value to parameterize.
	 * @return Parameterized SQL-value.
	 */
	public static <T extends DatabaseObject> String wrapValue(Object object) {
		Class<?> type = object.getClass();
		String value = "";
		
		try {
			value = object.toString();
		}
		catch (Exception e) {
			System.out.println(e);
			return value;
		}
		
		if (type == String.class) {
			return "\"" + value + "\"";
		}
		else if (type == Date.class) {
			return "\"" + value + "\"";
		}
		else if (type == java.util.Date.class) {
		    try {
				return "\"" + new java.sql.Date(((java.util.Date)object).getTime()).toString() + "\"";
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			}
		}
		else if (type == Timestamp.class) {
			return "\"" + value + "\"";
		}
		
		return value;
	}

	
	/**
	 * This may be used to execute functional SQL statements. Ideal for select statements or used in sub-queries or custom inner joins or UNIONS.
	 * @return An SQL statement.
	 */
	public String toString() {
		//System.out.println(selectExpression + " " + fromExpression + " " + (hasWhereCondition ? whereExpression : ""));
		return selectExpression + " " + fromExpression + " " + (hasWhereCondition ? whereExpression : "");
	}
	
}

