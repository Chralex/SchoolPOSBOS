package Authentication;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;

import DatabaseModel.DatabaseConnection;
import DatabaseModel.SQLExpressions.SQLExpression;
import DatabaseModel.Tables.User;

public class Login {
	
	public static HashMap<Integer, String> authenticationMap = new HashMap<Integer, String>();
	
	public static String login(String login, String password) throws NoSuchFieldException, SecurityException, SQLException  {
		DatabaseConnection db = new DatabaseConnection();
		User filterUser = new User();
		filterUser.login = login;
		filterUser.password = password;
		
		SQLExpression<User> filterExpression = new SQLExpression<User>(User.class).where(filterUser, new Field[] {
			User.class.getField("login"),
			User.class.getField("password")
		});
		
		
		if (db.exists(filterExpression)) {
			String randomToken = UUID.randomUUID().toString();
			authenticationMap.put(db.select(filterExpression).get(0).id, randomToken);
			return randomToken;
		}
		
		return "";
	}
	
}
