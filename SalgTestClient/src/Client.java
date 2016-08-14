import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Scanner;

import DatabaseModel.Tables.Product;

public class Client {

	public static int PortNumber = 2035;

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		Socket socket = new Socket("Localhost", PortNumber);

		PrintWriter pw;
		
		Scanner scan = new Scanner(System.in);
		
		System.out.println("Skriv kommando");
		String input = scan.next();
		scan.close();
		
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
		//}
		}
		else if (input.toLowerCase().trim().equals("sendsales") && socket.isConnected())
		{
		    pw = new PrintWriter(socket.getOutputStream(), true);
		    pw.println(input);
		}
		else if (input.toLowerCase().trim().equals("sendpurchase") && socket.isConnected())
		{
			 pw = new PrintWriter(socket.getOutputStream(), true);
			 pw.println(input);
		}
		else{
			
			
		}
		socket.close();
	}

}
