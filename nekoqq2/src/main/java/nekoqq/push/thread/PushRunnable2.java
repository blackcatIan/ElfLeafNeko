package nekoqq.push.thread;

import nekoqq.beans.SendMsgBean;
import nekoqq.beans.SendMsgList;
import nekoqq.helper.ApplicationContextHelper;
import nekoqq.inter.CanStopThread;
import nekoqq.push.svc.SendMsgSvc;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;

public class PushRunnable2 implements Runnable, CanStopThread {
    
    private Logger logger = Logger.getLogger(PushRunnable2.class);
    
    private boolean continueFlag = true;
    
    private SendMsgSvc sendMsgSvc;
    
    
    public PushRunnable2() {
        ApplicationContext ac = ApplicationContextHelper.getApplicationContext();
        if (ac != null) {
            sendMsgSvc = (SendMsgSvc) ac.getBean("sendMsgSvc");
        }
    }
    
    public void cancel() {
        continueFlag = false;

    }

    public void run() {
        sendMsg();

    }
    
    public synchronized void sendMsg() {

        SendMsgList msgList = SendMsgList.getInstance();
        try {
            while (continueFlag) {
                SendMsgBean msgBean = msgList.peekMsgBean();
                if (msgBean != null) {
                    Long gid = msgBean.getGid();
                    String sendMsg = msgBean.getSendMsg();
                    
                    if (sendMsg != StringUtils.EMPTY) {
                        sendMsgSvc.sendGroupMsg(gid, sendMsg);
                        // 最快消息速度间隔
                        Thread.sleep(500);
                    } //{} end of if (sendMsg != StringUtils.EMPTY)
                    
                } else {

                    Thread.sleep(30);   
                    
                }//{} end of if (msgBean != null)
                
            }//{}end of while
        } catch (Exception e) {
            logger.error("SendMsgRunnable ", e);
        }
    }

}
