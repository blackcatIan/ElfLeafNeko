package nekoqq.utils.httpclient;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import nekoqq.utils.httpclient.beans.HttpResponseData;

import org.apache.commons.lang.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;



public class UtilsHttpClient {
    private static Logger logger = Logger.getLogger(UtilsHttpClient.class);
    

    /**
     * 模拟登录GET请求
     * @param httpclient  httpclient实例
     * @param url 请求地址
     * @param canRedirect 是否允许自动跳转（true可以，false不可以）
     * @return
     */
    /**
     * @param httpclient
     * @param url
     * @param canRedirect
     * @return
     */
    public static HttpResponseData analogGetMethod(CloseableHttpClient httpclient, String url,Boolean canRedirect) {
        
        //返回的响应数据
        HttpResponseData responseData = new HttpResponseData();
        
        HttpGet httpGet = new HttpGet(url);
        httpGet.setHeader("Connection", "keep-alive");
        
        httpGet.setHeader("Accept", "*/*");
        httpGet.setHeader("Accept-Encoding", "gzip,deflate,sdch");
        httpGet.setHeader("Accept-Language", "zh-CN,zh;q=0.8,en;q=0.6,zh-TW;q=0.4,ja;q=0.2");
        httpGet.setHeader("Connection", "keep-alive");
        httpGet.setHeader("Content-Type", "utf-8");
        httpGet.setHeader("Referer", "http://d.web2.qq.com/proxy.html?v=20130916001&callback=1&id=2");
        httpGet.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/34.0.1847.116 Safari/537.36");
        
        
        
        
        //设置超时
        RequestConfig rc = RequestConfig.custom().setSocketTimeout(6500).setConnectTimeout(6500).build();//设置请求和传输超时时间
        httpGet.setConfig(rc);

        
        
        //请求上下文，用以获得cookie
        HttpClientContext context = HttpClientContext.create();
        
        //设置是否允许自动跳转
        if (!canRedirect) {
            //禁止跳转(默认是可以自动跳转的)
            RequestConfig requestConfig = RequestConfig.custom().setRedirectsEnabled(false).build();;
            httpGet.setConfig(requestConfig);
        }
        
        
        CloseableHttpResponse response;
        String content = StringUtils.EMPTY;
        try
        {
            logger.info("执行GET请求: " + httpGet.getRequestLine());
            //执行请求
            response = httpclient.execute(httpGet, context);
            responseData.setResponse(response);
            
            byte contentByte[] = EntityUtils.toByteArray(response.getEntity());
            responseData.setContentByte(contentByte);
            //获取返回的内容
            content = new String(contentByte,"utf-8");
            responseData.setContent(content);
            //存放最新的cookies
            responseData.setCookies(context.getCookieStore().getCookies());
            responseData.setHttpClientContext(context);

            logger.info("返回状态：" + response.getStatusLine().toString());
            if (content.length() < 800) {
                logger.info("返回信息：" + content);
            } else {
                logger.info("返回信息：" + "大量信息超过800字");
            }
            logger.info("----------------------------------------");
            
        } catch (Exception e)
        {
            responseData.setContent("发生异常没有获得数据");
            responseData.setHttpClientContext(context);
            logger.error("get method",e);
        }
        
        return responseData;
    }
    
    
    
    /**
     * @param httpclient
     * @param url
     * @param params
     * @return
     */
    public static HttpResponseData analogPostMethod(CloseableHttpClient httpclient, String url,Map<String,String> params,Integer timeout) {
        //返回的响应数据
        HttpResponseData responseData = new HttpResponseData();
        
        HttpPost httpPost = new HttpPost(url);

        //第二次登录必须设置详细的头信息
        httpPost.setHeader("Accept", "*/*");
        httpPost.setHeader("Accept-Encoding", "gzip,deflate,sdch");
        httpPost.setHeader("Accept-Language", "zh-CN,zh;q=0.8,en;q=0.6,zh-TW;q=0.4,ja;q=0.2");
        httpPost.setHeader("Connection", "keep-alive");
        httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
        httpPost.setHeader("Referer", "http://d.web2.qq.com/proxy.html?v=20130916001&callback=1&id=2");
        httpPost.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/34.0.1847.116 Safari/537.36");
        
        //设置超时
        //RequestConfig rc = RequestConfig.custom().setSocketTimeout(1000).setConnectTimeout(1000).build();//设置请求和传输超时时间
        RequestConfig rc = RequestConfig.custom().setSocketTimeout(2000).setConnectTimeout(timeout).build();//设置请求和传输超时时间
        httpPost.setConfig(rc);
        
        
        //请求上下文，用以获得cookie
        HttpClientContext context = HttpClientContext.create();
        
        //设置post参数
        List<NameValuePair> formparams = new ArrayList<NameValuePair>();
        
        Iterator<String> it= params.keySet().iterator();
        while (it.hasNext()) {
            String key = it.next();
            String value = params.get(key);
            formparams.add(new BasicNameValuePair(key, value));
        }
        
        String content = StringUtils.EMPTY;
        try
        {
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams, "UTF-8");
            httpPost.setEntity(entity);

            CloseableHttpResponse response = httpclient.execute(httpPost, context);
            
            logger.info("执行POST请求:" + httpPost.toString());
            //获取返回的内容
            content = new String(EntityUtils.toByteArray(response.getEntity()), "UTF-8");
            responseData.setContent(content);
            //存放最新的cookies
            responseData.setCookies(context.getCookieStore().getCookies());
            responseData.setHttpClientContext(context);
            
            if (response.getStatusLine().getStatusCode() == 200) {
                logger.info("\n\r");
                logger.info("----------------------------------------");
                logger.info("返回状态：" + response.getStatusLine().toString());
                if (content.length() < 800) {
                    logger.info("返回信息：" + content);
                } else {
                    logger.info("返回信息：" + "大量信息超过800字");
                }
                logger.info("----------------------------------------");
            } else {
                logger.info("----------------------------------------");
                logger.info("返回状态：" + response.getStatusLine().toString());
                logger.info("----------------------------------------");
            }
            
           
            
        } catch (SocketTimeoutException e1) {
            //do nothing
        }catch (Exception e2)
        {
            responseData.setContent("发生异常没有获得数据");
            responseData.setHttpClientContext(context);
            logger.error("post method",e2);
        }
        
        return responseData;
    }

}
