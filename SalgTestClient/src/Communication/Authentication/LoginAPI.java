package Communication.Authentication;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;

import DatabaseModel.Tables.User;

public class LoginAPI {

	private static String authToken;
	private static int userId;
	
	public static boolean Login(String username, String password) {
		Socket socket;
		
		try {
			socket = new Socket("Localhost", Client.Client.PortNumber);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}

		if (socket.isConnected())
		{		    
			ObjectOutputStream authObjectStream;
			
			try {
				authObjectStream = new ObjectOutputStream(socket.getOutputStream());
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				try {
					socket.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return false;
			}
			
			try {
				authObjectStream.writeUTF("authenticate");
				authObjectStream.flush();
			} catch (IOException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}

			User user = new User();
			user.login = username;
			user.password = password;
			
			try {
				authObjectStream.writeObject(user);
				authObjectStream.flush();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			ObjectInputStream ois;
			
			try {
				ois = new ObjectInputStream(socket.getInputStream());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
			
			try {
				if (ois.readBoolean()) {
					
					authToken = ois.readUTF();
					userId = ois.readInt();
					
					System.out.println(authToken);
					System.out.println(userId);
					
					ois.close();
					socket.close();
					return true;
				}
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		
		try {
			socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
				
		return false;
	}
	
}
