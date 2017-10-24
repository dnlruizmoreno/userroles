package controller;

import com.sun.net.httpserver.Authenticator;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import controller.utils.ConstantsCommon;
import model.user.User;
import model.user.UsersMemoryImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

import static controller.utils.ConstantsCommon.ROLE_PAGE_3;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;

/**
 * Created by danielruizm on 10/24/17.
 */
@RunWith(PowerMockRunner.class )
@PrepareForTest(HttpServer.class)
public class UserRolesServerTest {
      @Test
    public void start() throws Exception {
        //Preconditions
        HttpServer httpServer = Mockito.spy(HttpServer.class);
        HttpContext httpContext = Mockito.spy(HttpContext.class);
        PowerMockito.mockStatic(HttpServer.class);
        when(HttpServer.create(any(InetSocketAddress.class),anyInt())).thenReturn(httpServer);
        when(httpServer.createContext(eq("/api"),any(ApiHandler.class))).thenReturn(httpContext);
        when(httpContext.setAuthenticator(any(Authenticator.class))).thenReturn(Mockito.mock(Authenticator.class));

        //
        UserRolesServer userRolesServer = new UserRolesServer(8020);

        userRolesServer.start();


        verify(httpServer, times(1)).createContext(eq("/api"),any(ApiHandler.class));
          verify(httpServer, times(1)).createContext(eq("/"),any(WebHandler.class));
    }

}