package Authentication;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;

import DatabaseModel.DatabaseConnection;
import DatabaseModel.SQLExpressions.SQLExpression;
import DatabaseModel.Tables.User;

public class AuthenticationService {
	
	/**
	 * Saves the authenticated clients AuthToken which is a serialized UUID.
	 * The key is the user's UserId. See User.id;
	 */
	public static HashMap<Integer, String> authenticationMap = new HashMap<Integer, String>();
	
	/**
	 * 
	 * @param login - Username of the user to authenticate
	 * @param password - Password of the user to authenticate
	 * @return A serialized UUID. Which serves as authentication token.
	 * @throws SecurityException
	 * @throws SQLException
	 */
	public static String login(String login, String password) throws SecurityException, SQLException  {
		DatabaseConnection db = new DatabaseConnection();
		User filterUser = new User();
		filterUser.login = login;
		filterUser.password = password;
		SQLExpression<User> filterExpression;
		
		try {
			filterExpression = new SQLExpression<User>(User.class).where(filterUser, new Field[] {
				User.class.getField("login"),
				User.class.getField("password")
			});
		}
		catch (NoSuchFieldException exception) {
			exception.printStackTrace();
			return "";
		}
		
		if (db.exists(filterExpression)) {
			String randomToken = UUID.randomUUID().toString();
			authenticationMap.put(db.select(filterExpression).get(0).id, randomToken);
			return randomToken;
		}
		
		return "";
	}
	
}
