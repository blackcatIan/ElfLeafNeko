package nekoqq.pull.cpt;

import org.springframework.stereotype.Component;

import nekoqq.helper.ThreadManagerHelper;
import nekoqq.pull.thread.PullRunnable;

@Component
public class PullCpt {
    //开启pull线程
    public void startPull() {
        //创建线程执行体
        PullRunnable pollRunnable = new PullRunnable();
        
        //把线程注册到管理中
        ThreadManagerHelper.threadSet.add(pollRunnable);

        //创建线程
        Thread pollthread = new Thread(pollRunnable);
        pollthread.start();
    }
}
