package controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import model.User;
import model.UsersMemoryImpl;
import java.io.*;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by danielruizm on 10/17/17.
 */
public class ApiHandler implements HttpHandler {

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
        //TODO Refactor isAdmin and switch method
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
		String[] depth = uri.toString().substring("/api/".length()).split("/");
		if (depth.length == 1 && depth[0].equals("users")){
		    Gson gson =  new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setPrettyPrinting().create();
		    List<User> usersList =  users.getAllUsers();
		    byte[] bytes;
		    if (usersList != null) {
		        bytes= gson.toJson(usersList).getBytes();
		    }{
		        bytes= "{}".getBytes();
		    }
		    httpExchange.getResponseHeaders().add("Content-Type", "application/json");
		    httpExchange.sendResponseHeaders(200, bytes.length);
		    //First responseHeader!
		    os.write(bytes);
		} else if(depth.length == 2 && depth[0].equals("users")){
		    //GetByUser
		    Gson gson =  new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setPrettyPrinting().create();
		    User obtained=users.getUser(depth[1]);
		    byte[] bytes =gson.toJson(obtained).getBytes();
		    if (obtained != null) {
		        bytes= gson.toJson(obtained).getBytes();
		    }else{
		        bytes= "{}".getBytes();
		     }
		  //First responseHeader!
		    httpExchange.getResponseHeaders().add("Content-Type","application/json");
		    httpExchange.sendResponseHeaders(200, bytes.length);
		    os.write(bytes);
		}
		else{
		    httpExchange.sendResponseHeaders(404, 0);
		}
	}
	/**
	 * @param httpExchange
	 * @param uri
	 * @param os
	 * @throws IOException
	 */
	private void doPost(HttpExchange httpExchange, URI uri, OutputStream os) throws IOException {
		String[] depth = uri.toString().substring("/api/".length()).split("/");

		if(depth.length == 2 && depth[0].equals("users")) {

		    Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setPrettyPrinting().create();
		    String body = extractRequestBody(httpExchange.getRequestBody());
		    User user = gson.fromJson(body, User.class);
		    byte[] bytes = gson.toJson(user).getBytes();
		    httpExchange.getResponseHeaders().add("Content-Type","application/json");
		    httpExchange.sendResponseHeaders(200, bytes.length);
		    os.write(bytes);
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
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setPrettyPrinting().create();
		String body = extractRequestBody(httpExchange.getRequestBody());
		User user = gson.fromJson(body, User.class);
		if (users.addUser(user)){
		    httpExchange.getResponseHeaders().add("Location",uri.toString());
		    httpExchange.sendResponseHeaders(201, 0);
		}
		else{
		    httpExchange.sendResponseHeaders(422, 0);
		}
	}
	
	private String extractRequestBody(InputStream inputStream) throws IOException  {
		// TODO Auto-generated method stub
		
		BufferedReader bufferedReader = null;
		StringBuilder stringBuilder = new StringBuilder();
		String line;
		bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
		while ((line = bufferedReader.readLine()) != null) {
		    stringBuilder.append(line);
		}
		return stringBuilder.toString();
	}

	/**
	 * @param httpExchange
	 * @param uri
	 * @param users
	 * @throws IOException
	 */
	private void doDelete(HttpExchange httpExchange, URI uri, UsersMemoryImpl users) throws IOException {
		String[] depth = uri.toString().substring("/api/".length()).split("/");
		if (depth.length == 1){
		    httpExchange.sendResponseHeaders(403, 0);
		}
		else if(depth.length == 2 && depth[0].equals("users")){
		    if (users.deleteUser(depth[1])){
		        httpExchange.getResponseHeaders().add("Location",uri.toString());
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
		    h.add("Allow", String.join(",", methodAcceptedAdmin));
		}else{
		    h.add("Allow", String.join(",", methodAcceptedUser));
		}
		httpExchange.sendResponseHeaders(200, 0);
	}
	
}
