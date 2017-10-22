package controller.utils;

public class ConstantsCommon {

    private ConstantsCommon() {
        throw new IllegalStateException("Constant class");
    }

    //Realted to project
    public static final String RESOURCES_ROOT = "users";
    public static final String API_CONTEXT = "/api/";

    //HTTP Content related
    public static final String CONTENT_TYPE = "Content-Type";
    public static final String APPLICATION_JSON = "application/json";
    public static final String APPLICATION_XML = "application/xml";
    public static final String APPLICATION_HTML = "text/html";


    public static final String LOCATION = "Location";
    public static final String ALLOW = "Allow";

    //HTTP Method
    public static final String GET = "GET";
    public static final String POST = "POST";
    public static final String DELETE = "DELETE";
    public static final String PUT = "PUT";
    public static final String OPTIONS = "OPTIONS";

    //HTTP Status
    public static final int HTTP_STATUS_OK = 200;
    public static final int HTTP_STATUS_RESOURCE_NOT_FOUND = 404;
    public static final int HTTP_STATUS_UNAUTHORIZED = 401;
    public static final int HTTP_STATUS_FORBIDDEN = 403;
    public static final int HTTP_STATUS_METHOD_NOT_ALLOWED = 405;
    public static final int HTTP_STATUS_REDIRECT = 302;
    public static final int HTTP_STATUS_UNPROCESSABLE_ENTITY = 422;
    public static final int HTTP_STATUS_UNSUPPORTED_MEDIA_TYPE = 415;
    public static final int HTTP_STATUS_CREATED = 201;


    //HTTP Message
    public static final String MESSAGE_LOGIN_WAS_NOT_SUCCESSFUL = "LOGIN WAS NOT SUCCESSFUL";
    public static final String MESSAGE_RESOURCE_NOT_FOUND = "RESOURCE NOT FOUND";


    public static final String COOKIE = "Cookie";
    public static final String SET_COOKIE = "Set-Cookie";


    //ROLES AND PAGES
    public static final String ROLE_ADMIN = "admin";
    public static final String ROLE_PAGE_1 = "PAGE_1";
    public static final String ROLE_PAGE_2 = "PAGE_2";
    public static final String ROLE_PAGE_3 = "PAGE_3";

}
