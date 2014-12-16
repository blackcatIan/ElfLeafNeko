package nekoqq.push.cpt;

import org.springframework.stereotype.Component;

import nekoqq.helper.ThreadManagerHelper;
import nekoqq.push.thread.GroupNoticeRunnable;
import nekoqq.push.thread.LimitMsgCountRunnable;
import nekoqq.push.thread.PushRunnable2;


/**
 * 发送消息
 */
@Component
public class PushCpt {
    /**
     * 开始消息发送相关线程
     */
    public void startPush() {
        PushRunnable2 pushRunnable = new PushRunnable2();
        
        ThreadManagerHelper.threadSet.add(pushRunnable);
        
        Thread smThread = new Thread(pushRunnable);
        smThread.start();
        
        
        //消息限制数量线程下
        LimitMsgCountRunnable limitMsgCountRunnable = new LimitMsgCountRunnable();
        ThreadManagerHelper.threadSet.add(limitMsgCountRunnable);
        Thread limitThread = new Thread(limitMsgCountRunnable);
        limitThread.start();
        
        
        //公告线程
        GroupNoticeRunnable groupNoticeRunnable = new GroupNoticeRunnable();
        ThreadManagerHelper.threadSet.add(groupNoticeRunnable);
        Thread groupNoticeThread = new Thread(groupNoticeRunnable);
        groupNoticeThread.start();
        
    }
}
