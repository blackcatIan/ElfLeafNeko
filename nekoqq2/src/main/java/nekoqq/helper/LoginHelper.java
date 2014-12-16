package nekoqq.helper;

import nekoqq.beans.LoginUserInfoBean;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;


public class LoginHelper {

    //public static PoolingHttpClientConnectionManager pooConlManager = new PoolingHttpClientConnectionManager();

    public static CloseableHttpClient httpclient = ThreadHttpClientHelper.httplientMap.get("nekoqq");

    //2114055230
    public static LoginUserInfoBean loginUserInfoBean = new LoginUserInfoBean(2114055230, "isa544341779");
    
    private LoginHelper(){
    }
    

}
