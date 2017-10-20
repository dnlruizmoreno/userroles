package controller;

import java.io.*;
import java.net.URI;
import java.util.HashMap;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import model.UsersMemoryImpl;

@SuppressWarnings("restriction")
public class WebHandler implements HttpHandler {


	private static final int HTTP_OK_STATUS = 200;
	public static final String PATHNAME_TEMPLATE = "src/main/view/pages/pageTemplate.html";
	public static final String PATHNAME_LOGIN = "src/main/view/pages/login.html";

	@SuppressWarnings("restriction")
	public void handle(HttpExchange httpExchange) throws IOException {
		OutputStream os = httpExchange.getResponseBody();
		httpExchange.getResponseHeaders().add(ApiHandler.CONTENT_TYPE, ApiHandler.APPLICATION_HTML);
		URI uri = httpExchange.getRequestURI();
		UsersMemoryImpl users= UsersMemoryImpl.getInstance();
		//TODO SESSION
		//Create a response form the request query parameters
		if (httpExchange.getRequestMethod().equals("GET") ){

			if (uri.toString().equals("/login")){

				File file = new File(PATHNAME_LOGIN);
				byte[] bytearray = new byte[(int) file.length()];
				FileInputStream fis = new FileInputStream(file);
				BufferedInputStream bis = new BufferedInputStream(fis);
				bis.read(bytearray, 0, bytearray.length);

				httpExchange.sendResponseHeaders(HTTP_OK_STATUS, file.length());
				//Write the response string
				os.write(bytearray, 0, bytearray.length);
				os.close();

			} else if(uri.toString().equals("/PAGE_1")){


				os = getOutputStreamPageLogged(httpExchange, "userpage1", "page 1", "./closession?userpage1");

			} else if((uri.toString().equals("PAGE_2"))){
				os = getOutputStreamPageLogged(httpExchange, "userpage2", "page 2", "./closession?userpage2");

			} else if(uri.toString().equals("PAGE_3")){
				os = getOutputStreamPageLogged(httpExchange, "userpage3", "page 3", "./closession?userpage3");

			} else if (uri.toString().startsWith("/closession")){
				String user= uri.getQuery();

				if (users.exists(user)){
					//si session existe cerrarla

				}
				httpExchange.getResponseHeaders().set("Location", "/login");
				httpExchange.sendResponseHeaders(302, 0);

				//TODO Cerrar Sesion y a login

			} else {
				httpExchange.sendResponseHeaders(404, "RESOURCE NOT FOUND".getBytes().length);
				os.write("RESOURCE NOT FOUND".getBytes());

			}
		} else if(httpExchange.getRequestMethod().equals("POST")){
			if (uri.toString().equals("/doLogin")){
				String [] params = (ConversionUtils.inputStreamToString(httpExchange.getRequestBody())).split("&");
				if (params.length == 2) {
					String user = params[0].split("=")[1];
					String pwd = params[1].split("=")[1];
					//TODO CREAR SESSION user y pwd
					if (users.exists(user, pwd)) {
						//TODO CREAR SESSION user y pwd
					} else {
						httpExchange.sendResponseHeaders(401, "LOGIN WAS NOT SUCCESSFUL".getBytes().length);
						os.write("LOGIN WAS NOT SUCCESSFUL".getBytes());
					}
				}
				else{
					httpExchange.sendResponseHeaders(403, 0);

				}

			} else{
				httpExchange.sendResponseHeaders(404, "RESOURCE NOT FOUND".getBytes().length);
				os.write("RESOURCE NOT FOUND".getBytes());
			}

		} else{
			httpExchange.sendResponseHeaders(405, 0);
		}
		os.close();
	}

	private OutputStream getOutputStreamPageLogged(HttpExchange httpExchange, String username, String page, String url) throws IOException {
		OutputStream os;File file = new File(PATHNAME_TEMPLATE);
		os = httpExchange.getResponseBody();
		FileInputStream fis = new FileInputStream(file);
		String html = ConversionUtils.inputStreamToString(fis);
		html= String.format(html,page,username,url);
		//Write the response string
		byte[] bytearray =html.getBytes();
		httpExchange.sendResponseHeaders(HTTP_OK_STATUS,html.getBytes().length);
		os.write(bytearray);
		os.close();
		return os;
	}

}