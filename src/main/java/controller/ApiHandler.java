package controller;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import model.user.User;
import model.user.UsersDao;
import model.user.UsersMemoryImpl;
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
        methodAcceptedAdmin.add(ConstantsCommon.GET);
        methodAcceptedAdmin.add(ConstantsCommon.POST);
        methodAcceptedAdmin.add(ConstantsCommon.PUT);
        methodAcceptedAdmin.add(ConstantsCommon.OPTIONS);
        methodAcceptedAdmin.add(ConstantsCommon.DELETE);
        methodAcceptedUser = new ArrayList<String>();
        methodAcceptedUser.add(ConstantsCommon.GET);
    }
    public void handle(HttpExchange httpExchange) throws IOException {
        URI uri = httpExchange.getRequestURI();
 	        OutputStream os = httpExchange.getResponseBody();
        UsersDao users= UsersMemoryImpl.getInstance();
        //TODO Refactor isAdmin should return from Model?? and switch method
        boolean isAdmin= users.hasRights(httpExchange.getPrincipal().getUsername(), "admin");
        if (httpExchange.getRequestMethod().equals(ConstantsCommon.GET)){
            doGet(httpExchange, uri, os, users);
        } else if (httpExchange.getRequestMethod().equals(ConstantsCommon.POST) && isAdmin){
            doPost(httpExchange, uri, os);
        } else if (httpExchange.getRequestMethod().equals(ConstantsCommon.PUT) && isAdmin) {
            doPut(httpExchange, uri, users);
        } else if (httpExchange.getRequestMethod().equals(ConstantsCommon.DELETE) && isAdmin){
            doDelete(httpExchange, uri, users);
        } else if (httpExchange.getRequestMethod().equals(ConstantsCommon.OPTIONS)){
            doOptions(httpExchange, isAdmin);
        } else{
            httpExchange.sendResponseHeaders(ConstantsCommon.HTTP_STATUS_METHOD_NOT_ALLOWED, 0);
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
	private void doGet(HttpExchange httpExchange, URI uri, OutputStream os, UsersDao users) throws IOException {
		String[] depth = getDepth(uri);

		if (depth.length == 1 && depth[0].equals(ConstantsCommon.RESOURCES_ROOT)){
			//getAllUsers
			if (isAHeaderRequestingJson(httpExchange.getRequestHeaders())) {
				List<User> usersList = users.getAllUsers();
				byte[] bytes = ConversionUtils.beanJsonToBytes(usersList);
				httpExchange.getResponseHeaders().add(ConstantsCommon.CONTENT_TYPE, ConstantsCommon.APPLICATION_JSON);
				httpExchange.sendResponseHeaders(ConstantsCommon.HTTP_STATUS_OK, bytes.length);
				os.write(bytes);
			}
			else if (isaHeaderRequestingXML(httpExchange.getRequestHeaders())){
			List<User> usersList = users.getAllUsers();
				byte[] bytes = ConversionUtils.beanXMLToBytes(usersList);
				httpExchange.getResponseHeaders().add(ConstantsCommon.CONTENT_TYPE, ConstantsCommon.APPLICATION_XML);
				httpExchange.sendResponseHeaders(ConstantsCommon.HTTP_STATUS_OK, bytes.length);
				os.write(bytes);

			}
			else{
				httpExchange.sendResponseHeaders(ConstantsCommon.HTTP_STATUS_UNSUPPORTED_MEDIA_TYPE, 0);
			}
		} else if(depth.length == 2 && depth[0].equals(ConstantsCommon.RESOURCES_ROOT)){
		    //GetByUser
			if (isAHeaderRequestingJson(httpExchange.getRequestHeaders())) {

				User user = users.getUser(depth[1]);
				byte[] bytes = ConversionUtils.beanJsonToBytes(user);
				httpExchange.getResponseHeaders().add(ConstantsCommon.CONTENT_TYPE, ConstantsCommon.APPLICATION_JSON);
				httpExchange.sendResponseHeaders(ConstantsCommon.HTTP_STATUS_OK, bytes.length);
				os.write(bytes);
			}else if (isaHeaderRequestingXML(httpExchange.getRequestHeaders())){
				User user = users.getUser(depth[1]);
				byte[] bytes = ConversionUtils.beanXMLToBytes(user);
				httpExchange.getResponseHeaders().add(ConstantsCommon.CONTENT_TYPE, ConstantsCommon.APPLICATION_XML);
				httpExchange.sendResponseHeaders(ConstantsCommon.HTTP_STATUS_OK, bytes.length);
				os.write(bytes);
			}else{
				httpExchange.sendResponseHeaders(ConstantsCommon.HTTP_STATUS_UNSUPPORTED_MEDIA_TYPE, 0);
			}
		}
		else{
		    httpExchange.sendResponseHeaders(ConstantsCommon.HTTP_STATUS_RESOURCE_NOT_FOUND, 0);
		}
	}

	private boolean isaHeaderRequestingXML(Headers headers) {
		return headers !=null && headers.get(ConstantsCommon.CONTENT_TYPE).size()==1 && headers.get(ConstantsCommon.CONTENT_TYPE).get(0).equals(ConstantsCommon.APPLICATION_XML);
	}

	private static boolean isAHeaderRequestingJson(Headers headers) {
		return headers == null || null == headers.get(ConstantsCommon.CONTENT_TYPE) ||(headers.get(ConstantsCommon.CONTENT_TYPE).size()==1 && headers.get(ConstantsCommon.CONTENT_TYPE).get(0).equals(ConstantsCommon.APPLICATION_JSON));
	}

	/**
	 * @param httpExchange
	 * @param uri
	 * @param os
	 * @throws IOException
	 */
	private void doPost(HttpExchange httpExchange, URI uri, OutputStream os) throws IOException {
		String[] depth = getDepth(uri);

		if(depth.length == 2 && depth[0].equals(ConstantsCommon.RESOURCES_ROOT)) {
			if (isAHeaderRequestingJson(httpExchange.getRequestHeaders())) {
				String body = ConversionUtils.inputStreamToString(httpExchange.getRequestBody());
				byte[] bytes = ConversionUtils.stringJsonBeanToBytes(body, User.class);
				httpExchange.getResponseHeaders().add(ConstantsCommon.CONTENT_TYPE, ConstantsCommon.APPLICATION_JSON);
				httpExchange.sendResponseHeaders(ConstantsCommon.HTTP_STATUS_OK, bytes.length);
				os.write(bytes);
			}else  if (isaHeaderRequestingXML(httpExchange.getRequestHeaders())){
				String body = ConversionUtils.inputStreamToString(httpExchange.getRequestBody());
				byte[] bytes = ConversionUtils.stringXMLBeanToBytes(body, User.class);
				httpExchange.getResponseHeaders().add(ConstantsCommon.CONTENT_TYPE, ConstantsCommon.APPLICATION_JSON);
				httpExchange.sendResponseHeaders(ConstantsCommon.HTTP_STATUS_OK, bytes.length);
				os.write(bytes);
			}else{
				httpExchange.sendResponseHeaders(ConstantsCommon.HTTP_STATUS_UNSUPPORTED_MEDIA_TYPE, 0);
			}


		}else{
		    httpExchange.sendResponseHeaders(ConstantsCommon.HTTP_STATUS_RESOURCE_NOT_FOUND, 0);
		}
	}

	/**
	 * @param httpExchange
	 * @param uri
	 * @param users
	 * @throws IOException
	 */
	private void doPut(HttpExchange httpExchange, URI uri, UsersDao users) throws IOException {
		String body = ConversionUtils.inputStreamToString(httpExchange.getRequestBody());
		ObjectMapper mapper = new ObjectMapper();
		User user = mapper.readValue(body, User.class);
		if (users.addUser(user)){
		    httpExchange.getResponseHeaders().add(ConstantsCommon.LOCATION,uri.toString());
		    httpExchange.sendResponseHeaders(ConstantsCommon.HTTP_STATUS_CREATED, 0);
		}
		else{
		    httpExchange.sendResponseHeaders(ConstantsCommon.HTTP_STATUS_UNPROCESSABLE_ENTITY, 0);
		}
	}

	/**
	 * @param httpExchange
	 * @param uri
	 * @param users
	 * @throws IOException
	 */
	private void doDelete(HttpExchange httpExchange, URI uri, UsersDao users) throws IOException {
		String[] depth = getDepth(uri);
		if (depth.length == 1){
		    httpExchange.sendResponseHeaders(ConstantsCommon.HTTP_STATUS_FORBIDDEN, 0);
		}
		else if(depth.length == 2 && depth[0].equals(ConstantsCommon.RESOURCES_ROOT)){
		    if (users.deleteUser(depth[1])){
		        httpExchange.getResponseHeaders().add(ConstantsCommon.LOCATION,uri.toString());
		        httpExchange.sendResponseHeaders(ConstantsCommon.HTTP_STATUS_OK, 0);
		    }
		    else {
		        httpExchange.sendResponseHeaders(ConstantsCommon.HTTP_STATUS_UNPROCESSABLE_ENTITY, 0);
		    }

		}
	}

	private static String[] getDepth(URI uri) {
		return uri.toString().substring(ConstantsCommon.API_CONTEXT.length()).split("/");
	}

	/**
	 * @param httpExchange
	 * @param isAdmin
	 * @throws IOException
	 */
	private void doOptions(HttpExchange httpExchange, boolean isAdmin) throws IOException {
		Headers h = httpExchange.getResponseHeaders();
		if (isAdmin) {
		    h.add(ConstantsCommon.ALLOW, String.join(",", methodAcceptedAdmin));
		}else{
		    h.add(ConstantsCommon.ALLOW, String.join(",", methodAcceptedUser));
		}
		httpExchange.sendResponseHeaders(ConstantsCommon.HTTP_STATUS_OK, 0);
	}
	
}
