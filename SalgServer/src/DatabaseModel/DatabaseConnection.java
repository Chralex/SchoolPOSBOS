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

	/**
	 * Create a new database connection. Remember to close it when done!
	 * Not closing the connection may make the server unable to respond.
	 * If that happens restart the server. Type: "services.msc" in the start menu and find the MySQL server service and restart the service.
	 * @throws SQLException
	 */
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

	/**
	 * Close the connection in order to free up the server. If the connection isn't closed the SQL server may be clogged causing it to stop responding.
	 * @throws SQLException
	 */
	public void closeConnection() throws SQLException {
		if (connection != null)
			connection.close();

		System.out.println("Connection closed.");
	}

	/**
	 * Gets the length of a SQL ResultSet.
	 * @param resultSet The ResultSet instance to get the length of.
	 * @return Integer - Result length in rows.
	 * @throws SQLException
	 */
	public static int getResultSize(ResultSet resultSet) throws SQLException {
		resultSet.last();
		int max = resultSet.getRow();
		resultSet.beforeFirst();

		return max;
	}

	/**
	 * Select all rows from the given DatabaseObject.
	 * @param T - The DatabaseObject class.
	 * @return ArrayList<T> - A list of the type you requested with all the entries in the database.
	 * @throws SQLException
	 */
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

		while (resultSet.next()) {
			columnIndex = 1;
			try {
				items.add(T.newInstance());
			} catch (IllegalAccessException exception) {
				System.out.println("Target class with inaccessible protection level. - " + exception);
				return null;
			} catch (InstantiationException exception) {
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
						field.set(item, field.getType().cast(values.get(field.getName())[i - 1]));
					} catch (IllegalAccessException exception) {
						System.out.println("Field with inaccessible protection level. - " + exception);
						return null;
					}
				}
			}

			i++;
		}

		return items;
	}

	/**
	 * Use a SQLExpression to execute an advanced select statement.
	 * @param SQLExpression<T> expression - An expression over a type of DatabaseObject. 
	 * @return A list of the given DatabaseObject type provided in the SQLExpression.
	 * @throws SQLException
	 */
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

		while (results.next()) {
			columnIndex = 1;
			try {
				items.add((T) expression.databaseObject.newInstance());
			} catch (IllegalAccessException exception) {
				System.out.println("Target class with inaccessible protection level. - " + exception);
				return null;
			} catch (InstantiationException exception) {
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
						field.set(item, field.getType().cast(values.get(field.getName())[i - 1]));
					} catch (IllegalAccessException exception) {
						System.out.println("Field with inaccessible protection level. - " + exception);
						return null;
					}
				}
			}

			i++;
		}

		return items;
	}

	/**
	 * Insert an instance of a type that extends DatabaseObject.
	 * @param instance - A class instance that extends Table.
	 * @throws SQLException
	 */
	public <T extends Table> void insert(T instance) throws SQLException {
		Field[] fields = instance.getClass().asSubclass(instance.getClass()).getDeclaredFields();
		String fieldValuesString = "";
		String fieldsString = "";

		int iterations = fields.length;
		for (Field field : fields) {
			fieldsString += field.getName();
			try {
				if (field.get(instance) != null) {
					fieldValuesString += wrapValue(instance, field);
				} else
					fieldValuesString += "NULL";
			} catch (IllegalAccessException ex) {
				System.out.println(ex);
				return;
			}

			iterations--;
			fieldsString += (iterations != 0 ? "," : "");
			fieldValuesString += (iterations != 0 ? "," : "");
		}

		String sql = "INSERT INTO " + instance.getClass().getSimpleName() + "(" + fieldsString + ") VALUES("
				+ fieldValuesString + ")";
		//System.out.println(sql);

		PreparedStatement statement = connection.prepareStatement(sql);
		statement.executeUpdate();
		connection.commit();
	}
	
	/**
	 * Used to wrap a value and return it if necessary or just returned as the original serialized value if wrapping of that type is not necessary.
	 * @param T - The instance that contains the provided field.
	 * @param field - the field to wrap the value of.
	 * @return A serialized field value that is wrapped for SQL-parameterization or not.
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	private static <T extends DatabaseObject> String wrapValue(T T, Field field) throws IllegalArgumentException, IllegalAccessException {
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
		    return "\"" + new java.sql.Date(((java.util.Date)field.get(T)).getTime()).toString() + "\"";
		}
		else if (type == Timestamp.class) {
			return "\"" + value + "\"";
		}
		
		return value;
	}

	/**
	 * Delete a record from the database of a certain class type where the provided conditions fit.
	 * @param T - The type of database record to delete.
	 * @param HashMap<Field, Object> conditions - A key field which contains a field name paired with a value used to compose a WHERE-statement.
	 * @throws SQLException
	 */
	public <T extends DatabaseObject> void delete(Class<T> T, HashMap<Field, Object> conditions) throws SQLException {
		String sqlWhereCondition = "";

		if (conditions.size() != 0) {

			sqlWhereCondition = " WHERE";

			int iterations = conditions.size();

			for (Field field : conditions.keySet()) {
				boolean isString = field.getType().isAssignableFrom(String.class);
				sqlWhereCondition += " " + field.getName() + " = " + (isString ? "\"" : "") + conditions.get(field)
						+ (isString ? "\"" : "");
				iterations--;

				sqlWhereCondition += (iterations != 0 ? " AND " : "");
			}
		}

		//System.out.println("DELETE FROM " + T.getSimpleName() + sqlWhereCondition);

		PreparedStatement statement = this.connection
				.prepareStatement("DELETE FROM " + T.getSimpleName() + sqlWhereCondition);

		statement.executeUpdate();
		connection.commit();
	}

	/**
	 * Use an SQLExpression to delete a database record.
	 * @param SQLExpression<T> - An SQLExpression with a generic type that extends DatbaseObject.
	 * @throws SQLException
	 */
	public <T extends DatabaseObject> void delete(SQLExpression<T> expression) throws SQLException {
//		System.out.println("DELETE FROM " + expression.model.getSimpleName() + " "
//				+ (expression.hasWhereCondition ? expression.whereExpression : ""));
		PreparedStatement statement = this.connection.prepareStatement("DELETE FROM " + expression.wrapClass() + " "
				+ (expression.hasWhereCondition ? expression.whereExpression : ""));
		statement.executeUpdate();
		connection.commit();
	}

	/**
	 * Use an SQLExpression to update a record in the database.
	 * @param T instance - A generic object which contains the values to populate the target record with.
	 * @param SQLExpression<T> expression - An expression that is used to create the WHERE-statement for the update. 
	 * @throws NoSuchFieldException
	 * @throws SecurityException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws SQLException
	 */
	public <T extends DatabaseObject> void update(T instance, SQLExpression<T> expression) throws NoSuchFieldException,
			SecurityException, IllegalArgumentException, IllegalAccessException, SQLException {
		String sql = "UPDATE " + expression.wrapClass() + " SET ";
		int iterations = expression.selectFields.size();

		for (Field field : expression.selectFields) {
			sql += SQLExpression.wrapField(field) + " = " + SQLExpression.wrapValue(instance, field);
			if (--iterations != 0) {
				sql += ", ";
			}
		}
		sql += " " + expression.whereExpression;

		//System.out.println(sql);
		connection.prepareStatement(sql).executeUpdate();
		connection.commit();
	}

	/**
	 * Update a database record using a target Class<T extends DatabaseObject> 
	 * @param T - A Class that extends a DatabaseObject.
	 * @param HashMap<Field, Object> fieldsToUpdate - A HashMap structure that contains field definitions and their values, used to update the targeted database records.
	 * @param HashMap<Field, Object> conditions - A HashMap Structure that contains field definitions and their values. Used to compose the WHERE-Statement filter.
	 * @throws SQLException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	public <T extends DatabaseObject> void update(Class<T> T, HashMap<Field, Object> fieldsToUpdate,
			HashMap<Field, Object> conditions) throws SQLException, IllegalArgumentException, IllegalAccessException {
		String sqlSetFields = "";
		int iterations = fieldsToUpdate.size();

		for (Field field : fieldsToUpdate.keySet()) {
			boolean isString = field.getType().isAssignableFrom(String.class);
			sqlSetFields += field.getName() + " = " + (isString ? "\"" : "") + fieldsToUpdate.get(field)
					+ (isString ? "\"" : "");
			iterations--;

			sqlSetFields += (iterations != 0 ? "," : "");
		}

		//System.out.println(sqlSetFields);

		String sqlWhereExpression = "";

		if (conditions.size() != 0) {

			sqlWhereExpression = " WHERE";

			iterations = conditions.size();

			for (Field field : conditions.keySet()) {
				boolean isString = field.getType().isAssignableFrom(String.class);
				sqlWhereExpression += " " + field.getName() + " = " + (isString ? "\"" : "") + conditions.get(field)
						+ (isString ? "\"" : "");
				iterations--;

				sqlWhereExpression += (iterations != 0 ? " AND " : "");
			}
		}

		//System.out.println("UPDATE " + T.getSimpleName() + " SET " + sqlSetFields + sqlWhereExpression);

		PreparedStatement statement = this.connection
				.prepareStatement("UPDATE " + T.getSimpleName() + " SET " + sqlSetFields + sqlWhereExpression);

		statement.executeUpdate();
		connection.commit();
	}
	
	/**
	 * Use an SQLExpression to check if at least one record that matches the SQLExpression. 
	 * @param SQLExpression<T extends DatabaseObject> expression - If the expression is matched by a database record the method will return true.
	 * @return
	 * @throws SQLException
	 */
	public <T extends DatabaseObject> boolean exists(SQLExpression<T> expression) throws SQLException {
		ResultSet results = connection.createStatement().executeQuery("SELECT 1 as `BooleanColumn` FROM " + expression.wrapClass() + " " + expression.whereExpression + " LIMIT 1");
		
		results.next();
		if (results.getMetaData().getColumnCount() > 0 && results.getInt(1) == 1)
			return true;
		
		return false;
	}
}
