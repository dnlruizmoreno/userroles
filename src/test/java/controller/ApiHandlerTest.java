package controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpPrincipal;
import controller.utils.ConstantsCommon;
import controller.utils.ConversionUtils;
import junit.framework.TestCase;
import model.user.User;
import model.user.UsersMemoryImpl;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static controller.utils.ConstantsCommon.ROLE_PAGE_3;
import static org.mockito.Matchers.any;
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

    // Start testing GET//
    @Test
    public void testHandleGetNonExistentURI() throws Exception {
        ApiHandler componentToTest = new ApiHandler();
        HttpExchange httpExchange = Mockito.spy(HttpExchange.class);
        when(httpExchange.getRequestURI()).thenReturn(new URI("NONEXISTENT"));
        when(httpExchange.getPrincipal()).thenReturn(new HttpPrincipal("admin","password"));
        when(httpExchange.getRequestMethod()).thenReturn(ConstantsCommon.GET);
        when(httpExchange.getResponseBody()).thenReturn(Mockito.mock(OutputStream.class));
        componentToTest.handle(httpExchange);
        verify(httpExchange).sendResponseHeaders(eq(ConstantsCommon.HTTP_STATUS_RESOURCE_NOT_FOUND), eq(0L));
    }
    @Test
    public void testHandleGetExistentURIDepthOne() throws Exception {
        ApiHandler componentToTest = new ApiHandler();
        HttpExchange httpExchange = Mockito.spy(HttpExchange.class);
        when(httpExchange.getRequestURI()).thenReturn(new URI("/api/users/admin"));
        when(httpExchange.getPrincipal()).thenReturn(new HttpPrincipal("admin","password"));
        when(httpExchange.getRequestMethod()).thenReturn(ConstantsCommon.GET);
        when(httpExchange.getResponseBody()).thenReturn(Mockito.mock(OutputStream.class));
        when(httpExchange.getResponseHeaders()).thenReturn(Mockito.mock(Headers.class));
        componentToTest.handle(httpExchange);
        verify(httpExchange).sendResponseHeaders(eq(ConstantsCommon.HTTP_STATUS_OK),any(Long.class));
    }
    @Test
    public void testHandleGetExistentURIDepthTwo() throws Exception {

        ApiHandler componentToTest = new ApiHandler();
        HttpExchange httpExchange = Mockito.spy(HttpExchange.class);
        when(httpExchange.getRequestURI()).thenReturn(new URI("/api/users"));
        when(httpExchange.getPrincipal()).thenReturn(new HttpPrincipal("admin","password"));
        when(httpExchange.getRequestMethod()).thenReturn(ConstantsCommon.GET);
        when(httpExchange.getResponseBody()).thenReturn(Mockito.mock(OutputStream.class));
        when(httpExchange.getResponseHeaders()).thenReturn(Mockito.mock(Headers.class));
        componentToTest.handle(httpExchange);
        verify(httpExchange).sendResponseHeaders(eq(ConstantsCommon.HTTP_STATUS_OK),any(Long.class));
    }
    @Test
    public void testHandleGetExistentURIDepthOneXML() throws Exception {
        ApiHandler componentToTest = new ApiHandler();
        HttpExchange httpExchange = Mockito.spy(HttpExchange.class);
        when(httpExchange.getRequestURI()).thenReturn(new URI("/api/users/admin"));
        when(httpExchange.getPrincipal()).thenReturn(new HttpPrincipal("admin","password"));
        when(httpExchange.getRequestMethod()).thenReturn(ConstantsCommon.GET);
        when(httpExchange.getResponseBody()).thenReturn(Mockito.mock(OutputStream.class));
        when(httpExchange.getResponseHeaders()).thenReturn(Mockito.mock(Headers.class));
        when(httpExchange.getRequestHeaders()).thenReturn(getHeadersXML());
        componentToTest.handle(httpExchange);
        verify(httpExchange).sendResponseHeaders(eq(ConstantsCommon.HTTP_STATUS_OK),any(Long.class));
    }


    @Test
    public void testHandleGetExistentURIDepthTwoXML() throws Exception {

        ApiHandler componentToTest = new ApiHandler();
        HttpExchange httpExchange = Mockito.spy(HttpExchange.class);
        when(httpExchange.getRequestURI()).thenReturn(new URI("/api/users"));
        when(httpExchange.getPrincipal()).thenReturn(new HttpPrincipal("admin","password"));
        when(httpExchange.getRequestMethod()).thenReturn(ConstantsCommon.GET);
        when(httpExchange.getResponseBody()).thenReturn(Mockito.mock(OutputStream.class));
        when(httpExchange.getResponseHeaders()).thenReturn(Mockito.mock(Headers.class));
        when(httpExchange.getRequestHeaders()).thenReturn(getHeadersXML());
        componentToTest.handle(httpExchange);
        verify(httpExchange).sendResponseHeaders(eq(ConstantsCommon.HTTP_STATUS_OK),any(Long.class));
    }

    // End testing GET//

    //Start testing PUT//

    @Test
    public void testHandlePutNonExistentURI() throws Exception {
        ApiHandler componentToTest = new ApiHandler();
        HttpExchange httpExchange = Mockito.spy(HttpExchange.class);
        when(httpExchange.getRequestURI()).thenReturn(new URI("NONEXISTENT"));
        when(httpExchange.getPrincipal()).thenReturn(new HttpPrincipal("admin","password"));
        when(httpExchange.getRequestMethod()).thenReturn(ConstantsCommon.PUT);
        when(httpExchange.getResponseBody()).thenReturn(Mockito.mock(OutputStream.class));
        componentToTest.handle(httpExchange);
        verify(httpExchange).sendResponseHeaders(eq(ConstantsCommon.HTTP_STATUS_RESOURCE_NOT_FOUND), eq(0L));
    }


    @Test
    public void testHandlePutNoRights() throws Exception {
        createUsers();
        ApiHandler componentToTest = new ApiHandler();
        HttpExchange httpExchange = Mockito.spy(HttpExchange.class);
        when(httpExchange.getRequestURI()).thenReturn(new URI("/api/users/admin"));
        when(httpExchange.getPrincipal()).thenReturn(new HttpPrincipal("dos","password"));
        when(httpExchange.getRequestMethod()).thenReturn(ConstantsCommon.PUT);
        when(httpExchange.getResponseBody()).thenReturn(Mockito.mock(OutputStream.class));
        componentToTest.handle(httpExchange);
        verify(httpExchange).sendResponseHeaders(eq(ConstantsCommon.HTTP_STATUS_METHOD_NOT_ALLOWED), eq(0L));
    }
    @Test
    public void testHandlePutWithRights() throws Exception {
        createUsers();
        ApiHandler componentToTest = new ApiHandler();
        HttpExchange httpExchange = Mockito.spy(HttpExchange.class);
        when(httpExchange.getRequestURI()).thenReturn(new URI("/api/users/admin"));
        when(httpExchange.getPrincipal()).thenReturn(new HttpPrincipal("admin","password"));
        when(httpExchange.getRequestMethod()).thenReturn(ConstantsCommon.PUT);
        when(httpExchange.getResponseBody()).thenReturn(Mockito.mock(OutputStream.class));
        when(httpExchange.getRequestBody()).thenReturn(getInputStreamForNewUser());
        when(httpExchange.getResponseHeaders()).thenReturn(Mockito.mock(Headers.class));
        int precount = usersTest.size();
        componentToTest.handle(httpExchange);
        verify(httpExchange).sendResponseHeaders(eq(ConstantsCommon.HTTP_STATUS_CREATED), any(Long.class));
        assertEquals(precount+1,usersTest.size());
    }
    @Test
    public void testHandlePutWithRightsXML() throws Exception {
        createUsers();
        ApiHandler componentToTest = new ApiHandler();
        HttpExchange httpExchange = Mockito.spy(HttpExchange.class);
        when(httpExchange.getRequestURI()).thenReturn(new URI("/api/users/admin"));
        when(httpExchange.getPrincipal()).thenReturn(new HttpPrincipal("admin","password"));
        when(httpExchange.getRequestMethod()).thenReturn(ConstantsCommon.PUT);
        when(httpExchange.getResponseBody()).thenReturn(Mockito.mock(OutputStream.class));
        when(httpExchange.getRequestHeaders()).thenReturn(getHeadersXML());
        when(httpExchange.getRequestBody()).thenReturn(getInputStreamForNewUserXML());
        when(httpExchange.getResponseHeaders()).thenReturn(Mockito.mock(Headers.class));
        int precount = usersTest.size();
        componentToTest.handle(httpExchange);
        verify(httpExchange).sendResponseHeaders(eq(ConstantsCommon.HTTP_STATUS_CREATED), any(Long.class));
        assertEquals(precount+1,usersTest.size());
    }



    //End Testing PUT



    //Starting POST//
    @Test
    public void testHandlePostNonExistentURI() throws Exception {
        ApiHandler componentToTest = new ApiHandler();
        HttpExchange httpExchange = Mockito.spy(HttpExchange.class);
        when(httpExchange.getRequestURI()).thenReturn(new URI("NONEXISTENT"));
        when(httpExchange.getPrincipal()).thenReturn(new HttpPrincipal("admin","password"));
        when(httpExchange.getRequestMethod()).thenReturn(ConstantsCommon.POST);
        when(httpExchange.getResponseBody()).thenReturn(Mockito.mock(OutputStream.class));
        componentToTest.handle(httpExchange);
        verify(httpExchange).sendResponseHeaders(eq(ConstantsCommon.HTTP_STATUS_RESOURCE_NOT_FOUND), eq(0L));
    }

    @Test
    public void testHandlePostOneDepth() throws Exception {
        ApiHandler componentToTest = new ApiHandler();
        HttpExchange httpExchange = Mockito.spy(HttpExchange.class);
        when(httpExchange.getRequestURI()).thenReturn(new URI("/api/users"));
        when(httpExchange.getPrincipal()).thenReturn(new HttpPrincipal("admin","password"));
        when(httpExchange.getRequestMethod()).thenReturn(ConstantsCommon.POST);
        when(httpExchange.getResponseBody()).thenReturn(Mockito.mock(OutputStream.class));
        when(httpExchange.getResponseHeaders()).thenReturn(Mockito.mock(Headers.class));
        when(httpExchange.getRequestBody()).thenReturn(getInputStreamForNewUser());
        componentToTest.handle(httpExchange);
        verify(httpExchange).sendResponseHeaders(eq(ConstantsCommon.HTTP_STATUS_RESOURCE_NOT_FOUND), any(Long.class));
    }

    @Test
    public void testHandlePostWithRights() throws Exception {
        createUsers();
        ApiHandler componentToTest = new ApiHandler();
        HttpExchange httpExchange = Mockito.spy(HttpExchange.class);
        when(httpExchange.getRequestURI()).thenReturn(new URI("/api/users/new"));
        when(httpExchange.getPrincipal()).thenReturn(new HttpPrincipal("admin","password"));
        when(httpExchange.getRequestMethod()).thenReturn(ConstantsCommon.POST);
        when(httpExchange.getResponseBody()).thenReturn(Mockito.mock(OutputStream.class));
        when(httpExchange.getResponseHeaders()).thenReturn(Mockito.mock(Headers.class));
        when(httpExchange.getRequestBody()).thenReturn(getInputStreamForNewUser());
        int precount = usersTest.size();
        componentToTest.handle(httpExchange);
        assertEquals(precount+1,usersTest.size());
        verify(httpExchange).sendResponseHeaders(eq(ConstantsCommon.HTTP_STATUS_OK), any(Long.class));
    }

    @Test
    public void testHandlePostWithRightsExistent() throws Exception {
        createUsers();
        ApiHandler componentToTest = new ApiHandler();
        HttpExchange httpExchange = Mockito.spy(HttpExchange.class);
        when(httpExchange.getRequestURI()).thenReturn(new URI("/api/users/dos"));
        when(httpExchange.getPrincipal()).thenReturn(new HttpPrincipal("admin","password"));
        when(httpExchange.getRequestMethod()).thenReturn(ConstantsCommon.POST);
        when(httpExchange.getResponseBody()).thenReturn(Mockito.mock(OutputStream.class));
        when(httpExchange.getResponseHeaders()).thenReturn(Mockito.mock(Headers.class));
        when(httpExchange.getRequestBody()).thenReturn(getInputStreamForExistent());
        int precount = usersTest.size();
        componentToTest.handle(httpExchange);
        assertEquals(precount,usersTest.size());
        verify(httpExchange).sendResponseHeaders(eq(ConstantsCommon.HTTP_STATUS_OK), any(Long.class));
    }

    @Test
    public void testHandlePostWithRightsXML() throws Exception {
        createUsers();
        ApiHandler componentToTest = new ApiHandler();
        HttpExchange httpExchange = Mockito.spy(HttpExchange.class);
        when(httpExchange.getRequestURI()).thenReturn(new URI("/api/users/new"));
        when(httpExchange.getPrincipal()).thenReturn(new HttpPrincipal("admin","password"));
        when(httpExchange.getRequestMethod()).thenReturn(ConstantsCommon.POST);
        when(httpExchange.getResponseBody()).thenReturn(Mockito.mock(OutputStream.class));
        when(httpExchange.getResponseHeaders()).thenReturn(Mockito.mock(Headers.class));
        when(httpExchange.getRequestHeaders()).thenReturn(getHeadersXML());
        when(httpExchange.getRequestBody()).thenReturn(getInputStreamForNewUserXML());
        int precount = usersTest.size();
        componentToTest.handle(httpExchange);
        assertEquals(precount+1,usersTest.size());
        verify(httpExchange).sendResponseHeaders(eq(ConstantsCommon.HTTP_STATUS_OK), any(Long.class));
    }


    @Test
    public void testHandlePostWithRightsExistentXML() throws Exception {
        createUsers();
        ApiHandler componentToTest = new ApiHandler();
        HttpExchange httpExchange = Mockito.spy(HttpExchange.class);
        when(httpExchange.getRequestURI()).thenReturn(new URI("/api/users/dos"));
        when(httpExchange.getPrincipal()).thenReturn(new HttpPrincipal("admin","password"));
        when(httpExchange.getRequestMethod()).thenReturn(ConstantsCommon.POST);
        when(httpExchange.getResponseBody()).thenReturn(Mockito.mock(OutputStream.class));
        when(httpExchange.getResponseHeaders()).thenReturn(Mockito.mock(Headers.class));
        when(httpExchange.getRequestHeaders()).thenReturn(getHeadersXML());
        when(httpExchange.getRequestBody()).thenReturn(getInputStreamForExistentrXML());
        int precount = usersTest.size();
        componentToTest.handle(httpExchange);
        assertEquals(precount,usersTest.size());
        verify(httpExchange).sendResponseHeaders(eq(ConstantsCommon.HTTP_STATUS_OK), any(Long.class));
    }
    //End Testing Post



    //Start Testing DELETE
    @Test
    public void testHandleDeleteNonExistentURI() throws Exception {

        ApiHandler componentToTest = new ApiHandler();
        HttpExchange httpExchange = Mockito.spy(HttpExchange.class);
        when(httpExchange.getRequestURI()).thenReturn(new URI("NONEXISTENT"));
        when(httpExchange.getPrincipal()).thenReturn(new HttpPrincipal("admin","password"));
        when(httpExchange.getRequestMethod()).thenReturn(ConstantsCommon.DELETE);
        when(httpExchange.getResponseBody()).thenReturn(Mockito.mock(OutputStream.class));
        when(httpExchange.getRequestBody()).thenReturn(getInputStreamForExistent());
        componentToTest.handle(httpExchange);
        verify(httpExchange).sendResponseHeaders(eq(ConstantsCommon.HTTP_STATUS_FORBIDDEN), eq(0L));


    }

    @Test
    public void testHandleDeleteNoRights() throws Exception {
        createUsers();
        ApiHandler componentToTest = new ApiHandler();
        HttpExchange httpExchange = Mockito.spy(HttpExchange.class);
        when(httpExchange.getRequestURI()).thenReturn(new URI("/api/users/dos"));
        when(httpExchange.getPrincipal()).thenReturn(new HttpPrincipal("tres","password"));
        when(httpExchange.getRequestMethod()).thenReturn(ConstantsCommon.DELETE);
        when(httpExchange.getResponseBody()).thenReturn(Mockito.mock(OutputStream.class));
        when(httpExchange.getResponseHeaders()).thenReturn(Mockito.mock(Headers.class));
        int precount = usersTest.size();
        componentToTest.handle(httpExchange);
        verify(httpExchange).sendResponseHeaders(eq(ConstantsCommon.HTTP_STATUS_METHOD_NOT_ALLOWED), eq(0L));
        assertEquals(precount,usersTest.size());
    }

    @Test
    public void testHandleDeleteExistentUser() throws Exception {
        createUsers();
        ApiHandler componentToTest = new ApiHandler();
        HttpExchange httpExchange = Mockito.spy(HttpExchange.class);
        when(httpExchange.getRequestURI()).thenReturn(new URI("/api/users/dos"));
        when(httpExchange.getPrincipal()).thenReturn(new HttpPrincipal("admin","password"));
        when(httpExchange.getRequestMethod()).thenReturn(ConstantsCommon.DELETE);
        when(httpExchange.getResponseBody()).thenReturn(Mockito.mock(OutputStream.class));
        when(httpExchange.getResponseHeaders()).thenReturn(Mockito.mock(Headers.class));
        int precount = usersTest.size();
        componentToTest.handle(httpExchange);
        verify(httpExchange).sendResponseHeaders(eq(ConstantsCommon.HTTP_STATUS_OK), eq(0L));
        assertEquals(precount-1,usersTest.size());
    }
    @Test
    public void testHandleDeleteNonExistentUser() throws Exception {
        createUsers();
        ApiHandler componentToTest = new ApiHandler();
        HttpExchange httpExchange = Mockito.spy(HttpExchange.class);
        when(httpExchange.getRequestURI()).thenReturn(new URI("/api/users/siete"));
        when(httpExchange.getPrincipal()).thenReturn(new HttpPrincipal("admin","password"));
        when(httpExchange.getRequestMethod()).thenReturn(ConstantsCommon.DELETE);
        when(httpExchange.getResponseBody()).thenReturn(Mockito.mock(OutputStream.class));
        when(httpExchange.getResponseHeaders()).thenReturn(Mockito.mock(Headers.class));
        int precount = usersTest.size();
        componentToTest.handle(httpExchange);
        verify(httpExchange).sendResponseHeaders(eq(ConstantsCommon.HTTP_STATUS_UNPROCESSABLE_ENTITY), eq(0L));
        assertEquals(precount,usersTest.size());
    }
    //END Testing DELETE

    @Test
    public void testHandleOptionsAdmin() throws Exception {
        ApiHandler componentToTest = new ApiHandler();
        HttpExchange httpExchange = Mockito.spy(HttpExchange.class);
        when(httpExchange.getRequestURI()).thenReturn(new URI("URI"));
        when(httpExchange.getPrincipal()).thenReturn(new HttpPrincipal("admin","password"));
        when(httpExchange.getRequestMethod()).thenReturn(ConstantsCommon.OPTIONS);
        when(httpExchange.getResponseBody()).thenReturn(Mockito.mock(OutputStream.class));
        when(httpExchange.getResponseHeaders()).thenReturn(Mockito.mock(Headers.class));
        componentToTest.handle(httpExchange);
        verify(httpExchange).sendResponseHeaders(eq(ConstantsCommon.HTTP_STATUS_OK), eq(0L));
    }

    @Test
    public void testHandleOptionsNoAdmin() throws Exception {
        ApiHandler componentToTest = new ApiHandler();
        HttpExchange httpExchange = Mockito.spy(HttpExchange.class);
        when(httpExchange.getRequestURI()).thenReturn(new URI("URI"));
        when(httpExchange.getPrincipal()).thenReturn(new HttpPrincipal("dos","password"));
        when(httpExchange.getRequestMethod()).thenReturn(ConstantsCommon.OPTIONS);
        when(httpExchange.getResponseBody()).thenReturn(Mockito.mock(OutputStream.class));
        when(httpExchange.getResponseHeaders()).thenReturn(Mockito.mock(Headers.class));
        componentToTest.handle(httpExchange);
        verify(httpExchange).sendResponseHeaders(eq(ConstantsCommon.HTTP_STATUS_OK), eq(0L));
      }


    @Test
    public void testHandleAnotherMethodNonExistentURI() throws Exception {
        ApiHandler componentToTest = new ApiHandler();
        HttpExchange httpExchange = Mockito.spy(HttpExchange.class);
        when(httpExchange.getRequestURI()).thenReturn(new URI("NONEXISTENT"));
        when(httpExchange.getPrincipal()).thenReturn(new HttpPrincipal("admin","password"));
        when(httpExchange.getRequestMethod()).thenReturn("another");
        when(httpExchange.getResponseBody()).thenReturn(Mockito.mock(OutputStream.class));
        componentToTest.handle(httpExchange);
        verify(httpExchange).sendResponseHeaders(eq(ConstantsCommon.HTTP_STATUS_METHOD_NOT_ALLOWED), eq(0L));
    }
    private Headers getHeadersXML() {
        Headers headers = new Headers();
        headers.add(ConstantsCommon.CONTENT_TYPE, ConstantsCommon.APPLICATION_XML);
        return headers;
    }

    private InputStream getInputStreamForNewUser() throws JsonProcessingException {
        User user= new User("new", "new", new ArrayList<String>());
        return new ByteArrayInputStream(ConversionUtils.beanJsonToBytes(user));
    }
    private InputStream getInputStreamForExistent() throws JsonProcessingException {
        User user= new User("dos", "new", new ArrayList<String>());
        return new ByteArrayInputStream(ConversionUtils.beanJsonToBytes(user));
    }

    private InputStream getInputStreamForNewUserXML() throws JsonProcessingException {
        User user= new User("new", "new", new ArrayList<String>());
        return new ByteArrayInputStream(ConversionUtils.beanXMLToBytes(user));
    }
    private InputStream getInputStreamForExistentrXML() throws JsonProcessingException {
        User user= new User("dos", "new", new ArrayList<String>());
        return new ByteArrayInputStream(ConversionUtils.beanXMLToBytes(user));
    }


}