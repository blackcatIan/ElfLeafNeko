package nekoqq.helper;

import java.util.HashMap;

import nekoqq.utils.httpclient.thread.ThreadHttpClient;

import org.apache.http.impl.client.CloseableHttpClient;


/**
 * 为每个不同任务项目访问的“网站不同”创建多线程的httpclient
 */
public class ThreadHttpClientHelper {

    public static HashMap<String, CloseableHttpClient> httplientMap = new HashMap<String, CloseableHttpClient>(){
        {
        put("nekoqq", ThreadHttpClient.getInstance().getNewClient());
        put("eve_market", ThreadHttpClient.getInstance().getNewClient());
        put("mabinogi_task", ThreadHttpClient.getInstance().getNewClient());
        put("mabinogi_smuggle", ThreadHttpClient.getInstance().getNewClient());
        }
    };
    
    private ThreadHttpClientHelper() {
    }
}
