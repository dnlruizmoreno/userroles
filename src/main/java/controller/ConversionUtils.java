package controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import model.User;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

/**
 * Created by danielruizm on 10/20/17.
 */
public class ConversionUtils {

    static String inputStreamToString(InputStream inputStream) throws IOException {
        // TODO Auto-generated method stub

        BufferedReader bufferedReader = null;
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        while ((line = bufferedReader.readLine()) != null) {
            stringBuilder.append(line);
        }
        return stringBuilder.toString();
    }

    static byte[] beanJsonToBytes(Object object) throws JsonProcessingException {
        ObjectMapper mapperObj = new ObjectMapper();
        byte[] bytes;
        if (object != null) {
            bytes=  mapperObj.writeValueAsString(object).getBytes();
            }else{
            bytes= "{}".getBytes();
        }
        return bytes;
    }

    static byte[] beanXMLToBytes(Object object) throws JsonProcessingException {
        XmlMapper xmlMapper = new XmlMapper();
        byte[] bytes;
        if (object != null) {
            bytes = xmlMapper.writeValueAsString(object).getBytes();
        }else{
            bytes= "<element></element>".getBytes();
        }
        return bytes;
    }


    static <T>  byte[] stringJsonBeanToBytes(String body, Class <T> classname ) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return   mapper.writerWithDefaultPrettyPrinter().writeValueAsString(mapper.readValue(body, classname)).getBytes();
    }

    static <T>  byte[] stringXMLBeanToBytes(String body, Class <T> classname ) throws IOException {
        XmlMapper xmlMapper = new XmlMapper();
        return   xmlMapper.writerWithDefaultPrettyPrinter().writeValueAsString(xmlMapper.readValue(body, classname)).getBytes();
    }


}
