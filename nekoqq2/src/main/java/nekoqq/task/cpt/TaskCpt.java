package nekoqq.task.cpt;

import org.springframework.stereotype.Component;

import nekoqq.helper.ThreadManagerHelper;
import nekoqq.task.thread.MabiSmuggleSvcRunnable;

@Component
public class TaskCpt {
    
    public void startTaskThread() {
        MabiSmuggleSvcRunnable msRunnable = new MabiSmuggleSvcRunnable();
        
        ThreadManagerHelper.threadSet.add(msRunnable);
        
        Thread msTid = new Thread(msRunnable);
        
        msTid.start();
        
    }
}
