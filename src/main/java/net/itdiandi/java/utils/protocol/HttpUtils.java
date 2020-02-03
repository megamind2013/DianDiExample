package net.itdiandi.java.utils.protocol;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Created by shendan on 2018/4/23.
 */
public class HttpUtils {static Logger logger = LoggerFactory.getLogger(HttpUtils.class);

    private static int defaultTimeout = 5;

    private static RequestConfig requestConfig = RequestConfig.custom()
            .setSocketTimeout(defaultTimeout*1000)
            .setConnectTimeout(defaultTimeout*1000)
            .setConnectionRequestTimeout(defaultTimeout*1000)
            .build();

    public static void setTimeout(int timeout){
        timeout = timeout < 3 ? 3 :timeout;
        requestConfig = RequestConfig.custom()
                .setSocketTimeout(timeout*1000)
                .setConnectTimeout(timeout*1000)
                .setConnectionRequestTimeout(timeout*1000)
                .build();
    }

    private static void setDefaultTimeout(){
        requestConfig = RequestConfig.custom()
                .setSocketTimeout(defaultTimeout*1000)
                .setConnectTimeout(defaultTimeout*1000)
                .setConnectionRequestTimeout(defaultTimeout*1000)
                .build();
    }

    public static String simpleGet(String uri){
        logger.debug("Send HTTP/GET to "+ uri);
        if(StringUtils.isBlank(uri)){
            return "Invalid http uri provided.";
        }
        String responseStr = null;
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
            HttpGet httpget = new HttpGet(uri);
            httpget.setConfig(requestConfig);
            CloseableHttpResponse response = httpclient.execute(httpget);
            try {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    responseStr = EntityUtils.toString(entity, Consts.UTF_8);
                }
            } finally {
                HttpClientUtils.closeQuietly(response);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            HttpClientUtils.closeQuietly(httpclient);
            setDefaultTimeout();
        }
        return responseStr;
    }

    public static String jsonPost(String uri, String jsonStr){
        logger.debug("Send HTTP/POST to "+ uri + ", with body: "+ jsonStr);
        return post(uri, jsonStr, ContentType.APPLICATION_JSON);
    }

    public static String formUrlEncodedPost(String uri, String jsonStr){
        logger.debug("Send HTTP/POST to "+ uri + ", with body: "+ jsonStr);
        return post(uri, jsonStr, ContentType.APPLICATION_FORM_URLENCODED);
    }


    public static String xmlPost(String uri, String xmlStr){
        logger.debug("Send HTTP/POST to "+ uri + ", with body: "+ xmlStr);
        ContentType type = ContentType.create("application/xml", Consts.UTF_8);
        return post(uri, xmlStr, type);
    }

    private static String post(String uri, String rawStr, ContentType type){
        if(StringUtils.isBlank(uri)){
            return "Invalid http uri provided.";
        }
        String responseStr = null;
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
            HttpPost httppost = new HttpPost(uri);
            httppost.setConfig(requestConfig);
            HttpEntity reqEntity = new StringEntity(rawStr, type);
            httppost.setEntity(reqEntity);
            CloseableHttpResponse response = httpclient.execute(httppost);
            try {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    responseStr = EntityUtils.toString(entity, Consts.UTF_8);
                }
            } finally {
                HttpClientUtils.closeQuietly(response);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            HttpClientUtils.closeQuietly(httpclient);
            setDefaultTimeout();
        }
        return responseStr;
    }


    /**
     * 获取用户访问Ip
     *
     * @param request
     * @return
     */
    public static String getIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return filterIp(ip);
    }

    /**
     * 如果通过了多级反向代理的话，X-Forwarded-For的值并不止一个，而是一串IP值
     * @param ip
     * @return
     */
    private static String filterIp(String ip) {
        if (ip != null) {
            String[] data = ip.split(",");
            for (int i=0; i<data.length; i++) {
                if (!"unknown".equalsIgnoreCase(data[i].replaceAll("\\s*", ""))) {
                    ip = data[i].replaceAll("\\s*", "");   //去除首尾空格
                    break;
                }
            }
        }else{
            ip  =  "127.0.0.1";
        }
        return ip;
    }

}
