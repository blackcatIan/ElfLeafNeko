package nekoqq.pull.thread;

import java.util.HashMap;
import java.util.Map;

import nekoqq.beans.LoginUserInfoBean;
import nekoqq.constant.WebQQUrlConst;
import nekoqq.helper.ApplicationContextHelper;
import nekoqq.helper.LoginHelper;
import nekoqq.helper.StatusHelper;
import nekoqq.inter.CanStopThread;
import nekoqq.pull.svc.ReceiveMsgSvc;
import nekoqq.utils.httpclient.UtilsHttpClient;
import nekoqq.utils.httpclient.beans.HttpResponseData;
import net.sf.json.JSONObject;

import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;

public class PullRunnable implements Runnable , CanStopThread{
    
    private static Logger logger = Logger.getLogger(PullRunnable.class);
    
    
    private boolean continueFlag = true;
    
    private ReceiveMsgSvc receiveMsgSvc;

    public PullRunnable() {
        ApplicationContext ac = ApplicationContextHelper.getApplicationContext();
        if (ac != null) {
            receiveMsgSvc = (ReceiveMsgSvc) ac.getBean("receiveMsgSvc");
        }
    }
    
    
    public void run() {
        pull();
    }
    
    

    /**
     * 进行推送，每一分钟1次
     */
    public void pull() {

        LoginUserInfoBean loginUserInfoBean = LoginHelper.loginUserInfoBean;
        
        String name = "r";
        String value = "{\"ptwebqq\":\"" + loginUserInfoBean.getPtwebqq() 
                + "\",\"clientid\":" + loginUserInfoBean.getClientid() 
                + ",\"psessionid\":\""+ loginUserInfoBean.getPsessionid() +"\",\"key\":\"\"}";
        
        Map<String,String> params = new HashMap<String, String>();
        params.put(name, value);
        
        
            while(continueFlag) {
                try {
//                    PollThreadPostHelper threadPost = new PollThreadPostHelper(httpclient, WebQQUrlConst.POLL_URL,params);
//                    //请求推送
//                    synchronized (PollThreadLockHelper.commonLock) {
//                        threadPost.start();
//                        //这里如果不join的话不会获得数据
//                        //threadPost.join();
//                        PollThreadLockHelper.commonLock.wait();
//                    }  
//                    HttpResponseData responseData = threadPost.getResponseData();
                    
                    //网络阻塞
                    HttpResponseData responseData = null;
                    
                    responseData = UtilsHttpClient.analogPostMethod(LoginHelper.httpclient, WebQQUrlConst.POLL_URL, params, 1000);
                    
                    
                    logger.info("以上为pull消息");
                    
                    //处理返回信息
                    if (responseData != null) {
                        dealRetcode(responseData);
                    }
                    
                    
//                    synchronized (SendMsgThreadLockHelper.commonLock) {
//                        SendMsgThreadLockHelper.commonLock.notify();
//                    }
                    
                    
                    //Thread.sleep(3 * 1000);
                    
                } catch (Exception e) {
                    logger.error("pullRunnable",e);
                }//{} end of try
                
                
                
                
            }//{}end of while
    }
    
    
    
    /**
     * 处理推送返回码
     * @param responseData http返回数据
     */
    private void dealRetcode(HttpResponseData responseData) {
        String content = responseData.getContent();
        //返回的json
        JSONObject respPollJsonObj = JSONObject.fromObject(content);
        
        if (respPollJsonObj == null) {
            return;
        } else {
            Object retcodeObj = null;
            try {
                retcodeObj = respPollJsonObj.get("retcode");
            } catch (Exception e) {
                return;
            }
            if (retcodeObj == null) {
                return;
            }
            
            Integer retcode = retcodeObj == null? -1 : Integer.valueOf(retcodeObj.toString());
            logger.info("捕捉到retcode:" + retcode);

            switch (retcode) {
                case 0:
                    receiveMsgSvc.receiveMsg(responseData);
                    break;
                case 102:
                    //正常连接没有消息
                    //donothing
                    break;
                case 103:
                    //掉线了
                    logger.error("掉线啦！！retcode" + retcode);
                    StatusHelper.isLogin = false;
                    break;
                case 116:
                    //心跳检测机制
                    dealHeartBeatVerify(responseData, respPollJsonObj);
                    break;
                case 121:
                    //掉线了
                    logger.error("掉线啦！！retcode" + retcode);
                    StatusHelper.isLogin = false;
                    break;
                default:
                    //日志输出无法识别的返回码
                    logger.error("无法识别retcode" + retcode);
                    StatusHelper.isLogin = false;
                    break;
            }
            
        }
        
       
    }
    

    
    
    /**
     * 心跳机制会定期发送一个新的ptwebqq用以更新cookie
     * @param responseData
     * @param respPollJsonObj
     */
    /**
     * @param responseData
     * @param respPollJsonObj
     */
    private void dealHeartBeatVerify(HttpResponseData responseData, JSONObject respPollJsonObj) {
        //新的ptwebqq
        String ptwebqq = respPollJsonObj.getString("p").toString();
        
        //更新登录者信息中的ptwebqq
        LoginHelper.loginUserInfoBean.setPtwebqq(ptwebqq);
        
        //更新cookie的ptwebqq
        HttpClientContext context = responseData.getHttpClientContext();
        BasicClientCookie cookie = new BasicClientCookie("ptwebqq", ptwebqq);
        cookie.setVersion(0);
        cookie.setDomain("ptlogin2.qq.com");
        cookie.setPath("/");
        context.getCookieStore().addCookie(cookie);
    }


    public void cancel() {
        this.continueFlag = false;
    }    
    
    
}
