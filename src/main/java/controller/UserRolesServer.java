package controller;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.BasicAuthenticator;
import com.sun.net.httpserver.HttpContext;
import model.user.UsersMemoryImpl;
import model.user.UsersDao;

@SuppressWarnings("restriction")
public class UserRolesServer {

	public static final String CONTEXT_API = "/api";
	public static final String CONTEXT_API_SLASH = CONTEXT_API+"/";
	private HttpServer httpServer;

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
			HttpContext httpContext = httpServer.createContext(CONTEXT_API, new ApiHandler());
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


}
