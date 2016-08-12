package DatabaseModel;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import java.lang.reflect.*;

import DatabaseModel.Tables.Product;

public class DatabaseConnection {
	
	public Connection connection;
	
	DatabaseConnection() throws SQLException {
		System.out.println("====CONNECTING TO DATABASE======");
		System.out.println("................................");
		
		connection = null;
		
		// Connection information.
		Properties connectionInfo = new Properties();
		connectionInfo.put("user", "SERVER1");
		connectionInfo.put("password", "P@ssw0rd");
		
		DriverManager.registerDriver(new com.mysql.jdbc.Driver());
		//Class.forName("com.mysql.jdbc.Driver").newInstance();
		//DriverManager.registerDriver(new com.mysql.jdbc.Driver());
		
		connection = DriverManager.getConnection("jdbc:mysql://10.11.20.4/Test", connectionInfo);
		connection.setAutoCommit(false);
		if (connection.isClosed())
			System.out.println("Closed?");
		
		Statement statement = connection.createStatement();
		ResultSet resultSet = statement.executeQuery("SELECT * FROM Test");
		
		resultSet.last();
		int max = resultSet.getRow();
		resultSet.beforeFirst();
		
		ResultSetMetaData metaData = resultSet.getMetaData();
		
		int columns = metaData.getColumnCount();
		
		String[] columnNames = new String[columns];
		HashMap<String, Object[]> values = new HashMap<String, Object[]>();

		for (int i = 1; i <= columns; i++) {
			values.put(metaData.getColumnName(i), new Object[max]);
			columnNames[i - 1] = metaData.getColumnName(i);
		}
		
		
		int i = 1, columnIndex = 1;
		
		ArrayList<Product> products = new ArrayList<Product>();
		while(resultSet.next()) {
			products.add(new Product());
			
			for (String columnName : columnNames) {
				Object[] objects = values.get(columnName);
				Object obj = resultSet.getObject(columnIndex);
				objects[i - 1] = obj;
				values.replace(columnName, objects);
				columnIndex++;
			}
			
			products.get(i - 1).id = (int)values.get("Id")[i - 1];
			products.get(i - 1).name = (String)values.get("name")[i - 1];
			
			i++;
		}
		
		for (Product product : products) {
			System.out.println(product);
		}
		
		connection.close();
		System.out.println("Connection closed");
	}
	
	public DatabaseConnection(boolean experimental) throws SQLException {
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
		
		System.out.println(sqlWhereExpression);
		
		System.out.println("UPDATE " + T.getSimpleName() + " SET " + sqlSetFields + sqlWhereExpression);
		
		PreparedStatement statement = this.connection.prepareStatement("UPDATE " + T.getSimpleName() + " SET " + sqlSetFields + sqlWhereExpression);
		
		statement.executeUpdate();
	}
}
