import controller.UserRolesServer;

/**
 * Created by danielruizm on 10/20/17.
 */
public class Application {

    public static void main(String[] args) throws Exception {

        // Create a new SimpleHttpServer
        UserRolesServer simpleHttpServer = new UserRolesServer(8030);

        // Start the server
        simpleHttpServer.start();
        System.out.println("Server is started and listening on port "+ 8030);
    }
}
