import java.io.*;
import java.net.*;
public class Client {

public static int PortNumber = 2035;

	public static void main(String[] args) throws Exception{
		// TODO Auto-generated method stub
		

	    Socket MyClient = new Socket("Localhost", PortNumber);
	    InputStream obj = MyClient.getInputStream();
	    
	    

	    
	    BufferedReader br = new BufferedReader(new InputStreamReader(obj));
	    
	    
	    
	    
	    String str;
	    
	    while((str=br.readLine())!=null){
	    	System.out.println("From Server: " + str);
	    }
	    
	    br.close();
	    MyClient.close();
	    
	}

}
