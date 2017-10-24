package controller;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import controller.utils.ConstantsCommon;
import controller.utils.ConversionUtils;
import controller.utils.CryptoSessionUtils;
import junit.framework.TestCase;
import model.session.Session;
import model.session.SessionDao;
import model.session.SessionMemoryImpl;
import model.user.User;
import model.user.UsersMemoryImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static controller.utils.ConstantsCommon.ROLE_PAGE_3;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class )
@PrepareForTest(CryptoSessionUtils.class)
public class WebHandlerTest extends TestCase {



    List<User> usersTest;

    private HashMap<String,Session> sessionHashMap;

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
    private void createSession (){

        sessionHashMap = new HashMap<>();
        sessionHashMap.put("admin",new Session());
        SessionMemoryImpl sessionMemory = (SessionMemoryImpl) SessionMemoryImpl.getInstance();
        sessionMemory.setSessionHashMap(sessionHashMap);

    }

    @Test
    public void testHandleNoExistentUri() throws Exception {
        WebHandler componentToTest = new WebHandler();
        HttpExchange httpExchange = Mockito.spy(HttpExchange.class);
        when(httpExchange.getRequestURI()).thenReturn(new URI("NONEXISTENT"));
        when(httpExchange.getRequestMethod()).thenReturn(ConstantsCommon.GET);
        when(httpExchange.getResponseBody()).thenReturn(Mockito.mock(OutputStream.class));
        when(httpExchange.getResponseHeaders()).thenReturn(Mockito.mock(Headers.class));
        componentToTest.handle(httpExchange);
        verify(httpExchange).sendResponseHeaders(eq(ConstantsCommon.HTTP_STATUS_RESOURCE_NOT_FOUND), any(Long.class));
    }

    @Test
    public void testHandleLoginNoRedirect() throws Exception {
        WebHandler componentToTest = new WebHandler();
        HttpExchange httpExchange = Mockito.spy(HttpExchange.class);
        when(httpExchange.getRequestURI()).thenReturn(new URI(ConstantsCommon.LOGIN));
        when(httpExchange.getRequestMethod()).thenReturn(ConstantsCommon.GET);
        when(httpExchange.getResponseBody()).thenReturn(Mockito.mock(OutputStream.class));
        when(httpExchange.getResponseHeaders()).thenReturn(Mockito.mock(Headers.class));
        componentToTest.handle(httpExchange);
        verify(httpExchange).sendResponseHeaders(eq(ConstantsCommon.HTTP_STATUS_OK), any(Long.class));
    }

    @Test
    public void testHandleDoLoginNoParams() throws Exception {
        WebHandler componentToTest = new WebHandler();
        PowerMockito.mockStatic(CryptoSessionUtils.class);
        HttpExchange httpExchange = Mockito.spy(HttpExchange.class);
        ConversionUtils conversionUtils = Mockito.mock(ConversionUtils.class);
        when(httpExchange.getRequestURI()).thenReturn(new URI(ConstantsCommon.DO_LOGIN));
        when(httpExchange.getRequestMethod()).thenReturn(ConstantsCommon.POST);
        when(httpExchange.getResponseBody()).thenReturn(Mockito.mock(OutputStream.class));
        when(httpExchange.getResponseHeaders()).thenReturn(Mockito.mock(Headers.class));
        when(httpExchange.getRequestBody()).thenReturn(getInputStream());
        when(CryptoSessionUtils.encrypt(anyString(),anyString())).thenReturn("IDENCRYPTED");
        componentToTest.handle(httpExchange);
        verify(httpExchange).sendResponseHeaders(eq(ConstantsCommon.HTTP_STATUS_OK), any(Long.class));
    }

    @Test
    public void testHandleDoPageNoSession() throws Exception {
        WebHandler componentToTest = new WebHandler();
        HttpExchange httpExchange = Mockito.spy(HttpExchange.class);
        when(httpExchange.getRequestURI()).thenReturn(new URI(ConstantsCommon.PAGE+"_1"));
        when(httpExchange.getRequestMethod()).thenReturn(ConstantsCommon.GET);
        when(httpExchange.getResponseBody()).thenReturn(Mockito.mock(OutputStream.class));
        when(httpExchange.getResponseHeaders()).thenReturn(Mockito.mock(Headers.class));
        when(httpExchange.getRequestHeaders()).thenReturn(Mockito.mock(Headers.class));
        componentToTest.handle(httpExchange);
        verify(httpExchange).sendResponseHeaders(eq(ConstantsCommon.HTTP_STATUS_REDIRECT), any(Long.class));
    }
    @Test
    public void testHandleDoPageSession() throws Exception {
        createSession ();
        WebHandler componentToTest = new WebHandler();
        HttpExchange httpExchange = Mockito.spy(HttpExchange.class);
        PowerMockito.mockStatic(CryptoSessionUtils.class);
        when(httpExchange.getRequestURI()).thenReturn(new URI(ConstantsCommon.PAGE+"_1"));
        when(httpExchange.getRequestMethod()).thenReturn(ConstantsCommon.GET);
        when(httpExchange.getResponseBody()).thenReturn(Mockito.mock(OutputStream.class));
        when(httpExchange.getResponseHeaders()).thenReturn(Mockito.mock(Headers.class));
        when(httpExchange.getRequestHeaders()).thenReturn(getHeadersCookie());
        when(CryptoSessionUtils.decrypt(anyString(),anyString())).thenReturn("admin");
        componentToTest.handle(httpExchange);
        verify(httpExchange).sendResponseHeaders(eq(ConstantsCommon.HTTP_STATUS_OK), any(Long.class));
    }

    @Test
    public void testHandleDoPageUnexistentAndLogged() throws Exception {
        createSession ();
        WebHandler componentToTest = new WebHandler();
        HttpExchange httpExchange = Mockito.spy(HttpExchange.class);
        PowerMockito.mockStatic(CryptoSessionUtils.class);
        when(httpExchange.getRequestURI()).thenReturn(new URI(ConstantsCommon.PAGE+"_5"));
        when(httpExchange.getRequestMethod()).thenReturn(ConstantsCommon.GET);
        when(httpExchange.getResponseBody()).thenReturn(Mockito.mock(OutputStream.class));
        when(httpExchange.getResponseHeaders()).thenReturn(Mockito.mock(Headers.class));
        when(CryptoSessionUtils.decrypt(anyString(),anyString())).thenReturn("admin");
        when(httpExchange.getRequestHeaders()).thenReturn(Mockito.mock(Headers.class));
        componentToTest.handle(httpExchange);
        verify(httpExchange).sendResponseHeaders(eq(ConstantsCommon.HTTP_STATUS_REDIRECT), eq(0L));
    }

    @Test
    public void testHandleDoCloseSession() throws Exception {
        createSession ();
        WebHandler componentToTest = new WebHandler();
        HttpExchange httpExchange = Mockito.spy(HttpExchange.class);
        when(httpExchange.getResponseHeaders()).thenReturn(Mockito.mock(Headers.class));
        when(httpExchange.getRequestURI()).thenReturn(new URI(ConstantsCommon.PATH_CLOSESSION));
        when(httpExchange.getResponseBody()).thenReturn(Mockito.mock(OutputStream.class));
        when(httpExchange.getRequestMethod()).thenReturn(ConstantsCommon.GET);
        componentToTest.handle(httpExchange);
        verify(httpExchange).sendResponseHeaders(eq(ConstantsCommon.HTTP_STATUS_REDIRECT), eq(0L));
    }

    @Test
    public void testHandleDoCloseSessionWithPArams() throws Exception {
        createSession ();
        WebHandler componentToTest = new WebHandler();
        HttpExchange httpExchange = Mockito.spy(HttpExchange.class);
        when(httpExchange.getResponseHeaders()).thenReturn(Mockito.mock(Headers.class));
        when(httpExchange.getRequestURI()).thenReturn(new URI(ConstantsCommon.PATH_CLOSESSION+"?admin"));
        when(httpExchange.getResponseBody()).thenReturn(Mockito.mock(OutputStream.class));
        when(httpExchange.getRequestMethod()).thenReturn(ConstantsCommon.GET);
        componentToTest.handle(httpExchange);
        verify(httpExchange).sendResponseHeaders(eq(ConstantsCommon.HTTP_STATUS_REDIRECT), eq(0L));
    }


    private Headers getHeadersCookie() {
        Headers headers = new Headers();
        headers.add(ConstantsCommon.COOKIE, ConstantsCommon.SESSION_USER_ROLES+"=1234567890");
        return headers;
    }


    private InputStream getInputStream(){
        return new ByteArrayInputStream("user=admin&password=password".getBytes());

    }




}