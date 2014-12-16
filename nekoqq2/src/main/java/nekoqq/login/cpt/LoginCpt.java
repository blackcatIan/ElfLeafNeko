package nekoqq.login.cpt;

import nekoqq.helper.LoginHelper;
import nekoqq.helper.StatusHelper;
import nekoqq.login.svc.GetContactsSvc;
import nekoqq.login.svc.LoginSvc;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LoginCpt {

    private static Logger logger = Logger.getLogger(LoginCpt.class);
    
    @Autowired
    private LoginSvc loginSvc;
    @Autowired
    private GetContactsSvc getContactsSvc;
    
    
    /**
     * 执行登陆部分
     * @return
     */
    public Boolean doLogin() {
        Boolean loginResult = false;
        //登录 
        loginResult = loginSvc.login();
       
        if (loginResult) {
            logger.info("登录成功！(＾－＾)V");
        } else {
            logger.info("登录失败！(>_<)");
        }
        
        
        
        
        return loginResult;
    }
    
    /**
     * 获得联系人信息
     */
    public void getContract() {
        //STEP 获取好友列表,群列表等
        if (StatusHelper.isLogin) {
            getContactsSvc.getGroupList();
            getContactsSvc.getSelfInfo();
        }
    }
    
    
    
    /**
     * 检测是否需要验证码
     * @param httpclient
     * @param loginUserInfoBean
     * @return true需要，false不需要
     */
    public Boolean checkRequiredVerify() {
        //获取Verifysession信息
        loginSvc.getVerifysessionCookie(LoginHelper.httpclient);

        //检测是否需要输入验证码
        Boolean requireVerify = false;
        
        requireVerify = loginSvc.checkRequiredVerify();
        
        return requireVerify;
    }
    
    
    /**
     * 获得默认验证码(当QQ不需要验证码时会根据账号有一个不直接可见的默认验证码)
     * @param loginUserInfoBean
     */
    public void catchDefaultVerifyCode(){
        //STEP获得默认验证码
        loginSvc.catchDefaultVerifyCode(); 
    }
    
    /**
     * 从QQ官网读取验证码图片保留到本地
     * @param httpclient
     * @param loginUserInfoBean
     */
    public void saveVerifyImage() {
        loginSvc.saveVerifyImage();
    }
    
}
