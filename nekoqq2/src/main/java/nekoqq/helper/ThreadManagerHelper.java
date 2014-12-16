package nekoqq.helper;

import java.util.HashSet;
import java.util.Set;

import nekoqq.inter.CanStopThread;

public class ThreadManagerHelper {
    public static Set<CanStopThread> threadSet= new HashSet<CanStopThread>();
    
    private ThreadManagerHelper(){}
    
    
    public static void StopAll(){
        for(CanStopThread cst : threadSet) {
            cst.cancel();
        }
    }

}
