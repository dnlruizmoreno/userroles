package controller;

import java.io.*;
import java.net.URI;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

@SuppressWarnings("restriction")
public class WebHandler implements HttpHandler {


	private static final int HTTP_OK_STATUS = 200;

	@SuppressWarnings("restriction")
	public void handle(HttpExchange t) throws IOException {

		//Create a response form the request query parameters
		URI uri = t.getRequestURI();

		Headers h = t.getResponseHeaders();
		h.add("Content-Type", "text/html");
		File file = new File("src/main/webapp/logged/page1.jsp");
		byte [] bytearray  = new byte [(int)file.length()];
		FileInputStream fis = new FileInputStream(file);
		BufferedInputStream bis = new BufferedInputStream(fis);
		bis.read(bytearray, 0, bytearray.length);
		//String response = createResponseFromQueryParams(uri);
		System.out.println("Response from MyHandler: ");
		//Set the response header status and length
		OutputStream os = t.getResponseBody();

		t.sendResponseHeaders(HTTP_OK_STATUS, file.length());
		//Write the response string

		os.write(bytearray,0,bytearray.length);
		os.close();
	}

}