import java.io.*;
import java.net.*;
import java.sql.SQLException;
import java.util.ArrayList;

import DatabaseModel.DatabaseConnection;
import DatabaseModel.Tables.Product;

public class Server {

	public int portNumber = 2035;

	private ServerSocket myService;

	Server() {
		// TODO Auto-generated method stub
		try {
			Run();
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

	public void Run() throws IOException {
		myService = new ServerSocket(portNumber);

		DatabaseConnection db;
		ArrayList<Product> products;

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

		// Next up send the individual OutputFileStream bytes to the client. And
		// parse it there.
		while (true) {
			Socket s = myService.accept();

			ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
			oos.writeObject(products);
			oos.flush();
			oos.close();

			s.close();
		}
	}

}
