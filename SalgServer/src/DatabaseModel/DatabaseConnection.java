package DatabaseModel;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.lang.reflect.*;

import DatabaseModel.SQLExpressions.SQLExpression;

public class DatabaseConnection {
	
	public Connection connection;
	
	public DatabaseConnection() throws SQLException {
		System.out.println("====CONNECTING TO DATABASE======");
		System.out.println("................................");
		
		connection = null;
		
		// Connection information.
		Properties connectionInfo = new Properties();
		connectionInfo.put("user", "SERVER1");
		connectionInfo.put("password", "P@ssw0rd");
		
		DriverManager.registerDriver(new com.mysql.jdbc.Driver());

		connection = DriverManager.getConnection("jdbc:mysql://localhost/Test", connectionInfo);
		connection.setAutoCommit(false);
		
		if (connection.isClosed())
			System.out.println("Closed???");
		
		
	}
	
	public void closeConnection() throws SQLException {
		if (connection != null)
			connection.close();
		
		System.out.println("Connection closed.");
	}
	
	public static int getResultSize(ResultSet resultSet) throws SQLException {
		resultSet.last();
		int max = resultSet.getRow();
		resultSet.beforeFirst();
		
		return max;
	}
	
	public <T extends DatabaseObject> ArrayList<T> select(Class<T> T) throws SQLException {
		ArrayList<T> items = new ArrayList<T>();
		
		Statement statement = this.connection.createStatement();
		ResultSet resultSet = statement.executeQuery("SELECT * FROM " + T.getSimpleName());
		int max = getResultSize(resultSet);
		
		ResultSetMetaData metaData = resultSet.getMetaData();
		
		int columns = metaData.getColumnCount();
		
		String[] columnNames = new String[columns];
		HashMap<String, Object[]> values = new HashMap<String, Object[]>();

		for (int i = 1; i <= columns; i++) {
			values.put(metaData.getColumnName(i), new Object[max]);
			columnNames[i - 1] = metaData.getColumnName(i);
		}
		
		int i = 1, columnIndex = 1;
		
		while(resultSet.next()) {
			columnIndex = 1;
			try {
				items.add(T.newInstance());
			}
			catch (IllegalAccessException exception) {
				System.out.println("Target class with inaccessible protection level. - " + exception);
				return null;
			}
			catch (InstantiationException exception) {
				System.out.println("Target class with inaccessible protection level. - " + exception);
				return null;
			}
			
			for (String columnName : columnNames) {
				Object[] objects = values.get(columnName);
				Object obj = resultSet.getObject(columnIndex);
				objects[i - 1] = obj;
				values.replace(columnName, objects);
				columnIndex++;
			}
			
			T item = items.get(i - 1);
			
			Field[] fields = item.getClass().getFields();
			
			for (Field field : fields) {
				if (values.containsKey(field.getName())) {
					try {
						field.set(item, field.getType().cast(values.get(field.getName())[i -1]));
					}
					catch (IllegalAccessException exception) {
						System.out.println("Field with inaccessible protection level. - " + exception);
						return null;
					}
				}
			}
			
			i++;
		}
		
		return items;
	}
	
	public <T extends DatabaseObject> List<T> select(SQLExpression<T> expression) throws SQLException {
		List<T> items = new ArrayList<T>();
		
		ResultSet results = connection.prepareStatement(expression.toString()).executeQuery();
		
		ResultSetMetaData metaData = results.getMetaData();
		
		int columns = metaData.getColumnCount(), max = DatabaseConnection.getResultSize(results);
		
		String[] columnNames = new String[columns];
		
		HashMap<String, Object[]> values = new HashMap<String, Object[]>();

		for (int i = 1; i <= columns; i++) {
			values.put(metaData.getColumnName(i), new Object[max]);
			columnNames[i - 1] = metaData.getColumnName(i);
		}
		
		int i = 1, columnIndex = 1;
		
		while(results.next()) {
			columnIndex = 1;
			try {
				items.add((T)expression.model.newInstance());
			}
			catch (IllegalAccessException exception) {
				System.out.println("Target class with inaccessible protection level. - " + exception);
				return null;
			}
			catch (InstantiationException exception) {
				System.out.println("Target class with inaccessible protection level. - " + exception);
				return null;
			}
			
			for (String columnName : columnNames) {
				Object[] objects = values.get(columnName);
				Object obj = results.getObject(columnIndex);
				objects[i - 1] = obj;
				values.replace(columnName, objects);
				columnIndex++;
			}
			
			T item = items.get(i - 1);
			
			Field[] fields = item.getClass().getFields();
			
			for (Field field : fields) {
				if (values.containsKey(field.getName())) {
					try {
						field.set(item, field.getType().cast(values.get(field.getName())[i -1]));
					}
					catch (IllegalAccessException exception) {
						System.out.println("Field with inaccessible protection level. - " + exception);
						return null;
					}
				}
			}
			
			i++;
		}
		
		return items;
	}
	
	public <T extends Table> void insert(T instance) throws SQLException {
		Field[] fields = instance.getClass().asSubclass(instance.getClass()).getDeclaredFields();
		String fieldValuesString = "";
		String fieldsString = "";
		
		int iterations = fields.length;
		for (Field field : fields) {
			fieldsString +=  field.getName();
			try {
				if (field.get(instance) != null) {
					fieldValuesString += (field.getType() != String.class ? field.get(instance) : "\"" + field.get(instance) + "\"" );
				}
				else
					fieldValuesString += "NULL";
			}
			catch (IllegalAccessException ex) {
				System.out.println(ex);
				return;
			}
			
			iterations--;
			fieldsString += (iterations != 0 ? "," : "");
			fieldValuesString += (iterations != 0 ? "," : "");
		}
		
		String sql = "INSERT INTO " + instance.getClass().getSimpleName() + "(" + fieldsString + ") VALUES(" + fieldValuesString + ")";
		System.out.println(sql);
		
		PreparedStatement statement = connection.prepareStatement(sql);
		statement.executeUpdate();
	}
	
	public <T extends DatabaseObject> void delete(Class<T> T, HashMap<Field, Object> conditions) throws SQLException {
		String sqlWhereCondition = "";
		
		if (conditions.size() != 0) {
			
			sqlWhereCondition = " WHERE";
			
			int iterations = conditions.size();
			
			for (Field field : conditions.keySet()) {
				boolean isString = field.getType().isAssignableFrom(String.class);
				sqlWhereCondition += " " + field.getName() + " = " + (isString ? "\"" : "" ) + conditions.get(field) + (isString ? "\"" : "");
				iterations--;
				
				sqlWhereCondition += (iterations != 0 ? " AND " : "" ); 
			}
		}
		
		System.out.println("DELETE FROM " + T.getSimpleName() + sqlWhereCondition);
		
		PreparedStatement statement = this.connection.prepareStatement("DELETE FROM " + T.getSimpleName() + sqlWhereCondition);
		
		statement.executeUpdate();
	}
	
	public <T extends DatabaseObject> void delete(SQLExpression<T> expression) throws SQLException {
		System.out.println("DELETE FROM " + expression.model.getSimpleName() + " " + (expression.hasWhereCondition ? expression.whereExpression : ""));
		PreparedStatement statement = this.connection.prepareStatement("DELETE FROM " + expression.wrapClass() + " " + (expression.hasWhereCondition ? expression.whereExpression : ""));
		statement.executeUpdate();
		connection.commit();
	}
	
	public <T extends DatabaseObject> void update(T instance, SQLExpression<T> expression) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException, SQLException { 
		String sql = "UPDATE " + expression.wrapClass() + " SET ";
		int iterations = expression.selectFields.size();
		
		for (Field field : expression.selectFields) {
			sql += SQLExpression.wrapField(field) + " = " + SQLExpression.wrapValue(instance, field);
			if (--iterations != 0) {
				sql += ", ";
			}
		}
		sql += " " + expression.whereExpression;
		
		System.out.println(sql);
		connection.prepareStatement(sql).executeUpdate();
	}
	
	public <T extends DatabaseObject> void update(Class<T> T, HashMap<Field, Object> fieldsToUpdate, HashMap<Field, Object> conditions) throws SQLException, IllegalArgumentException, IllegalAccessException {
		String sqlSetFields = "";
		int iterations = fieldsToUpdate.size();
		
		for (Field field : fieldsToUpdate.keySet()) {
			boolean isString = field.getType().isAssignableFrom(String.class);
			sqlSetFields += field.getName() + " = " + (isString ? "\"" : "" ) + fieldsToUpdate.get(field) + (isString ? "\"" : "");
			iterations--;
			
			sqlSetFields += (iterations != 0 ? "," : "" ); 
		}
		
		System.out.println(sqlSetFields);
		
		String sqlWhereExpression = "";
		
		if (conditions.size() != 0) {
			
			sqlWhereExpression = " WHERE";
			
			iterations = conditions.size();
			
			for (Field field : conditions.keySet()) {
				boolean isString = field.getType().isAssignableFrom(String.class);
				sqlWhereExpression += " " + field.getName() + " = " + (isString ? "\"" : "" ) + conditions.get(field) + (isString ? "\"" : "");
				iterations--;
				
				sqlWhereExpression += (iterations != 0 ? " AND " : "" ); 
			}
		}

		System.out.println("UPDATE " + T.getSimpleName() + " SET " + sqlSetFields + sqlWhereExpression);
		
		PreparedStatement statement = this.connection.prepareStatement("UPDATE " + T.getSimpleName() + " SET " + sqlSetFields + sqlWhereExpression);
		
		statement.executeUpdate();
	}
}
