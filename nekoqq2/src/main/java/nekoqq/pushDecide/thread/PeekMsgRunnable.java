package nekoqq.pushDecide.thread;

import nekoqq.beans.ReceiveMsgBean;
import nekoqq.beans.ReceiveMsgList;
import nekoqq.beans.SendMsgBean;
import nekoqq.inter.CanStopThread;

public class PeekMsgRunnable implements Runnable, CanStopThread {

    private boolean continueFlag = true;

    public void cancel() {
        continueFlag = false;
    }

    public void run() {
        while (continueFlag) {
            ReceiveMsgBean receiveMsgBean = ReceiveMsgList.getInstance().peekMsgBean();
            
            if (receiveMsgBean != null) {
                Long gid = receiveMsgBean.getGid();
                
                //创建生成消息的线程,并发处理消息回应
                DecideMsgRunnable dmRunnable = new DecideMsgRunnable(receiveMsgBean);
                
                Thread tid = new Thread(dmRunnable);
                tid.start();
            }
            
            try {
                Thread.sleep(10);
            } catch (Exception e) {
                e.printStackTrace();
            }
            

        }
    }

}
