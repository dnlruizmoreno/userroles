package server;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.BasicAuthenticator;
import com.sun.net.httpserver.Filter;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpHandler;

@SuppressWarnings("restriction")
public class MyServer {

	
	private HttpServer httpServer;

	/**
	 * Instantiates a new simple http server.
	 *
	 * @param port the port
	 * @param context the context
	 * @param handler the handler
	 */
	public MyServer(int port, String context, Filter filter, MyHandler handler) {
		try {
			//Create HttpServer which is listening on the given port 
			httpServer = HttpServer.create(new InetSocketAddress(port), 0);
			//Create a new context for the given context and handler
			HttpContext httpContext = httpServer.createContext(context, handler);
			BasicAuthenticator authenticator =new BasicAuthenticator("get") {
		        @Override
		        public boolean checkCredentials(String user, String pwd) {
		            return user.equals("admin") && pwd.equals("password");
		        }
		    };
		    httpContext.setAuthenticator(authenticator);
			 // Add HttpRequestFilter to the context

			httpContext.getFilters().add(filter);

			//Create a default executor
			httpServer.setExecutor(null);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}


	/**
	 * Start.
	 */
	public void start() {
		this.httpServer.start();
	}
	
	public static void main(String[] args) throws Exception {

			// Create a new SimpleHttpServer
			MyServer simpleHttpServer = new MyServer(8050, "/prueba",new MyFilter(),new MyHandler());
			
			// Start the server
			simpleHttpServer.start();
			System.out.println("Server is started and listening on port "+ 8050);
	}
	
}
