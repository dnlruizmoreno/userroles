package controller;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpPrincipal;
import controller.utils.ConstantsCommon;
import junit.framework.TestCase;
import model.user.User;
import model.user.UsersMemoryImpl;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static controller.utils.ConstantsCommon.ROLE_PAGE_3;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ApiHandlerTest extends TestCase {

    List<User> usersTest;

    private void createUsers() {
        ArrayList<String> listRolesUno = new ArrayList<String>();
        listRolesUno.add(ConstantsCommon.ROLE_ADMIN);
        listRolesUno.add(ConstantsCommon.ROLE_PAGE_1);
        User uno = new User("admin","password",listRolesUno);
        ArrayList<String> listRolesDos = new ArrayList<String>();
        listRolesDos.add(ConstantsCommon.ROLE_PAGE_1);
        listRolesDos.add(ConstantsCommon.ROLE_PAGE_2);
        listRolesDos.add(ROLE_PAGE_3);
        User dos = new User("dos","password",listRolesDos);
        ArrayList<String> listRolesTres = new ArrayList<String>();
        listRolesTres.add(ConstantsCommon.ROLE_PAGE_2);
        User tres = new User("tres","password",listRolesTres);
        usersTest = new ArrayList();
        usersTest.add(uno);
        usersTest.add(dos);
        usersTest.add(tres);
        UsersMemoryImpl userDao = (UsersMemoryImpl) UsersMemoryImpl.getInstance();
        userDao.setUsers(usersTest);
    }


    @Test
    public void testHandleGetNonExistentURI() throws Exception {
        createUsers();
        ApiHandler componentToTest = new ApiHandler();
        HttpExchange httpExchange = Mockito.spy(HttpExchange.class);
        when(httpExchange.getRequestURI()).thenReturn(new URI("localhost:8030"));
        when(httpExchange.getPrincipal()).thenReturn(new HttpPrincipal("admin","password"));
        when(httpExchange.getRequestMethod()).thenReturn(ConstantsCommon.GET);
        when(httpExchange.getResponseBody()).thenReturn(Mockito.mock(OutputStream.class));
        componentToTest.handle(httpExchange);
        verify(httpExchange).sendResponseHeaders(eq(ConstantsCommon.HTTP_STATUS_RESOURCE_NOT_FOUND), eq(0L));
  }



}