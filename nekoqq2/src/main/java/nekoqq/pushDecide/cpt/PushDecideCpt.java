package nekoqq.pushDecide.cpt;

import org.springframework.stereotype.Component;

import nekoqq.helper.ThreadManagerHelper;
import nekoqq.pushDecide.thread.PeekMsgRunnable;


/**
 * 决策回应消息内容
 */
@Component
public class PushDecideCpt {
    
    
    
    
    /**
     * 开启回话消息处理决策
     */
    public void startPushDecide() {
        PeekMsgRunnable pmRunnable = new PeekMsgRunnable();
        ThreadManagerHelper.threadSet.add(pmRunnable);
        
        Thread tid = new Thread(pmRunnable);
        tid.start();
        
    }
}
