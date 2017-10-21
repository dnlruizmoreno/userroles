package controller;

import java.io.IOException;

import com.sun.net.httpserver.Filter;
import com.sun.net.httpserver.HttpExchange;

//TODO borramos?
@SuppressWarnings("restriction")
public class MyFilter extends Filter{

    

    @Override
	public String description() {
		// TODO Auto-generated method stub
		return "Filtro autenticacion";
	}

	@Override
	public void doFilter(HttpExchange exchange, Chain chain) throws IOException {
		// Create a string form the request query parameters

		/*URI uri = exchange.getRequestURI();
		//exchange.getPrincipal().getUsername()
		Session userSession = (Session)exchange.getAttribute("session");

		//TODO review if key basic is valid as a id for sessions hashmap
		if (null == exchange.getAttribute("session")){
		   //No session, redirect to login and later to proper page
			if ( exchange.getRequestHeaders().get("Authorization") != null) {
				String user = (String) exchange.getRequestHeaders().get("Authorization").get(0);
				sessionHashMap.remove(user);
			}
			Headers responseHeaders = exchange.getResponseHeaders();
			System.err.println("no session, so we take out to login in order we can log it");
			responseHeaders.set("Location", "login.html");
			exchange.sendResponseHeaders(302, 0);

		}
		else if (userSession.isAlive()){
		    //refresh lastAction and continue to proper page, no action related to.
			userSession.refresh();

        }
		else{
			//session expired, remove session and redirect to login
			String user = (String) exchange.getRequestHeaders().get("Authorization").get(0);
			System.err.println("session expired, so we take out to login in order we can log it");
			sessionHashMap.remove(user);
            Headers responseHeaders = exchange.getResponseHeaders();
            responseHeaders.set("Location", "login.html");
            exchange.sendResponseHeaders(302, 0);
        }
*/
		chain.doFilter(exchange);
		// Chain the request to HttpRequestHandler

		
	}

}
