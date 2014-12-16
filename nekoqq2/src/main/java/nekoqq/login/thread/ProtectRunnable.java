package nekoqq.login.thread;

import nekoqq.helper.ApplicationContextHelper;
import nekoqq.helper.StatusHelper;
import nekoqq.helper.ThreadManagerHelper;
import nekoqq.login.ctrl.LoginCtrl;

import org.springframework.context.ApplicationContext;

public class ProtectRunnable implements Runnable{

    private LoginCtrl loginCtrl;
    
    public ProtectRunnable() {
        ApplicationContext ac = ApplicationContextHelper.getApplicationContext();
        if (ac != null) {
            loginCtrl = (LoginCtrl) ac.getBean("loginCtrl");
        }
    }
    
    
    public void run() {
        while(true) {
            //被踢下线了
            try {
                if (StatusHelper.isLogin == false) {
                    
                    Thread.sleep(60*1000);
                    //释放所有线程
                    ThreadManagerHelper.StopAll();
                    //清空线程缓存
                    ThreadManagerHelper.threadSet.clear();
                    //重启登录
                    //MockHttpServletRequest request = new MockHttpServletRequest("GET","/login");
                    loginCtrl.login(null, null, null);
                }
            
           
                Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
            
        }
    }

}
