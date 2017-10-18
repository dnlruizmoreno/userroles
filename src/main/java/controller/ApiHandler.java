package controller;

import com.google.gson.Gson;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import model.Users;

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
        Users users= new Users();
        boolean isAdmin= users.hasRights(httpExchange.getPrincipal().getUsername(), "admin");

        System.err.println(httpExchange.getPrincipal());
        if (httpExchange.getRequestMethod().equals("GET")){
            String[] depth = uri.toString().substring("/api/".length()).split("/");
            if (depth.length == 1 && depth[0].equals("users")){
                Gson gson = new Gson();
                httpExchange.setAttribute("content-type","application/json");
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(bos);
                oos.writeObject(gson.toJson(users.getAllUsers()));
                byte[] bytes = bos.toByteArray();
                os.write(bytes);
                        //
                httpExchange.sendResponseHeaders(200, 0);
            } else if(depth.length == 2 && depth[0].equals("users")){
                //GetByUser
                httpExchange.sendResponseHeaders(200, 0);
            }

            else{
                httpExchange.sendResponseHeaders(400, 0);
            }


        } else if (httpExchange.getRequestMethod().equals("POST") && isAdmin){
            //update

            httpExchange.sendResponseHeaders(201, 0);
        } else if (httpExchange.getRequestMethod().equals("PUT") && isAdmin) {
            //Create or full update
            httpExchange.sendResponseHeaders(200, 0);
        }   else if (httpExchange.getRequestMethod().equals("DELETE") && isAdmin){
                //delete
                httpExchange.sendResponseHeaders(200, 0);

        } else if (httpExchange.getRequestMethod().equals("OPTIONS")){
            Headers h = httpExchange.getResponseHeaders();
            if (isAdmin) {
                h.add("Allow", String.join(",", methodAcceptedAdmin));
            }else{
                h.add("Allow", String.join(",", methodAcceptedUser));
            }
            httpExchange.sendResponseHeaders(200, 0);
        } else{
            httpExchange.sendResponseHeaders(405, 0);
        }
        /*Headers h = httpExchange.getResponseHeaders();
        h.add("Content-Type", "text/html");
        File file = new File("src/main/webapp/logged/page1.jsp");

        FileInputStream fis = new FileInputStream(file);
        BufferedInputStream bis = new BufferedInputStream(fis);
        bis.read(bytearray, 0, bytearray.length);
         System.out.println("Response from ApiHandler: ");
        //Set the response header status and length*/


       // httpExchange.sendResponseHeaders(200, file.length());
        //Write the response string
        os.close();
    }
}
