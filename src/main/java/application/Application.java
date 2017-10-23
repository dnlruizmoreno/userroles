package application;

import controller.UserRolesServer;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;


/**
 * Created by danielruizm on 10/20/17.
 */
public class Application {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());


    public static void main(String[] args) throws Exception {

        int port = 8030;
        if (args.length == 1 && NumberUtils.isDigits(args[0])){
            port = Integer.parseInt(args[0]);
        }
                // Start the server
        UserRolesServer simpleHttpServer = new UserRolesServer(port);
        simpleHttpServer.start();
        LOGGER.info("Server is started and listening on port {} ", port);
    }
}
