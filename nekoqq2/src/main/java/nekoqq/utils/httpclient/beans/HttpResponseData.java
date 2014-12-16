package nekoqq.utils.httpclient.beans;

import java.util.List;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.cookie.Cookie;

/**
 * httpclient请求后reponse返回的结果的bean
 */
public class HttpResponseData {
    /**响应结果**/
    private CloseableHttpResponse response;
    
    /**响应内容**/
    private String content;
    
    /**cookie内容**/
    private List<Cookie> cookies;
    
    /**http上下文**/
    private HttpClientContext httpClientContext;
    
    /**字节流**/
    private byte contentByte[];

    public CloseableHttpResponse getResponse() {
        return response;
    }

    public void setResponse(CloseableHttpResponse response) {
        this.response = response;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<Cookie> getCookies() {
        return cookies;
    }

    public void setCookies(List<Cookie> cookies) {
        this.cookies = cookies;
    }

    public HttpClientContext getHttpClientContext() {
        return httpClientContext;
    }

    public void setHttpClientContext(HttpClientContext httpClientContext) {
        this.httpClientContext = httpClientContext;
    }

    public byte[] getContentByte() {
        return contentByte;
    }

    public void setContentByte(byte[] contentByte) {
        this.contentByte = contentByte;
    }
    
}
