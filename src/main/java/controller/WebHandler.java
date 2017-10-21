package controller;

import java.io.*;
import java.net.URI;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import model.session.SessionDao;
import model.session.SessionMemoryImpl;
import model.user.UsersDao;
import model.user.UsersMemoryImpl;

@SuppressWarnings("restriction")
public class WebHandler implements HttpHandler {


	public static final String PATHNAME_TEMPLATE = "src/main/view/pages/pageTemplate.html";
	public static final String FILEPATH_PAGE_LOGIN = "src/main/view/pages/login.html";
	public static final String SESSION_USER_ROLES = "sessionUserRoles";
	public static final String PATH_CLOSESSION = "/closession";
	public static final String SEED="SEMILLAUSERSESSION";
	public static final String LOGIN_WITH_PARAMS = "/login?";

	@SuppressWarnings("restriction")
	public void handle(HttpExchange httpExchange) throws IOException {
		OutputStream os = httpExchange.getResponseBody();
		httpExchange.getResponseHeaders().add(ConstantsCommon.CONTENT_TYPE, ConstantsCommon.APPLICATION_HTML);
		URI uri = httpExchange.getRequestURI();
		UsersDao users= UsersMemoryImpl.getInstance();
		SessionDao sessions  = SessionMemoryImpl.getInstance();


		if (httpExchange.getRequestMethod().equals(ConstantsCommon.GET) ){

			if (uri.toString().startsWith("/login")){

				File file = new File(FILEPATH_PAGE_LOGIN);
				byte[] bytearray = new byte[(int) file.length()];
				FileInputStream fis = new FileInputStream(file);
				String html =ConversionUtils.inputStreamToString(fis);
				html= String.format(html,uri.getRawQuery());
				httpExchange.sendResponseHeaders(ConstantsCommon.HTTP_STATUS_OK,html.getBytes().length);
				//Write the response string
				os.write(html.getBytes());
				os.close();

			} else if(uri.toString().startsWith("/PAGE")){
				os = doPageLogged(httpExchange, os, uri);

			} else if (uri.toString().startsWith(PATH_CLOSESSION)){
				String user= uri.getQuery();

				if (users.exists(user)){
					sessions.deleteSession(user);//si session existe cerrarla
					httpExchange.getResponseHeaders().set(ConstantsCommon.COOKIE, "");

				}
				doRedirect(httpExchange, "/login");

				//TODO Cerrar Sesion y a login

			} else {
				sendHeaderAndWrite(httpExchange, os, ConstantsCommon.HTTP_STATUS_RESOURCE_NOT_FOUND, ConstantsCommon.MESSAGE_RESOURCE_NOT_FOUND);

			}
		} else if(httpExchange.getRequestMethod().equals(ConstantsCommon.POST)){
			if (uri.toString().startsWith("/doLogin")){
				String [] params = (ConversionUtils.inputStreamToString(httpExchange.getRequestBody())).split("&");
				if (params.length == 2) {
					String user = params[0].split("=")[1];
					String pwd = params[1].split("=")[1];
					if (users.exists(user, pwd)) {
						sessions.createSession(user);
						try {
							httpExchange.getResponseHeaders().set("Set-Cookie",SESSION_USER_ROLES+"=" + CryptoSessionUtils.encrypt(SEED,user) + "; path=/");
						} catch (Exception e) {
							e.printStackTrace();
						}
						httpExchange.setAttribute(SESSION_USER_ROLES, user);
						if (uri.getRawQuery()!=null){
							doRedirect(httpExchange, uri.getRawQuery());
						}else{
							httpExchange.sendResponseHeaders(ConstantsCommon.HTTP_STATUS_OK, 0);
						}
					} else {
						sendHeaderAndWrite(httpExchange, os, ConstantsCommon.HTTP_STATUS_UNAUTHORIZED, ConstantsCommon.MESSAGE_LOGIN_WAS_NOT_SUCCESSFUL);
					}
				}
				else{
					httpExchange.sendResponseHeaders(ConstantsCommon.HTTP_STATUS_FORBIDDEN, 0);
				}
			} else{
				sendHeaderAndWrite(httpExchange, os, ConstantsCommon.HTTP_STATUS_RESOURCE_NOT_FOUND, ConstantsCommon.MESSAGE_RESOURCE_NOT_FOUND);
			}

		} else{
			httpExchange.sendResponseHeaders(ConstantsCommon.HTTP_STATUS_METHOD_NOT_ALLOWED, 0);
		}
		os.close();
	}

	private void sendHeaderAndWrite(HttpExchange httpExchange, OutputStream os, int httpStatus, String message) throws IOException {
		httpExchange.sendResponseHeaders(httpStatus, message.getBytes().length);
		os.write(message.getBytes());
	}

	private void doRedirect(HttpExchange httpExchange, String target) throws IOException {
		httpExchange.getResponseHeaders().set(ConstantsCommon.LOCATION, target);
		httpExchange.sendResponseHeaders(ConstantsCommon.HTTP_STATUS_REDIRECT, 0);
	}

	private OutputStream doPageLogged(HttpExchange httpExchange, OutputStream os, URI uri) throws IOException {
		UsersDao users= UsersMemoryImpl.getInstance();
		SessionDao sessions  = SessionMemoryImpl.getInstance();
		String usersession =httpExchange.getRequestHeaders().getFirst(ConstantsCommon.COOKIE);
		String uriWithoutSlash= uri.toString().substring(1);
		if (usersession!= null &&  usersession.split(SESSION_USER_ROLES+"=").length ==2){
			try {
				String userTryingAccess = CryptoSessionUtils.decrypt(SEED, usersession.split(SESSION_USER_ROLES + "=")[1]);

				if (users.exists(userTryingAccess) && users.hasRights(userTryingAccess,uriWithoutSlash) && sessions.hasSession(userTryingAccess)){
					sessions.refreshSession(userTryingAccess);
					os = getOutputStreamPageLogged(httpExchange, userTryingAccess, uri.toString(), PATH_CLOSESSION+"?"+userTryingAccess);
				}
				else{
					doRedirect(httpExchange, LOGIN_WITH_PARAMS +uriWithoutSlash);
				}
			}
			catch(Exception e){
				e.printStackTrace();
			}
        }
        else{
			doRedirect(httpExchange, LOGIN_WITH_PARAMS +uriWithoutSlash);
        }
		return os;
	}

	private OutputStream getOutputStreamPageLogged(HttpExchange httpExchange, String username, String page, String url) throws IOException {
		OutputStream os;File file = new File(PATHNAME_TEMPLATE);
		os = httpExchange.getResponseBody();
		FileInputStream fis = new FileInputStream(file);
		String html = ConversionUtils.inputStreamToString(fis);
		html= String.format(html,page,username,url);
		//Write the response string
		byte[] bytearray =html.getBytes();
		httpExchange.sendResponseHeaders(ConstantsCommon.HTTP_STATUS_OK,html.getBytes().length);
		os.write(bytearray);
		os.close();
		return os;
	}

}