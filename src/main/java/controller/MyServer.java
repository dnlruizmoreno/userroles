package controller;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.BasicAuthenticator;
import com.sun.net.httpserver.Filter;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import model.UsersMemoryImpl;
import model.UsersDao;

@SuppressWarnings("restriction")
public class MyServer {

	
	private HttpServer httpServer;

	/**
	 * Instantiates a new simple http server.
	 *
	 * @param port the port
	 */
	public MyServer(int port) {
		try {
			//Create HttpServer which is listening on the given port 
			httpServer = HttpServer.create(new InetSocketAddress(port), 0);
			//Create a new context for the given context and handler
			HttpContext httpContext = httpServer.createContext("/api", new ApiHandler());
			HttpContext httpContext2 = httpServer.createContext("/", new MyHandler());
			//TODO review authenticator instead of Basic
			//BasicAuthenticator authenticator =new MyAuthenticator("Realm");
		    httpContext.setAuthenticator(new BasicAuthenticator("ApiRealm") {
				@Override
				public boolean checkCredentials(String user, String pwd) {
					UsersDao users = UsersMemoryImpl.getInstance();
					return users.exists(user, pwd);
				}
			});
			 // Add HttpRequestFilter to the context
			//httpContext.getFilters().add(filter);
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
			MyServer simpleHttpServer = new MyServer(8030);
			
			// Start the server
			simpleHttpServer.start();
			System.out.println("Server is started and listening on port "+ 8030);
	}
	
}
