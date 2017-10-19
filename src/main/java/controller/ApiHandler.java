package controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import model.User;
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
        Users users= Users.getInstance();
        boolean isAdmin= users.hasRights(httpExchange.getPrincipal().getUsername(), "admin");

        System.err.println(httpExchange.getPrincipal());
        if (httpExchange.getRequestMethod().equals("GET")){
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
                }{
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


        } else if (httpExchange.getRequestMethod().equals("POST") && isAdmin){
            String[] depth = uri.toString().substring("/api/".length()).split("/");

            if(depth.length == 2 && depth[0].equals("users")) {

                Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setPrettyPrinting().create();
                BufferedReader bufferedReader = null;
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                bufferedReader = new BufferedReader(new InputStreamReader(httpExchange.getRequestBody()));
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line);
                }
                stringBuilder.toString();
                User user = gson.fromJson(stringBuilder.toString(), User.class);
                byte[] bytes = gson.toJson(user).getBytes();
                    httpExchange.getResponseHeaders().add("Content-Type","application/json;charset=UTF-8");
                    httpExchange.sendResponseHeaders(200, bytes.length);
                    os.write(bytes);
            }else{
                httpExchange.sendResponseHeaders(404, 0);
            }
            //update

//            httpExchange.sendResponseHeaders(201, 0);¿??
        } else if (httpExchange.getRequestMethod().equals("PUT") && isAdmin) {
            //Create or full update
                Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setPrettyPrinting().create();
                BufferedReader bufferedReader = null;
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                bufferedReader = new BufferedReader(new InputStreamReader(httpExchange.getRequestBody()));
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line);
                }
                stringBuilder.toString();
                User user = gson.fromJson(stringBuilder.toString(), User.class);
                if (users.addUser(user)){
                    httpExchange.getResponseHeaders().add("Location",uri.toString());
                    httpExchange.sendResponseHeaders(201, 0);
                }
                else{
                    httpExchange.sendResponseHeaders(422, 0);
                }

        }   else if (httpExchange.getRequestMethod().equals("DELETE") && isAdmin){
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
                //delete


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

        os.close();
    }
}
