package Client;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Scanner;

import Communication.ProductAPI;
import DatabaseModel.Tables.Product;
import UI.AuthenticationScreen;

public class Client {

	public static int PortNumber = 2035;

	public static void main(String[] args) throws Exception {
		new AuthenticationScreen();	
	}

}
