import java.io.*;
import java.net.*;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import DatabaseModel.Tables.Product;

public class Client {

	public static int PortNumber = 2035;

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		Socket socket = new Socket("Localhost", PortNumber);

		if (socket.isConnected()) {
			ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
			ArrayList<Product> products = (ArrayList<Product>) ois.readObject();
			System.out.println(products);
			ois.close();
		}

		socket.close();
	}

}
