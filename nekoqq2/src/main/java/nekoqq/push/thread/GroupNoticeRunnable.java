package nekoqq.push.thread;

import java.net.URLDecoder;
import java.util.Set;

import nekoqq.beans.SendMsgBean;
import nekoqq.beans.SendMsgList;
import nekoqq.helper.ApplicationContextHelper;
import nekoqq.helper.LoginHelper;
import nekoqq.inter.CanStopThread;
import nekoqq.login.svc.GetContactsSvc;
import nekoqq.push.svc.SendMsgSvc;
import nekoqq.utils.file.UtilsFile;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;

public class GroupNoticeRunnable  implements Runnable, CanStopThread{
    
    
    private Logger logger = Logger.getLogger(GroupNoticeRunnable.class);

    private boolean continueFlag = true;
    
    private SendMsgSvc sendMsgSvc;
    
    private GetContactsSvc getContactsSvc;
    
    
    public GroupNoticeRunnable() {
        ApplicationContext ac = ApplicationContextHelper.getApplicationContext();
        if (ac != null) {
            sendMsgSvc = (SendMsgSvc) ac.getBean("sendMsgSvc");
            getContactsSvc = (GetContactsSvc) ac.getBean("getContactsSvc");
        }
    }
    
    
    public void cancel() {
        continueFlag = false;
        
    }

    public void run() {
        try {
            Thread.sleep(15 * 60 * 1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        while (continueFlag) {
            try {
                Thread.sleep(3 * 60 * 1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            //读取公告
            String path = Thread.currentThread().getContextClassLoader()
                                .getResource("").getPath() + "/HELP/GROUPNOTICE";
            try {
                path = URLDecoder.decode(path, "utf-8");
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
            
            String msg = UtilsFile.readTxtFile(path);
            msg = msg.replaceAll("\r\n", "\\\\n");
            
            //清空公告(只发送一次)
            String newStr = StringUtils.EMPTY;
            UtilsFile.writeTxtFile(path, newStr);
            
            
            if(!msg.trim().equals(StringUtils.EMPTY)) {
                
                SendMsgList msgList = SendMsgList.getInstance();
                
                //刷新群列表
                getContactsSvc.getGroupList();
                Set<Long> groupList = LoginHelper.loginUserInfoBean.getGroupList();
                //发送公告
                try {
                    for(Long gid : groupList) {
                        SendMsgBean sendMsgBean = new SendMsgBean();
                        sendMsgBean.setGid(gid);
                        sendMsgBean.setSendMsg(msg);
                        
                        msgList.add(sendMsgBean);
                        //最快群发公告消息速度间隔
                        Thread.sleep(3 * 60 * 1000);
                    }
                } catch (Exception e) {
                    logger.error("groupNoticeRunnable ", e);
                }
                
                
                
            }
            
            
        }
        
    }

}
