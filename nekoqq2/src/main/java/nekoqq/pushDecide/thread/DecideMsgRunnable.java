package nekoqq.pushDecide.thread;

import nekoqq.beans.ReceiveMsgBean;
import nekoqq.beans.SendMsgBean;
import nekoqq.helper.ApplicationContextHelper;
import nekoqq.inter.CanStopThread;
import nekoqq.pushDecide.svc.DecideMsgSvc;

import org.springframework.context.ApplicationContext;

public class DecideMsgRunnable implements Runnable, CanStopThread {

    private boolean continueFlag = true;
    
    private DecideMsgSvc decideMsgSvc;
    
    private SendMsgBean sendMsgBean;
    
    private ReceiveMsgBean receiveMsgBean;
    
    public DecideMsgRunnable(ReceiveMsgBean receiveMsgBean) {
        this.receiveMsgBean = receiveMsgBean;
        ApplicationContext ac = ApplicationContextHelper.getApplicationContext();
        if (ac != null) {
            decideMsgSvc = (DecideMsgSvc) ac.getBean("decideMsgSvc");
        }
    }
    
    public void cancel() {
        continueFlag = false;
    }

    public void run() {
        decideMsgSvc.decideMsg(receiveMsgBean);
    }

}
