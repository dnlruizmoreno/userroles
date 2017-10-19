package controller;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;

import com.sun.net.httpserver.BasicAuthenticator;
import com.sun.net.httpserver.Filter;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import model.UsersMemoryImpl;
import model.Session;
import model.UsersDao;

@SuppressWarnings("restriction")
public class UserRolesServer {

	private HttpServer httpServer;
	//TODO Singleton pattern
	private static HashMap<String, Session> sessionHashMap = new HashMap<String, Session>();
	/**
	 * Instantiates a new simple http server.
	 *
	 * @param port the port
	 */
	public UserRolesServer(int port) {
		try {
			//Create HttpServer which is listening on the given port 
			httpServer = HttpServer.create(new InetSocketAddress(port), 0);
			//Create a new context for the given context and handler
			HttpContext httpContext = httpServer.createContext("/api", new ApiHandler());
			//Create authenticator associated to api
			httpContext.setAuthenticator(new BasicAuthenticator("ApiRealm") {
				@Override
				public boolean checkCredentials(String user, String pwd) {
					UsersDao users = UsersMemoryImpl.getInstance();
					return users.exists(user, pwd);
				}
			});
		    HttpContext httpContext2 = httpServer.createContext("/", new WebHandler());
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
	//TODO refactor main to another class?
	public static void main(String[] args) throws Exception {
		
		// Create a new SimpleHttpServer
		UserRolesServer simpleHttpServer = new UserRolesServer(8030);
		
		// Start the server
		simpleHttpServer.start();
		System.out.println("Server is started and listening on port "+ 8030);
	}
}
