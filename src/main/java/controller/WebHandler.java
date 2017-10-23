package controller;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import controller.utils.ConstantsCommon;
import controller.utils.ConversionUtils;
import controller.utils.CryptoSessionUtils;
import model.session.SessionDao;
import model.session.SessionMemoryImpl;
import model.user.UsersDao;
import model.user.UsersMemoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.invoke.MethodHandles;
import java.net.URI;

@SuppressWarnings("restriction")

public class WebHandler implements HttpHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());


	public static final String PATHNAME_TEMPLATE = "src/main/view/pages/pageTemplate.html";
	public static final String FILEPATH_PAGE_LOGIN = "src/main/view/pages/login.html";
	public static final String SESSION_USER_ROLES = "sessionUserRoles";
	public static final String PATH_CLOSESSION = "/closession";
	public static final String SEED="SEMILLAUSERSESSION";
	public static final String LOGIN_WITH_PARAMS = "/login?";
	public static final String LOGIN = "/login";
	public static final String PAGE = "/PAGE";
	public static final String DO_LOGIN = "/doLogin";

	@SuppressWarnings("restriction")
	public void handle(HttpExchange httpExchange) throws IOException {

		LOGGER.info("Starting handle");
		OutputStream os = httpExchange.getResponseBody();
		httpExchange.getResponseHeaders().add(ConstantsCommon.CONTENT_TYPE, ConstantsCommon.APPLICATION_HTML);
		LOGGER.info("Uri request {}, Method {}", httpExchange.getRequestURI().toString(),httpExchange.getRequestMethod());
		URI uri = httpExchange.getRequestURI();
		UsersDao users= UsersMemoryImpl.getInstance();
		SessionDao sessions  = SessionMemoryImpl.getInstance();
		if (httpExchange.getRequestMethod().equals(ConstantsCommon.GET) ){
			if (uri.toString().startsWith(LOGIN)){
				LOGGER.info("Starting login");
				doPageLogin(httpExchange, os, uri);
			} else if(uri.toString().startsWith(PAGE)){
				LOGGER.info("redirecting to page allowed just for logged users");
				os = doPageLogged(httpExchange, os, uri);
			} else if (uri.toString().startsWith(PATH_CLOSESSION)){
				LOGGER.info("Requested close session of a user");
				doCloseSessionAndRedirect(httpExchange, uri, users, sessions);
			} else {
				LOGGER.info(ConstantsCommon.MESSAGE_RESOURCE_NOT_FOUND);
				sendHeaderAndWrite(httpExchange, os, ConstantsCommon.HTTP_STATUS_RESOURCE_NOT_FOUND, ConstantsCommon.MESSAGE_RESOURCE_NOT_FOUND);
			}
		} else if(httpExchange.getRequestMethod().equals(ConstantsCommon.POST)){
			if (uri.toString().startsWith(DO_LOGIN)){
				LOGGER.info("Requested login from form");
				doSessionAfterLogin(httpExchange, os, uri, users, sessions);
			} else{
				LOGGER.info(ConstantsCommon.MESSAGE_RESOURCE_NOT_FOUND);
				sendHeaderAndWrite(httpExchange, os, ConstantsCommon.HTTP_STATUS_RESOURCE_NOT_FOUND, ConstantsCommon.MESSAGE_RESOURCE_NOT_FOUND);
			}

		} else{
			LOGGER.info("Method not allowed");
			httpExchange.sendResponseHeaders(ConstantsCommon.HTTP_STATUS_METHOD_NOT_ALLOWED, 0);
		}
		os.close();
		LOGGER.info("End of handle");
	}

	private void doSessionAfterLogin(HttpExchange httpExchange, OutputStream os, URI uri, UsersDao users, SessionDao sessions) throws IOException {
		String [] params = (ConversionUtils.inputStreamToString(httpExchange.getRequestBody())).split("&");
		if (params.length == 2) {
            String user = params[0].split("=")[1];
            String pwd = params[1].split("=")[1];
            if (users.exists(user, pwd)) {

                sessions.createSession(user);
				LOGGER.info("Session created in memory for the user {}", user);
                try {
                	LOGGER.info("Requested to create a cookie session for user {}",user);
                    httpExchange.getResponseHeaders().set(ConstantsCommon.SET_COOKIE,SESSION_USER_ROLES+"=" + CryptoSessionUtils.encrypt(SEED,user) + "; path=/");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (uri.getRawQuery()!=null){
                	LOGGER.info("Requested a redirect from get param after log in user");
                    doRedirect(httpExchange, uri.getRawQuery());
                }else{
                    httpExchange.sendResponseHeaders(ConstantsCommon.HTTP_STATUS_OK, 0);
                }
            } else {
            	LOGGER.info("The user {}, doesn't exist in the Model", user);
                sendHeaderAndWrite(httpExchange, os, ConstantsCommon.HTTP_STATUS_UNAUTHORIZED, ConstantsCommon.MESSAGE_LOGIN_WAS_NOT_SUCCESSFUL);
            }
        }
        else{
            httpExchange.sendResponseHeaders(ConstantsCommon.HTTP_STATUS_FORBIDDEN, 0);
        }
	}

	private void doCloseSessionAndRedirect(HttpExchange httpExchange, URI uri, UsersDao users, SessionDao sessions) throws IOException {
		String user= uri.getQuery();
		if (users.exists(user)){
            sessions.deleteSession(user);//si session existe cerrarla
			LOGGER.info("Session deleted for the user {}", user);
            //httpExchange.getResponseHeaders().set(ConstantsCommon.COOKIE, "");

        }
		doRedirect(httpExchange, LOGIN);
	}

	private void doPageLogin(HttpExchange httpExchange, OutputStream os, URI uri) throws IOException {
		File file = new File(FILEPATH_PAGE_LOGIN);
		FileInputStream fis = new FileInputStream(file);
		String html = ConversionUtils.inputStreamToString(fis);
		String urirawquery=uri.getRawQuery();
		if (urirawquery == null) urirawquery="";
		html= String.format(html,urirawquery);
		httpExchange.sendResponseHeaders(ConstantsCommon.HTTP_STATUS_OK,html.getBytes().length);
		//Write the response string
		os.write(html.getBytes());
	}

	private void sendHeaderAndWrite(HttpExchange httpExchange, OutputStream os, int httpStatus, String message) throws IOException {
		LOGGER.info("Response status {}",httpStatus);
		httpExchange.sendResponseHeaders(httpStatus, message.getBytes().length);
		os.write(message.getBytes());
	}

	private void doRedirect(HttpExchange httpExchange, String target) throws IOException {
		LOGGER.info("Requested redirection to {}", target);
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
					LOGGER.error("Refreshing session for user {}", userTryingAccess);
					sessions.refreshSession(userTryingAccess);
					os = getOutputStreamPageLogged(httpExchange, userTryingAccess, uriWithoutSlash, PATH_CLOSESSION+"?"+userTryingAccess);
				}
				else{
					doRedirect(httpExchange, LOGIN_WITH_PARAMS +uriWithoutSlash);
				}
			}
			catch(Exception e){
				LOGGER.error("There was an error trying to decrypt session");
				e.printStackTrace();
			}
        }
        else{
			doRedirect(httpExchange, LOGIN_WITH_PARAMS +uriWithoutSlash);
        }
		return os;
	}

	private OutputStream getOutputStreamPageLogged(HttpExchange httpExchange, String username, String page, String url) throws IOException {
		OutputStream os;
		File file = new File(PATHNAME_TEMPLATE);
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