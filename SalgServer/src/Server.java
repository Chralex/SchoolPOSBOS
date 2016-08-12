import java.io.*;
import java.net.*;

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
