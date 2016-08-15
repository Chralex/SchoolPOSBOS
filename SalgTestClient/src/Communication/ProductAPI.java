package Communication;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Scanner;

import DatabaseModel.Tables.Product;

public class ProductAPI {
	
	public static ArrayList<Product> getProducts() throws UnknownHostException, IOException, ClassNotFoundException
	{
		ArrayList<Product> products = new ArrayList<Product>();
		
		Socket socket = new Socket("Localhost", Client.Client.PortNumber);

		if (socket.isConnected())
		{		    
			ObjectOutputStream pw = new ObjectOutputStream(socket.getOutputStream());
			pw.writeUTF("getproducts");
			pw.flush();
			
			ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
			products = (ArrayList<Product>) ois.readObject();
			System.out.println(products);
			ois.close();
		}
		
		socket.close();
		
		return products;
	}
	
	public static String sendSales() throws UnknownHostException, IOException, ClassNotFoundException
	{
		/*Socket socket = new Socket("Localhost", Client.Client.PortNumber);
		ArrayList<Product> sales = new ArrayList<Product>();
		
		sales.add{1,10,1,1};
		
		ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
		oos.writeObject(sales);
		oos.flush();
		oos.close();
		
		socket.close();
		*/
		ArrayList<Object> saleArr = new ArrayList<Object>();
		  
		  
		  
		Socket socket = new Socket("Localhost", Client.Client.PortNumber);
		PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);
		
		if (socket.isConnected())
		{		    
		pw.println("sendsales");
		pw.flush();
		
		}
		pw.close();
		socket.close();
		return "sendsales";
	}
	
	public static String sendPurchase() throws UnknownHostException, IOException, ClassNotFoundException
	{
		/*
		Socket socket = new Socket("Localhost", Client.Client.PortNumber);
		ArrayList<Product> purchase = new ArrayList<Product>();
		
		
		ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
		oos.writeObject(purchase);
		oos.flush();
		oos.close();
		
		socket.close();
		*/
		
		
		Socket socket = new Socket("Localhost", Client.Client.PortNumber);
		PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);
		
		if (socket.isConnected())
		{		    
		pw.println("sendpurchase");
		pw.flush();
		}
		pw.close();
		socket.close();
		return "sendpurchase";
	}
	
	
}
