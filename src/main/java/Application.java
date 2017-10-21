import controller.UserRolesServer;
import org.apache.commons.lang3.math.NumberUtils;

/**
 * Created by danielruizm on 10/20/17.
 */
public class Application {

    public static void main(String[] args) throws Exception {

        // Create a new SimpleHttpServer
        UserRolesServer simpleHttpServer;
        if (args[0] != null && NumberUtils.isDigits(args[0])){
            simpleHttpServer = new UserRolesServer(Integer.parseInt(args[0]));
        }
        else{
            simpleHttpServer = new UserRolesServer(8030);
        }
        // Start the server
        simpleHttpServer.start();
        System.out.println("Server is started and listening on port "+ 8030);
    }
}
