package controller;

import com.sun.net.httpserver.BasicAuthenticator;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpPrincipal;
import model.UsersMemoryImpl;


import java.net.URI;
import java.net.URISyntaxException;
import java.util.Base64;

/**
 * Created by danielruizm on 10/17/17.
 */
public class MyAuthenticator extends BasicAuthenticator{

    private static UsersMemoryImpl users = UsersMemoryImpl.getInstance();

    public MyAuthenticator(String realm){
        super(realm);
      }
    @Override
    public boolean checkCredentials(String username, String password) {
        System.err.println("Checking credentials");
        return users.exists(username);
    }

    public boolean hasRights(String username, String password, String role){
        System.err.println("Checking rights");
        return users.hasRights(username, role);
    }

    @Override
    public Result authenticate(HttpExchange t) {
        Headers rmap = t.getRequestHeaders();
        String auth = rmap.getFirst("Authorization");
        if(auth == null) {
            Headers map = t.getResponseHeaders();
            map.set("WWW-Authenticate", "Basic realm=\"" + this.realm + "\"");
            return new Retry(401);
        } else {
            int sp = auth.indexOf(32);
            if(sp != -1 && auth.substring(0, sp).equals("Basic")) {
                byte[] b = Base64.getDecoder().decode(auth.substring(sp + 1));
                String userpass = new String(b);
                int colon = userpass.indexOf(58);
                String uname = userpass.substring(0, colon);
                String pass = userpass.substring(colon + 1);

                if(this.checkCredentials(uname, pass)) {
                   return new Success(new HttpPrincipal(uname, this.realm));
                } else {
                    System.err.println("It was a problem in credentials and rights so error 401");
                    Headers map = t.getResponseHeaders();
                    map.set("WWW-Authenticate", "Basic realm=\"" + this.realm + "\"");
                    return new Failure(401);
                }
            } else {
                System.err.println("It was a problem in authentication so error 401");
                return new Failure(401);
            }
        }
    }


}
