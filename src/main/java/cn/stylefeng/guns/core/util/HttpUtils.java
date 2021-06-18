package cn.stylefeng.guns.core.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

public class HttpUtils {
    private static final Logger logger = LoggerFactory.getLogger(HttpUtils.class);
    private static final CloseableHttpClient httpClient;
    private static final RequestConfig requestConfig;
    static final String CHARSET = "UTF-8";
    //连接池最大并发连接数
    private static final int MAX_TOTAL_CONNECTIONS = 2000;
    //单路由最大并发数
    private static final int MAX_ROUTE_CONNECTIONS = 2000;
    //超时时间
    private static final int CONNECT_TIMEOUT = 15000;
    private static final int SOCKET_TIMEOUT = 15000;
    private static final int CONNECTION_REQUEST_TIMEOUT = 15000;

    /*
      配置http连接池
     */
    static{
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        cm.setMaxTotal(MAX_TOTAL_CONNECTIONS);
        cm.setDefaultMaxPerRoute(MAX_ROUTE_CONNECTIONS);
        httpClient = HttpClients.custom().setConnectionManager(cm).build();
        requestConfig = RequestConfig.custom().setSocketTimeout(SOCKET_TIMEOUT).setConnectTimeout(CONNECT_TIMEOUT)
                .setConnectionRequestTimeout(CONNECTION_REQUEST_TIMEOUT).build();
    }


    /**
     * post请求(Map方式)
     * @param url 请求的url地址 ?之前的地址
     * @param params 请求的参数
     * @param charset 编码格式
     * @return 返回所请求接口的反馈信息
     */
    public static String doPost(String url, Map<String, String> params, String charset){
        logger.info(">>>>>>>>>>>>>doPost 请求url：{}，参数：{}，字符编码：{}",url,params,charset);
        if(StringUtils.isBlank(url)){
            return null;
        }
        HttpPost httpPost = null;

        try {
            // 使用NameValuePairca格式进行接口请求参数的传递和拼装
            List<NameValuePair> pairs = null;
            if (params != null && !params.isEmpty()) {
                pairs = new ArrayList<>(params.size());
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    String value = entry.getValue();
                    if (value != null) {
                        pairs.add(new BasicNameValuePair(entry.getKey(), value));
                    }
                }
            }
            httpPost = new HttpPost(url);
            httpPost.setConfig(requestConfig);
            if (pairs != null && pairs.size() > 0) {
                httpPost.setEntity(new UrlEncodedFormEntity(pairs, CHARSET));
            }
            long startTime = System.currentTimeMillis();
            CloseableHttpResponse response = httpClient.execute(httpPost);
            long endTime = System.currentTimeMillis();
            logger.info("{} spend time {} millis", url, (endTime - startTime));
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != 200) {
                httpPost.abort();
                throw new RuntimeException("HttpClient,error status code :" + statusCode);
            }
            HttpEntity entity = response.getEntity();
            String result = null;
            if (entity != null) {
                result = EntityUtils.toString(entity, "utf-8");
            }
            EntityUtils.consume(entity);
            response.close();
            logger.info("调用post请求接口，响应结果：{}", result);
            return result;
        }catch (IOException e){
            httpPost.releaseConnection();
            e.printStackTrace();
        }
        return null;
    }

    /**
     * post请求(NameValuePair方式)
     * @param url 请求的url地址 ?之前的地址
     * @param map 请求的参数
     * @param charset 编码格式
     * @return 返回所请求接口的反馈信息
     */
    public static String doPost(String url, List<NameValuePair> map, String charset , Map<String,String> headerMap) {
        logger.info(">>>>>>>>>>>>>doPost 请求url：{}，参数：{}，字符编码：{}，header：{}",url,map,charset,headerMap);
        CloseableHttpResponse response = null;
        if (org.apache.commons.lang3.StringUtils.isBlank(url)) {
            return null;
        }
        HttpPost httpPost = null;
        try {
            httpPost = createHttpPost(url , map);
            if( null != headerMap && !headerMap.isEmpty() ) {
                for (Map.Entry<String, String> entry : headerMap.entrySet()) {
                    httpPost.addHeader(entry.getKey(), entry.getValue());
                }
            }
            response = httpClient.execute(httpPost);
            String result = acquireResult(response,httpPost);
            logger.info("调用post请求接口，响应结果：{}", result);
            return result;
        } catch (Exception e) {
            logger.error("do post error " , e);
        } finally {
            IOUtil.close(response);
            if (httpPost != null) {
                httpPost.releaseConnection();
            }
        }
        return null;
    }

    /**
     * POST请求,参数在url上
     * @param url 请求url
     * @param urlParams url参数
     * @param charset 字符编码
     * @param headerMap 请求头
     * @return String 服务器返回数据
     */
    public static String doPost(String url, Map<String, String> urlParams, String charset, Map<String,String> headerMap, RequestConfig requestConfig){
        logger.info(">>>>>>>>>>>>>doPost 请求 url：{}，url参数：{}，字符编码：{}", url, urlParams, charset);
        if (org.apache.commons.lang3.StringUtils.isBlank(url)) {
            return null;
        }
        CloseableHttpResponse response = null;
        HttpPost httpPost = null;

        try {
            url = getString(url, urlParams, charset);
            httpPost = new HttpPost(url);
            httpPost.setConfig(requestConfig);
            if( null != headerMap && !headerMap.isEmpty() ) {
                for (Map.Entry<String, String> entry : headerMap.entrySet()) {
                    httpPost.addHeader(entry.getKey(), entry.getValue());
                }
            }
            response = httpClient.execute(httpPost);
            return acquireResult(response,httpPost);

        }catch (IOException e){
            if (null != httpPost) {
                httpPost.releaseConnection();
            }
            logger.error("<<<<<<<<<<<<<doPost 请求失败：{}", urlParams, e);
        }
        logger.info("调用post请求接口，响应结果：{}", response);
        return null;
    }

    private static String getString(String url, Map<String, String> urlParams, String charset) throws IOException {
        if (urlParams != null && !urlParams.isEmpty()) {
            List<NameValuePair> pairs = new ArrayList<>(urlParams.size());
            for (Map.Entry<String, String> entry : urlParams.entrySet()) {
                String value = entry.getValue();
                if (value != null) {
                    pairs.add(new BasicNameValuePair(entry.getKey(), value));
                }
            }
            url += "?" + EntityUtils.toString(new UrlEncodedFormEntity(pairs, charset));
        }
        return url;
    }

    /**
     * Get请求(Map方式)
     * @param url 请求的url地址 ?之前的地址
     * @param params 请求的参数
     * @return 返回所请求接口的反馈信息
     */
    public static String doGet(String url, Map<String, Object> params, Map<String, String> headerMap){
        logger.info(">>>>>>>>>>>>>doGet 请求 url：{}，参数：{}，字符编码：{}", url, params, HttpUtils.CHARSET);
        if (StringUtils.isBlank(url)) {
            return null;
        }
        HttpGet httpGet = null;

        try {
            if (params != null && !params.isEmpty()) {
                List<NameValuePair> pairs = new ArrayList<>(params.size());
                for (Map.Entry<String, Object> entry : params.entrySet()) {
                    String value = (String) entry.getValue();
                    if (value != null) {
                        pairs.add(new BasicNameValuePair(entry.getKey(), value));
                    }
                }
                url += "?" + EntityUtils.toString(new UrlEncodedFormEntity(pairs, HttpUtils.CHARSET));
            }
            httpGet = new HttpGet(url);
            httpGet.setConfig(requestConfig);
            if( null != headerMap && !headerMap.isEmpty() ) {
                for (Map.Entry<String, String> entry : headerMap.entrySet()) {
                    httpGet.addHeader(entry.getKey(), entry.getValue());
                }
            }
            CloseableHttpResponse response = httpClient.execute(httpGet);
            int statusCode = response.getStatusLine().getStatusCode();
//            if (statusCode != 200) {
//                httpGet.abort();
//                throw new RuntimeException("HttpClient,error status code :" + statusCode);
//            }
            HttpEntity entity = response.getEntity();
            String result = null;
            if (entity != null) {
                result = EntityUtils.toString(entity, "utf-8");
            }
            EntityUtils.consume(entity);
            response.close();
            logger.info(">>>>>>>>>>>>>doGet 返回：{}" ,result);
            return result;
        }catch (IOException e){
            if (null != httpGet) {
                httpGet.releaseConnection();
            }
            logger.error("<<<<<<<<<<<<<doGet 请求失败：{}", params, e);
        }
        return null;
    }



    /**
     * Get请求(NameValuePair方式)
     * @param url 请求的url地址 ?之前的地址
     * @param params 请求的参数
     * @param charset 编码格式
     * @return 返回所请求接口的反馈信息
     */
    public static String doGet(String url, List<NameValuePair> params, String charset) {
        logger.info(">>>>>>>>>>>>>doGet 请求url ：{}，参数：{}，字符编码：{}",url,params,charset);
        CloseableHttpResponse response = null;
        if (org.apache.commons.lang3.StringUtils.isBlank(url)) {
            return null;
        }
        HttpGet httpGet = null;
        try {
            if (params != null && !params.isEmpty()) {
                url += "?" + EntityUtils.toString(new UrlEncodedFormEntity(params, charset));
            }
            httpGet = createHttpGet(url);
            response = httpClient.execute(httpGet);
            return acquireResult(response , httpGet);
        } catch (Exception e) {
            logger.error("do get error " , e);
        } finally {
            IOUtil.close(response);
            if (httpGet != null) {
                httpGet.releaseConnection();
            }
        }
        logger.info("调用get请求接口，响应结果：{}", response);
        return null;
    }

    public static String doGet(String url, Map<String, Object> params) {
        return doGet(url, params, null);
    }
    public static String doPost(String url, Map<String, String> params) {
        return doPost(url, params, CHARSET);
    }


    /***
     * 获取Http Get/Post请求中返回的数据
     * @param response	服务器返回response
     * @param requestBase HttpGet/HttpPost 对象
     *
     * @return String 服务器返回数据
     * */
    private static String acquireResult(CloseableHttpResponse response , HttpRequestBase requestBase) throws IOException {
        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode != HttpStatus.SC_OK) {
            requestBase.abort();
            throw new RuntimeException("HttpClient,error status code :" + statusCode);
        }
        HttpEntity entity = response.getEntity();
        String result = null;
        if (entity != null) {
            result = EntityUtils.toString(entity, "utf-8");
        }
        EntityUtils.consume(entity);
        return result;
    }

    private static HttpPost createHttpPost(String url, List<NameValuePair> pairs) {
        HttpPost post = new HttpPost(url);
        post.setConfig(requestConfig);
        try {
            if (pairs != null && pairs.size() > 0) {
                post.setEntity(new UrlEncodedFormEntity(pairs, CHARSET));
            }
        }catch (Exception e) {
            logger.error("create http post error " , e);
        }
        return post;
    }

    private static HttpGet createHttpGet(String url) {
        HttpGet get = new HttpGet(url);
        get.setConfig(requestConfig);
        return get;
    }


    /**
     * 调用post请求接口
     * @param url 发送的服务器地址
     * @param json 请求的json报文
     * @return 响应结果JSON对象
     */
    public static JSONObject doPostOfJson(String url, JSONObject json) {
        logger.info("调用post请求接口，请求地址：{},请求参数：{}", url, json);
        CloseableHttpClient httpclient = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost(url);
        JSONObject response = null;
        try {
            StringEntity s = new StringEntity(json.toString(),"utf-8");
            s.setContentEncoding("UTF-8");
            //发送json数据需要设置contentType
            s.setContentType("application/json;charset=UTF-8");
            post.setEntity(s);
            HttpResponse res = httpclient.execute(post);
            if(res.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
                // 返回json格式：
                String result = EntityUtils.toString(res.getEntity(),"utf-8");
                response = JSONObject.parseObject(result);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        logger.info("调用post请求接口，响应结果：{}", response);
        return response;
    }

    public static String doPutOfJson(String url, JSONObject json) {
        logger.info("调用put请求接口，请求地址：{},请求参数：{}", url, json);
        CloseableHttpClient httpclient = HttpClientBuilder.create().build();
        HttpPut httpPut = new HttpPut(url);
        String resultString = "";
        int statusCode = 200;
        try {
            StringEntity s = new StringEntity(json.toString(),"utf-8");
            s.setContentEncoding("UTF-8");
            //发送json数据需要设置contentType
            s.setContentType("application/json;charset=UTF-8");
            httpPut.setEntity(s);
            HttpResponse res = httpclient.execute(httpPut);
            statusCode = res.getStatusLine().getStatusCode();
                // 返回json格式：
                resultString = EntityUtils.toString(res.getEntity(),"utf-8");

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        logger.info("调用put请求接口，HTTP状态码：{}，响应结果：{}", statusCode ,resultString);
        return resultString;
    }

    public static String doPostString(String url, JSONObject json) {
        logger.info("调用post请求接口，请求地址：{},请求参数：{}", url, json);
        CloseableHttpClient httpclient = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost(url);
        String response = null;
        int statusCode = 200;
        try {
            StringEntity s = new StringEntity(json.toString(),"utf-8");
            s.setContentEncoding("UTF-8");
            //发送json数据需要设置contentType
            s.setContentType("application/json;charset=UTF-8");
            post.setEntity(s);
            HttpResponse res = httpclient.execute(post);
            statusCode = res.getStatusLine().getStatusCode();
            // 返回json格式：
            response = EntityUtils.toString(res.getEntity(),"utf-8");
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        logger.info("调用put请求接口，HTTP状态码：{}，响应结果：{}", statusCode ,response);
        return response;
    }

    public static JSONObject doPostWithHeader(String url, String json, Map<String, String> headerMap) {
        logger.info("调用post请求接口，请求地址：{},请求参数：{}", url, json);
        CloseableHttpClient httpclient = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost(url);
        JSONObject response = null;
        try {
            StringEntity s = new StringEntity(json,"UTF-8");
            s.setContentEncoding("UTF-8");
            s.setContentType("application/json;charset=utf-8");
            post.setEntity(s);
            if( null != headerMap && !headerMap.isEmpty() ) {
                for (Map.Entry<String, String> entry : headerMap.entrySet()) {
                    post.addHeader(entry.getKey(), entry.getValue());
                }
            }
            HttpResponse res = httpclient.execute(post);
            if(res.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
                String result = EntityUtils.toString(res.getEntity());
                response = JSONObject.parseObject(result);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        logger.info("调用post请求接口，响应结果：{}", response);
        return response;
    }

    public static String doDelete(String url, Map<String, Object> params){
        logger.info(">>>>>>>>>>>>>doDelete 请求 url：{}，参数：{}，字符编码：{}", url, params, HttpUtils.CHARSET);
        if (StringUtils.isBlank(url)) {
            return null;
        }
        HttpDelete httpDelete = null;
        try {
            if (params != null && !params.isEmpty()) {
                List<NameValuePair> pairs = new ArrayList<>(params.size());
                for (Map.Entry<String, Object> entry : params.entrySet()) {
                    String value = (String) entry.getValue();
                    if (value != null) {
                        pairs.add(new BasicNameValuePair(entry.getKey(), value));
                    }
                }
                url += "?" + EntityUtils.toString(new UrlEncodedFormEntity(pairs, HttpUtils.CHARSET));
            }
            httpDelete = new HttpDelete(url);
            httpDelete.setConfig(requestConfig);
            CloseableHttpResponse response = httpClient.execute(httpDelete);
            int statusCode = response.getStatusLine().getStatusCode();
            HttpEntity entity = response.getEntity();
            String result = null;
            if (entity != null) {
                result = EntityUtils.toString(entity, "utf-8");
            }
            EntityUtils.consume(entity);
            response.close();
            logger.info(">>>>>>>>>>>>>doDelete HTTP状态码：{}， 返回：{}" ,statusCode ,result);
            return result;
        } catch (IOException e){
            if (null != httpDelete) {
                httpDelete.releaseConnection();
            }
            logger.error("<<<<<<<<<<<<<doDelete 请求失败：{}", params, e);
        }
        return null;
    }

    /**
     * 创建一个用于basic auth验证的请求头
     * @param headerMap 请求头
     * @param username 用户名
     * @param password 密码
     * @return map
     */
    public static Map<String,String> createBasicAuthHeader(Map<String,String> headerMap,
                                                           String username,String password){
        if(MapUtils.isEmpty(headerMap)){
            headerMap = new HashMap<>();
        }
        headerMap.put("Authorization", "Basic " + Base64
                .getUrlEncoder().encodeToString((username + ":" + password).getBytes()));
        return headerMap;
    }
}
