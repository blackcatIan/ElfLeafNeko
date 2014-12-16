package nekoqq.push.thread;

import org.apache.log4j.Logger;

import nekoqq.inter.CanStopThread;
import nekoqq.push.helper.SendMsgSvcHelper;

public class LimitMsgCountRunnable implements Runnable, CanStopThread{

    private Logger logger = Logger.getLogger(LimitMsgCountRunnable.class);
    
    private boolean continueFlag = true;
    
    
    public void cancel() {
        continueFlag = false;
        
    }

    public void run() {
        while(continueFlag) {
            try {
                Thread.sleep((60 / SendMsgSvcHelper.maxCount) * 1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            for(Long peekGid : SendMsgSvcHelper.gidSet) {
                Integer count = SendMsgSvcHelper.msgCountMap.get(peekGid);
                count = count + 1;
                if (count <= SendMsgSvcHelper.maxCount) {
                    SendMsgSvcHelper.msgCountMap.put(peekGid, count);
                    SendMsgSvcHelper.countWarningMap.put(peekGid, true);
                }
            }
            
        }
        
    }

}
