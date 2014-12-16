package nekoqq.helper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * 用于获得spring元注解的上下文
 */
public class ApplicationContextHelper {
    
    private static ApplicationContext applicationContext;
    
    private ApplicationContextHelper(){}
    
    public static void saveApplicationContext(ApplicationContext wac) {
        if (applicationContext == null) {
            //applicationContext = WebApplicationContextUtils.getWebApplicationContext(request.getServletContext());
            applicationContext = wac;
        }
        
    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }
}
