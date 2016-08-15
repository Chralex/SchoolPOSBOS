package Client;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Scanner;

import Communication.ProductAPI;
import DatabaseModel.Tables.Product;

public class Client {

	public static int PortNumber = 2035;

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
	
		
		ArrayList<Product> productTest = Communication.ProductAPI.getProducts();
		for (Product p : productTest) {
			System.out.println(p);
		}
		
		//Socket socket = new Socket("Localhost", PortNumber);

		//PrintWriter pw;
		
		Scanner scan = new Scanner(System.in);
		
		System.out.println("Skriv kommando");
		String input = scan.next();
		scan.close();
		
		if (input.toLowerCase().trim().equals("a"))
		{
			System.out.println("product");
			/*ArrayList<Product>*/ productTest = Communication.ProductAPI.getProducts();
			for (Product p : productTest) {
				System.out.println(p);
			}
		}
		else if (input.toLowerCase().trim().equals("b"))
		{
			System.out.println("sale");
			
			
			
			ProductAPI.sendSales();
		}		
		else if (input.toLowerCase().trim().equals("c"))
		{
			System.out.println("purchase");
			ProductAPI.sendPurchase();
		}		
		else
		{
		System.out.println("else");
		}
		
		/*
		if (input.toLowerCase().trim().equals("getproducts") && socket.isConnected())
		{
		//if (socket.isConnected()) {
			
		    pw = new PrintWriter(socket.getOutputStream(), true);
		    pw.println(input);
			
			ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
			ArrayList<Product> products = (ArrayList<Product>) ois.readObject();
			System.out.println(products);
			//System.out.println(products.get(0).name);   // products.size();
			ois.close();

		}
		/*
		else if (input.toLowerCase().trim().equals("sendsales") && socket.isConnected())
		{
		pw = new PrintWriter(socket.getOutputStream(), true);
		    pw = new PrintWriter(socket.getOutputStream(), true);
		    pw.println(input);
		}
		else if (input.toLowerCase().trim().equals("sendpurchase") && socket.isConnected())
		{pw = new PrintWriter(socket.getOutputStream(), true);
			 pw = new PrintWriter(socket.getOutputStream(), true);
			 pw.println(input);
		}
		else{
			
			
		}
		*/
		//socket.close();
	}

}
