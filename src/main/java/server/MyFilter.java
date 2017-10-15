package server;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

import com.sun.net.httpserver.Filter;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;


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
		URI uri = exchange.getRequestURI();
		if (uri.getQuery().equals("redirect=true")){
			//redirect
			
	        Headers responseHeaders = exchange.getResponseHeaders();
	        responseHeaders.set("Location", "login");
	        exchange.sendResponseHeaders(302, 0);
		}
		
		
		// Chain the request to HttpRequestHandler
		chain.doFilter(exchange);
		
	}

}
