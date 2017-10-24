package view;

import controller.utils.ConstantsCommon;
import controller.utils.ConversionUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by danielruizm on 10/24/17.
 */
public class Pages {
    public static byte[] getOutputStreamPageLogin(String query) throws IOException {
        File file = new File(ConstantsCommon.FILEPATH_PAGE_LOGIN);
        FileInputStream fis = new FileInputStream(file);
        String html = ConversionUtils.inputStreamToString(fis);
        String urirawquery=query;
        if (urirawquery == null) urirawquery="";
        html= String.format(html,urirawquery);
        return html.getBytes();
    }
    public static byte[] getOutputStreamPageLogged(String username, String page, String url) throws IOException {
        File file = new File(ConstantsCommon.PATHNAME_TEMPLATE);
        FileInputStream fis = new FileInputStream(file);
        String html = ConversionUtils.inputStreamToString(fis);
        html= String.format(html,page,username,url);
        return html.getBytes();
    }

}
