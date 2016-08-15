import java.sql.*;

import java.util.ArrayList;
import java.util.Date;
import Authentication.*;
import DatabaseModel.*;
import DatabaseModel.DatabaseInitializer.FieldNotInitializedException;
import DatabaseModel.Tables.*;

public class Program {

	public static DatabaseConnection connection;

	public static void main(String[] args) {
		System.out.println("===INITIALIZING====");
		InitializationChecks();
		System.out.println("===FINALIZED===");
	
		// Test login.
		try {
			String authToken = AuthenticationService.login("test", "test");
			System.out.println("Auth token for user Test: " + authToken);
		}
		catch (SQLException exception) {
			// There's an error in the SQL statement. What are we doing?
			exception.printStackTrace();
		}
		
		new Server();
	}

	public static void InitializationChecks() {
		try {
			Program.connection = new DatabaseConnection();
			ArrayList<Table> tables = new ArrayList<Table>();
			tables.add(new Licence());
			tables.add(new Purchase());
			tables.add(new Product());
			tables.add(new Sale());
			tables.add(new User());

			ArrayList<Product> products = new ArrayList<Product>();
			ArrayList<Table> tableData = new ArrayList<Table>();
			
			Product p1 = new Product();
			p1.name = "IP Phone";
			p1.price = 250.5d;
			p1.id = 1;
			Product p2 = new Product();
			p2.name = "HD Screen";
			p2.price = 1000.5d;
			p2.id = 2;
			Product p3 = new Product();
			p3.name = "Printer";
			p3.price = 100.5d;
			p3.id = 3;
			Product p4 = new Product();
			p4.name = "Desktop PC";
			p4.price = 3500d;
			p4.id = 4;
			Product p5 = new Product();
			p5.name = "Cisco Catalyst 2960";
			p5.price = 5000d;
			p5.id = 5;
			products.add(p1);
			products.add(p2);
			products.add(p3);
			products.add(p4);
			products.add(p5);
			
			int purhcases = (int)(Math.random() * 100);
			
			for (int i = purhcases; i != 0; i--) {
				int sales = (int)(Math.random() * 10);
				Purchase purchase = new Purchase();
				purchase.orderNumber = "ORDER" + i;
				purchase.orderDate = new Date();
				purchase.price = 0d;
				purchase.userId = 1;	// The userid of the initial system user.
				if (sales > 0) {
					for (int iterations = sales; iterations != 0; iterations--) {
						Sale sale = new Sale();
						Product randomProduct = products.get((int)(Math.random()*(products.size() - 1)));
						
						purchase.price += randomProduct.price;
						sale.productId = randomProduct.id;
						sale.orderNumber = purchase.orderNumber;
						sale.price = randomProduct.price;
						
						tableData.add(sale);
					}
					tableData.add(purchase);
				}
			}
			
			User u1 = new User();
			u1.password = "test";
			u1.login = "test";
			
			tableData.addAll(products);
			tableData.add(u1);
			
			DatabaseModel model = new DatabaseModel(tables, tableData);

			try {
				if (DatabaseInitializer.IncompleteModel(connection, model)) {
					System.out.println("Model invalidated - Reconstructing database model...");
					DatabaseInitializer.CleanupDatabase(connection, model);
					try {
						new DatabaseInitializer(model).InitializeDatabase(connection);
					} catch (FieldNotInitializedException exception) {
						// Well we're stupid...
						connection.closeConnection();
						System.out.println(exception);
						return;
					}
					System.out.println("Database reconstruction complete...");
				}
			} catch (FieldNotInitializedException exception) {
				// Eh, look at your code?
				connection.closeConnection();
				return;
			} catch (SQLException exception) {
				System.out.println(exception);
				connection.closeConnection();
				return;
			}
			connection.closeConnection();
		} catch (SQLException e) {
			try {
				connection.closeConnection();
			}
			catch (SQLException exception) {
				exception.printStackTrace();
			}
			e.printStackTrace();
		}
	}

	// SQLExpression<Product> expression = new
	// SQLExpression<Product>(Product.class);
	// try {
	// DatabaseConnection testConnection = new DatabaseConnection(true);
	// List<Product> products = testConnection.executeSelect(expression);
	//
	// for (Product product : products) {
	// System.out.println(product);
	// }
	// }
	// catch (SQLException exception ) {
	// System.out.println(exception);
	// return;
	// }
	// Product product = new Product();
	// product.id = 1;
	//
	// expression.where(product,
	// new Field[] {
	// Product.class.getDeclaredField("id")
	// }
	// );
	// DatabaseConnection db = new DatabaseConnection(true);
	//
	// List<Product> products = db.select(expression);
	//
	// for (Product p : products) {
	// System.out.println(product);
	// }
	//
	// db.delete(expression);
	//
	// products = db.select(expression);
	//
	// for (Product p : products) {
	// System.out.println(product);
	// }
	//
	// Product instance = new Product();
	// instance.id = 1;
	// instance.name = "Kaffemaskine";
	//
	// SQLExpression<Product> updateProduct = new
	// SQLExpression<Product>(Product.class);
	// updateProduct.select(new Field[] {
	// Product.class.getField("name")
	// });
	// updateProduct.where(instance, new Field[] {
	// Product.class.getField("id")
	// });
	//
	// try {
	// db.update(instance, updateProduct);
	// } catch (IllegalArgumentException | IllegalAccessException e) {
	// e.printStackTrace();
	// }

	// new Product().Drop(db);

	// db.closeConnection();

	// ArrayList<Product> test = connection.select(Product.class);
	//
	// connection.closeConnection();
	//
	// for (Product product : test) {
	// System.out.println(product);
	// }

	/*
	 * connection = new DatabaseConnection(true); try {
	 * connection.update(Product.class, new HashMap<Field, Object>() {{
	 * put(Product.class.getField("name"), "nope"); }}, new HashMap<Field,
	 * Object>() {{ put(Product.class.getField("id"), 1); }});
	 * 
	 * 
	 * for (Product product : connection.select(Product.class)) {
	 * System.out.println(product); }
	 * 
	 * connection.closeConnection(); } catch (Exception e) {
	 * System.out.println(e); }
	 */

	/*
	 * connection = new DatabaseConnection(true);
	 * 
	 * try { connection.delete(Product.class, new HashMap<Field, Object>() {{
	 * put(Product.class.getField("id"), 2); }});
	 * 
	 * for (Product product : connection.select(Product.class)) {
	 * System.out.println(product); }
	 * 
	 * connection.connection.rollback();// Do this so that TRANSACTIONS are
	 * disposed on the SQL server. Suspecting that non-commited and
	 * non-rolledback transactions stay in memory forever.
	 * connection.closeConnection(); } catch (Exception e) {
	 * System.out.println(e); }
	 */

	// connection = new DatabaseConnection(true);
	// try {
	//
	// Product p = new Product();
	// p.name = "Test";
	// p.price = 1d;
	//
	// connection.insert(p);
	//
	// ArrayList<Product> test = connection.select(Product.class);
	// for (Product product : test) {
	// System.out.println(product);
	// }
	// }
	// catch (Exception e) {
	// System.out.println(e);
	// }

	// ArrayList<Table> tables = new ArrayList<Table>();
	// tables.add(new Licence());
	//
	// DatabaseModel model = new DatabaseModel(tables);
	//
	// new Licence().Create(connection);
	// DatabaseInitializer.CleanupDatabase(
	// connection,
	// model
	// );
}
