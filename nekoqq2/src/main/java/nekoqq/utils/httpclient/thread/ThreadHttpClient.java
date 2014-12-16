package nekoqq.utils.httpclient.thread;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

/**
 * 获得一个多线程的httpclient实例
 */
public class ThreadHttpClient {
    
    private static ThreadHttpClient threadClient= null;
    
//    private CloseableHttpClient httpclient;
//    
//    private HttpClientBuilder httpClientBuilder;
    
    private ThreadHttpClient(){
//        PoolingHttpClientConnectionManager pooConlManager = new PoolingHttpClientConnectionManager();
//        pooConlManager.setMaxTotal(100);
//        httpClientBuilder = HttpClients.custom().setConnectionManager(pooConlManager);
//        httpclient = httpClientBuilder.build();
    }
    
    
    public static synchronized ThreadHttpClient getInstance() {
        if (threadClient == null) {
            threadClient = new ThreadHttpClient();
        }
        return threadClient;
    }
    
    
    
    public CloseableHttpClient getNewClient() {
        
        PoolingHttpClientConnectionManager pooConlManager = new PoolingHttpClientConnectionManager();
        pooConlManager.setMaxTotal(50);
//        httpClientBuilder = HttpClients.custom().setConnectionManager(pooConlManager);
//        httpclient = httpClientBuilder.build();
        return HttpClients.custom().setConnectionManager(pooConlManager).build();
        //return httpclient;
    }
}
