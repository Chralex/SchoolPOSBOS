import java.io.*;
import java.net.*;
import java.sql.SQLException;
import java.util.ArrayList;

import DatabaseModel.DatabaseConnection;
import DatabaseModel.Tables.Product;

public class Server {
	
	public static int PortNumber = 2035;

	private static ServerSocket MyService; 
	
	Server() {
		// TODO Auto-generated method stub
		try {
			Run();
		}
		catch (IOException exception) {
			System.out.println(exception);
			if (MyService != null && MyService.isClosed() == false) {
				try  {
					MyService.close();
				}
				catch (IOException ioCloseException) {
					System.out.println(ioCloseException);		
				}
			}
		}
	}
	
	public static void Run() throws IOException
	{
		MyService = new ServerSocket(PortNumber);
		
    	DatabaseConnection db;
    	ArrayList<Product> products;

    	try  {
    		db = new DatabaseConnection(true);
    	}
    	catch (SQLException exception) {
    		System.out.println("Failed to connect to database - " + exception);
    		return;
    	}
    	
    	try {
    		 products = db.select(Product.class);
    	}
    	catch (SQLException exception) {
    		System.out.println("Failed to get products for client - " + exception);
    		return;
    	}
		
		// Next up send the individual OutputFileStream bytes to the client. And parse it there.
	    while(true)
	    {
		    Socket s=MyService.accept();
		    
		    ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
		    oos.writeObject(products);
		    oos.flush();
		    oos.close();
		    
		    s.close();
	    }
	}

}
