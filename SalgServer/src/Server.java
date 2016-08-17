import java.io.*;
import java.net.*;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import DatabaseModel.DatabaseConnection;
import DatabaseModel.Tables.Product;
import DatabaseModel.Tables.Purchase;
import DatabaseModel.Tables.Sale;
import DatabaseModel.Tables.User;

public class Server {

	public int portNumber = 2035;

	private ServerSocket myService;

	Server() {
		// TODO Auto-generated method stub
		try {
			try {
				Run();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (IOException exception) {
			exception.printStackTrace();
			if (myService != null && myService.isClosed() == false) {
				try {
					myService.close();
				} catch (IOException ioCloseException) {
					ioCloseException.printStackTrace();
				}
			}
		}
	}

	public void Run() throws IOException, SQLException {
		Date date;
		SimpleDateFormat sdf;
		String formattedDate;
		
		myService = new ServerSocket(portNumber);

		DatabaseConnection db = new DatabaseConnection();
		ArrayList<Product> products = new ArrayList<Product>();
		db.closeConnection();
		/*
		try {
			db = new DatabaseConnection();
		} catch (SQLException exception) {
			System.out.println("Failed to connect to database - " + exception);
			return;
		}

		try {
			products = db.select(Product.class);
		} catch (SQLException exception) {
			System.out.println("Failed to get products for client - " + exception);
			return;
		}
		 */
		
		
		
		// Next up send the individual OutputFileStream bytes to the client. And
		// parse it there.
		while (true) {
			
			 date = new Date();
			 sdf = new SimpleDateFormat("MM/dd/yyyy h:mm:ss a");
			 formattedDate = sdf.format(date);
			
			
			
			Socket s = myService.accept();

			
			
			if (s.isConnected())
			{


				try {
					db = new DatabaseConnection();
				} catch (SQLException exception) {
					exception.printStackTrace();
					System.out.println("Failed to connect to database - " + exception);
					return;
				}

				try {
					products = db.select(Product.class);
					db.closeConnection();
					
				} catch (SQLException exception) {
					exception.printStackTrace();
					System.out.println("Failed to get products for client - " + exception);
					db.closeConnection();
					return;
				}
			}

			
		    ObjectInputStream initialReader = new ObjectInputStream(s.getInputStream());
		     
		    String str1 = initialReader.readUTF();

		    if (str1 == null)
		    {
		    str1 = "Not legal command";
		    System.out.println(formattedDate + " From Klient: " + str1);
		    }
		    else
		    {
		    System.out.println(formattedDate + " From Klient: " + str1);
		    }

		    if (str1.toLowerCase().trim().equals("authenticate")) {
		    	System.out.println("Authentication started.");
		    	
		    	ObjectOutputStream authenticationResponseStream = new ObjectOutputStream(s.getOutputStream());
		    	
		    	try {
					User user = (User)initialReader.readObject();
					
					String authToken = Authentication.AuthenticationService.login(user.login, user.password);
			
					if (!authToken.equals("")) {
						int userId =  Authentication.AuthenticationService.reverseAuth.get(authToken);
						
						authenticationResponseStream.writeBoolean(true);
						authenticationResponseStream.flush();
						
						authenticationResponseStream.writeUTF(authToken);
						authenticationResponseStream.flush();
						
						authenticationResponseStream.writeInt(userId);
						authenticationResponseStream.flush();
					}
					else {
						authenticationResponseStream.writeBoolean(false);
						authenticationResponseStream.flush();
					}
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					s.close();
				}		    	
		    }
		    else if (str1.toLowerCase().trim().equals("getproducts"))
			{
			ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
			oos.writeObject(products);
			oos.flush();
			oos.close();
			

			}
			else if (str1.toLowerCase().trim().equals("sendsales"))
			{
				List<Integer> soldProducts = new ArrayList<Integer>();
	
				ObjectInputStream oos = new ObjectInputStream(s.getInputStream());
				try {
					soldProducts = (List<Integer>)oos.readObject();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				oos.close();
				
				Sale[] sales = new Sale[soldProducts.size()];
				
				for (Integer i = soldProducts.size() - 1; i >= 0; i--) {
					sales[i] = new Sale();
					sales[i].productId = soldProducts.get(i);
				}
				
				API.SalesAPI.processSales(sales);
				
				System.out.println(formattedDate + " Sales updated by Klient");
			}/*
			else if (str1.toLowerCase().trim().equals("sendpurchase")) //??
			{
				try {
					db = new DatabaseConnection();
					Purchase purchaseInsert = new Purchase();
					purchaseInsert.orderNumber = "TestPurchase";
					purchaseInsert.price = 25.33d;
					purchaseInsert.orderDate = date;
					db.insert(purchaseInsert);
					
					db.closeConnection();
					
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					db.closeConnection();
					e.printStackTrace();
				}

				
				System.out.println(formattedDate + " Purchases updated by Klient");
			}*/
			else {

			}
			/*
			try {
				db.closeConnection();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			*/
			
			s.close();
		}
	}

}
