package API;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import DatabaseModel.DatabaseConnection;
import DatabaseModel.SQLExpressions.SQLExpression;
import DatabaseModel.Tables.Product;
import DatabaseModel.Tables.Purchase;
import DatabaseModel.Tables.Sale;

public class SalesAPI {

	public static boolean processSales(Sale[] sales) throws SQLException {
		DatabaseConnection db = new DatabaseConnection();
		SQLExpression<Product> productsExpression = new SQLExpression<Product>(Product.class);
		
		for (Sale sale : sales) {
			try {
				Product product = new Product();
				product.id = sale.productId;
				productsExpression.or(product, new Field[] {
					Product.class.getField("id")
				});
			} catch (NoSuchFieldException | SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		HashMap<Integer, Product> productsMap = new HashMap<Integer, Product>();
		List<Product> productsList = db.select(productsExpression);
		
		for (Product product : productsList) {
			productsMap.put(product.id, product);
		}
	
		db.closeConnection();
		System.out.println(productsList);
		
		try {
			db = new DatabaseConnection();
			
			if (sales.length == 0)
				return false;
			
			Purchase purchase = new Purchase();
			purchase.orderDate = new Date();
			purchase.price = 0d;
			purchase.orderNumber = UUID.randomUUID().toString();
			
			for (Sale sale : sales) {
				sale.orderNumber = purchase.orderNumber;
				sale.price = productsMap.get(sale.productId).price;
				
				db.insert(sale);
				purchase.price += sale.price;
			}
			
			db.insert(purchase);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		db.closeConnection();
		return true;
	}
	
}
