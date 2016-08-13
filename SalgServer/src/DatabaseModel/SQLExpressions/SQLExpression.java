package DatabaseModel.SQLExpressions;

import java.lang.reflect.Field;
import java.util.ArrayList;

import DatabaseModel.DatabaseObject;

public class SQLExpression<T extends DatabaseObject> {
	
	public boolean hasWhereCondition = false;
	
	public String fromExpression = "FROM ";
	public String selectExpression = "SELECT *";
	public String whereExpression = "WHERE ";
	public Class<T> model;
	public ArrayList<Field> selectFields = new ArrayList<Field>(); 
	
	
	public SQLExpression(Class<T> model) {
		this.model = model;
		fromExpression += wrapClass(model);
	}

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
	
	public SQLExpression<T> where (T T, Field[] fields) {
		for (Field field : fields) { 
			whereExpression +=  (hasWhereCondition == true ? " AND " : "") + wrapField(field) + " = " + wrapValue(T, field);
			hasWhereCondition = true;
		}
		
		return this;
	}
	
	public SQLExpression<T> or (T T, Field[] fields) {
		for (Field field : fields) { 
			whereExpression += (hasWhereCondition == true ? " OR " : "") + wrapField(field) + " = " + wrapValue(T, field);
			hasWhereCondition = true;
		}
		
		return this;
	}
	
	public SQLExpression<T> and (T T, Field[] fields) {
		for (Field field : fields) { 
			whereExpression += (hasWhereCondition == true ? " AND " : "") + wrapField(field) + " = " + wrapValue(T, field);
			hasWhereCondition = true;
		}
		
		return this;
	}
	
	public static String wrapField(Field field) {
		return "`" + field.getName() + "`";
	}
	
	private static String wrapClass(Class<?> definition) {
		return "`" + definition.getSimpleName() + "`";
	}
	
	public String wrapClass() {
		return "`" + model.getSimpleName() + "`";
	}
	
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
		
		return value;
	}

	public String toString() {
		System.out.println(selectExpression + " " + fromExpression + " " + (hasWhereCondition ? whereExpression : ""));
		return selectExpression + " " + fromExpression + " " + (hasWhereCondition ? whereExpression : "");
	}
	
}
