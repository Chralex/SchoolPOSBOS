package DatabaseModel;

import java.sql.ResultSet;
import java.sql.SQLException;
//import com.mysql.jdbc.NotImplemented;
import DatabaseModel.Table;

public class DatabaseInitializer {

	private DatabaseModel model;

	public DatabaseInitializer(DatabaseModel model) {
		this.model = model;
	}

	public void InitializeDatabase(DatabaseConnection db) throws FieldNotInitializedException, SQLException {
		if (model.tables == null || model.tables.size() == 0)
			throw new FieldNotInitializedException(model);

		for (Table table : model.tables) {
			table.Create(db);
		}

		if (model.tableData != null && model.tableData.size() != 0)
			for (Table data : model.tableData) {
				db.insert(data);
			}

		db.connection.commit();
	}

	public static class FieldNotInitializedException extends Exception {

		FieldNotInitializedException(DatabaseModel model) {
			System.out.println(model);
		}

		private static final long serialVersionUID = 1L;

	}

	public static boolean IncompleteModel(DatabaseConnection db, DatabaseModel model)
			throws FieldNotInitializedException, SQLException {
		if (model.tables == null || model.tables.size() == 0)
			throw new FieldNotInitializedException(model);

		for (Table table : model.tables) {
			ResultSet result = db.connection.createStatement()
					.executeQuery("SHOW TABLES LIKE '" + table.getClass().getSimpleName() + "'");

			if (DatabaseConnection.getResultSize(result) == 0) {
				return true;
			}
		}

		return false;
	}

	public static void CleanupDatabase(DatabaseConnection db, DatabaseModel model) throws SQLException {
		for (Table table : model.tables) {

			ResultSet result = db.connection.createStatement()
					.executeQuery("SHOW TABLES LIKE '" + table.getClass().getSimpleName() + "'");

			if (DatabaseConnection.getResultSize(result) != 0) {
				table.Drop(db);
			}
		}
	}
}
