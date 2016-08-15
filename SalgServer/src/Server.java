import java.io.*;
import java.net.*;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import DatabaseModel.DatabaseConnection;
import DatabaseModel.Tables.Product;
import DatabaseModel.Tables.Purchase;
import DatabaseModel.Tables.Sale;

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
			System.out.println(exception);
			if (myService != null && myService.isClosed() == false) {
				try {
					myService.close();
				} catch (IOException ioCloseException) {
					System.out.println(ioCloseException);
				}
			}
		}
	}

	public void Run() throws IOException, SQLException {
		Date date;
		SimpleDateFormat sdf;
		String formattedDate;
		
		myService = new ServerSocket(portNumber);
		InputStream inp;// = myService.getInputStream();
	    

	    
	    BufferedReader br;// = new BufferedReader(new InputStreamReader(inp));



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
					System.out.println("Failed to connect to database - " + exception);
					return;
				}

				try {
					products = db.select(Product.class);
					db.closeConnection();
					
				} catch (SQLException exception) {
					System.out.println("Failed to get products for client - " + exception);
					db.closeConnection();
					return;
				}
			}
			
			 inp = s.getInputStream();
		    
		     br = new BufferedReader(new InputStreamReader(inp));			
			
		    String str1 = br.readLine();

		    if (str1 == null)
		    {
		    str1 = "Not legal command";
		    System.out.println(formattedDate + " From Klient: " + str1);
		    }
		    else
		    {
		    System.out.println(formattedDate + " From Klient: " + str1);
		    }

			if (str1.toLowerCase().trim().equals("getproducts"))
			{
			ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
			oos.writeObject(products);
			oos.flush();
			oos.close();
			

			}
			else if (str1.toLowerCase().trim().equals("sendsales"))
			{
				try {
					db = new DatabaseConnection();
					Sale saleInsert = new Sale();
					saleInsert.orderNumber = "TestSale";
					saleInsert.productId = 5;
					saleInsert.price = 120.3d;
					db.insert(saleInsert);
					
					db.closeConnection();
					
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					db.closeConnection();
					e.printStackTrace();
				}
				
				System.out.println(formattedDate + " Sales updated by Klient");
			}
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
			}
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
