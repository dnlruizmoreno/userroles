package controller;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import model.User;
import model.UsersMemoryImpl;
import java.io.*;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by danielruizm on 10/17/17.
 */
public class ApiHandler implements HttpHandler {


	public static final String CONTENT_TYPE = "Content-Type";
	public static final String APPLICATION_JSON = "application/json";
	public static final String APPLICATION_XML = "application/xml";
	public static final String APPLICATION_HTML = "text/html";
	public static final String RESOURCES_ROOT = "users";
	public static final String API_CONTEXT = "/api/";
	public static final String LOCATION = "Location";
	public static final String ALLOW = "Allow";

	private static List<String> methodAcceptedAdmin;
    private static List<String> methodAcceptedUser;
    static {
        methodAcceptedAdmin = new ArrayList<String>();
        methodAcceptedAdmin.add("GET");
        methodAcceptedAdmin.add("POST");
        methodAcceptedAdmin.add("PUT");
        methodAcceptedAdmin.add("OPTIONS");
        methodAcceptedAdmin.add("DELETED");
        methodAcceptedUser = new ArrayList<String>();
        methodAcceptedUser.add("GET");
    }
    public void handle(HttpExchange httpExchange) throws IOException {
        URI uri = httpExchange.getRequestURI();
 	        OutputStream os = httpExchange.getResponseBody();
        UsersMemoryImpl users= UsersMemoryImpl.getInstance();
        //TODO Refactor isAdmin should return from Model?? and switch method
        boolean isAdmin= users.hasRights(httpExchange.getPrincipal().getUsername(), "admin");
        if (httpExchange.getRequestMethod().equals("GET")){
            doGet(httpExchange, uri, os, users);
        } else if (httpExchange.getRequestMethod().equals("POST") && isAdmin){
            doPost(httpExchange, uri, os);
        } else if (httpExchange.getRequestMethod().equals("PUT") && isAdmin) {
            doPut(httpExchange, uri, users);
        } else if (httpExchange.getRequestMethod().equals("DELETE") && isAdmin){
            doDelete(httpExchange, uri, users);
        } else if (httpExchange.getRequestMethod().equals("OPTIONS")){
            doOptions(httpExchange, isAdmin);
        } else{
            httpExchange.sendResponseHeaders(405, 0);
        }

        os.close();
    }
    /**
	 * @param httpExchange
	 * @param uri
	 * @param os
	 * @param users
	 * @throws IOException
	 */
	private void doGet(HttpExchange httpExchange, URI uri, OutputStream os, UsersMemoryImpl users) throws IOException {
		String[] depth = uri.toString().substring(API_CONTEXT.length()).split("/");

		if (depth.length == 1 && depth[0].equals(RESOURCES_ROOT)){
			//getAllUsers
			if (isAHeaderRequestingJson(httpExchange.getRequestHeaders())) {
				List<User> usersList = users.getAllUsers();
				byte[] bytes = ConversionUtils.beanJsonToBytes(usersList);
				httpExchange.getResponseHeaders().add(CONTENT_TYPE, APPLICATION_JSON);
				httpExchange.sendResponseHeaders(200, bytes.length);
				os.write(bytes);
			}
			else if (isaHeaderRequestingXML(httpExchange.getRequestHeaders())){
			List<User> usersList = users.getAllUsers();
				byte[] bytes = ConversionUtils.beanXMLToBytes(usersList);
				httpExchange.getResponseHeaders().add(CONTENT_TYPE, APPLICATION_XML);
				httpExchange.sendResponseHeaders(200, bytes.length);
				os.write(bytes);

			}
			else{
				httpExchange.sendResponseHeaders(415, 0);
			}
		} else if(depth.length == 2 && depth[0].equals(RESOURCES_ROOT)){
		    //GetByUser
			if (isAHeaderRequestingJson(httpExchange.getRequestHeaders())) {

				User user = users.getUser(depth[1]);
				byte[] bytes = ConversionUtils.beanJsonToBytes(user);
				httpExchange.getResponseHeaders().add(CONTENT_TYPE, APPLICATION_JSON);
				httpExchange.sendResponseHeaders(200, bytes.length);
				os.write(bytes);
			}else if (isaHeaderRequestingXML(httpExchange.getRequestHeaders())){
				User user = users.getUser(depth[1]);
				byte[] bytes = ConversionUtils.beanXMLToBytes(user);
				httpExchange.getResponseHeaders().add(CONTENT_TYPE, APPLICATION_XML);
				httpExchange.sendResponseHeaders(200, bytes.length);
				os.write(bytes);
			}else{
				httpExchange.sendResponseHeaders(415, 0);
			}
		}
		else{
		    httpExchange.sendResponseHeaders(404, 0);
		}
	}

	private boolean isaHeaderRequestingXML(Headers headers) {
		return headers !=null && headers.get(CONTENT_TYPE).size()==1 && headers.get(CONTENT_TYPE).get(0).equals(APPLICATION_XML);
	}

	private static boolean isAHeaderRequestingJson(Headers headers) {
		return headers == null || null == headers.get(CONTENT_TYPE) ||(headers.get(CONTENT_TYPE).size()==1 && headers.get(CONTENT_TYPE).get(0).equals(APPLICATION_JSON));
	}

	/**
	 * @param httpExchange
	 * @param uri
	 * @param os
	 * @throws IOException
	 */
	private void doPost(HttpExchange httpExchange, URI uri, OutputStream os) throws IOException {
		String[] depth = uri.toString().substring(API_CONTEXT.length()).split("/");

		if(depth.length == 2 && depth[0].equals(RESOURCES_ROOT)) {
			if (isAHeaderRequestingJson(httpExchange.getRequestHeaders())) {
				String body = ConversionUtils.inputStreamToString(httpExchange.getRequestBody());
				byte[] bytes = ConversionUtils.stringJsonBeanToBytes(body, User.class);
				httpExchange.getResponseHeaders().add(CONTENT_TYPE, APPLICATION_JSON);
				httpExchange.sendResponseHeaders(200, bytes.length);
				os.write(bytes);
			}else  if (isaHeaderRequestingXML(httpExchange.getRequestHeaders())){
				String body = ConversionUtils.inputStreamToString(httpExchange.getRequestBody());
				byte[] bytes = ConversionUtils.stringXMLBeanToBytes(body, User.class);
				httpExchange.getResponseHeaders().add(CONTENT_TYPE, APPLICATION_JSON);
				httpExchange.sendResponseHeaders(200, bytes.length);
				os.write(bytes);
			}else{
				httpExchange.sendResponseHeaders(415, 0);
			}


		}else{
		    httpExchange.sendResponseHeaders(404, 0);
		}
	}

	/**
	 * @param httpExchange
	 * @param uri
	 * @param users
	 * @throws IOException
	 */
	private void doPut(HttpExchange httpExchange, URI uri, UsersMemoryImpl users) throws IOException {
		String body = ConversionUtils.inputStreamToString(httpExchange.getRequestBody());
		ObjectMapper mapper = new ObjectMapper();
		User user = mapper.readValue(body, User.class);
		if (users.addUser(user)){
		    httpExchange.getResponseHeaders().add(LOCATION,uri.toString());
		    httpExchange.sendResponseHeaders(201, 0);
		}
		else{
		    httpExchange.sendResponseHeaders(422, 0);
		}
	}

	/**
	 * @param httpExchange
	 * @param uri
	 * @param users
	 * @throws IOException
	 */
	private void doDelete(HttpExchange httpExchange, URI uri, UsersMemoryImpl users) throws IOException {
		String[] depth = uri.toString().substring(API_CONTEXT.length()).split("/");
		if (depth.length == 1){
		    httpExchange.sendResponseHeaders(403, 0);
		}
		else if(depth.length == 2 && depth[0].equals(RESOURCES_ROOT)){
		    if (users.deleteUser(depth[1])){
		        httpExchange.getResponseHeaders().add(LOCATION,uri.toString());
		        httpExchange.sendResponseHeaders(200, 0);
		    }
		    else {
		        httpExchange.sendResponseHeaders(422, 0);
		    }

		}
	}
	/**
	 * @param httpExchange
	 * @param isAdmin
	 * @throws IOException
	 */
	private void doOptions(HttpExchange httpExchange, boolean isAdmin) throws IOException {
		Headers h = httpExchange.getResponseHeaders();
		if (isAdmin) {
		    h.add(ALLOW, String.join(",", methodAcceptedAdmin));
		}else{
		    h.add(ALLOW, String.join(",", methodAcceptedUser));
		}
		httpExchange.sendResponseHeaders(200, 0);
	}
	
}
