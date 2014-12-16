package nekoqq.push.helper;

import java.util.HashMap;
import java.util.HashSet;


/**
 * 对每个群的查询频度进行限制
 */
public class SendMsgSvcHelper {
    
    private SendMsgSvcHelper(){};
    
    /**默认最大每分钟请求次数**/
    public static Integer maxCount = 4;
    
    
    /**k=gid  value = 每剩余次数请求次数默认一分钟4次**/
    public static HashMap<Long, Integer> msgCountMap = new HashMap<Long, Integer>();
    
    /**可提示消息发送过于频繁标记位，这样 提示消息，不会因为短时间刷频而多次发送，limitMsgCount线程会重置这个Flag*/
    public static HashMap<Long, Boolean> countWarningMap = new HashMap<Long, Boolean>();
    
    
    /**已发送过消息的gid列表**/
    public static HashSet<Long> gidSet = new HashSet<Long>();
    
}
