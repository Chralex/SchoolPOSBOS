import java.io.*;
import java.net.*;
import java.sql.SQLException;
import java.util.ArrayList;

import DatabaseModel.DatabaseConnection;
import DatabaseModel.Tables.Product;

public class Server {
	
	public static int PortNumber = 2035;

	private static ServerSocket MyService; 
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			Server.Run();
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
    	
    	/*Testing serialization and deserialization*/
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
    	
    	FileOutputStream stream = new FileOutputStream("tmp/Product.ser"); // WHYYY DO YOU HAVE TO SERIALIZE IN THIS WAY JAVA; WHAT THE FUCK.
    	
		ObjectOutputStream objectStream = new ObjectOutputStream(stream);
		objectStream.writeObject(products);
		objectStream.close();
		stream.close();
		
		FileInputStream fileInputStream = new FileInputStream("tmp/Product.ser"); // Java serialization still sucks dunkey dick.
		ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
		
		try {
			ArrayList<Product> deserializedProduct = (ArrayList<Product>)objectInputStream.readObject();
			
			for (Product product : deserializedProduct) {
				System.out.print(product);
			}
		} 
		catch (ClassNotFoundException anExceptionThatShouldNeverBeThrown)
		{
			System.out.println(anExceptionThatShouldNeverBeThrown);
			return;
		}
		
		objectInputStream.close();
		fileInputStream.close();
		/*Testing ended*/
		
		// Next up send the individual OutputFileStream bytes to the client. And parse it there.
		boolean Running = true;
	    while(Running = true)
	    {
		    Socket s=MyService.accept();
		    System.out.println("Connection Etableret");
		    
		    
		    OutputStream obj=s.getOutputStream();
		    PrintStream ps=new PrintStream(obj);
		    
		    String str = "Hej Klient";
		    
		    ps.println(str);
	
		    ps.println("Bye");
		    ps.close();
		    MyService.close();
		    s.close();
	    }
	}

}
