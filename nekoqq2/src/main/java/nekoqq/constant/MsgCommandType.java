package nekoqq.constant;

public interface MsgCommandType {
    
//    /**当一句话过长的时候的续文,决定消息的时候直接发出续文**/
//    public final static String CONTINUE_CONTENT = "continue_content";
    
    /**eve查询物价**/
    public final static String EVE_PRICE = "eve_price";
    
    /**mabinogi查询物价**/
    public final static String MABINOGI_PRICE = "mabinogi_price";
    
    /**mabinogi任务查询**/
    public final static String MABINOGI_TASK = "mabinogi_task";
    
    /**maibnogi走私查询**/
    public final static String MABINOGI_TRADE = "mabinogi_trade";
    
    /**系统繁忙中**/
    public final static String BUSY = "busy";
    
    /**普通对话消息**/
    public final static String COMMON_MSG = "common_msg";
    
    /**帮助信息**/
    public final static String NEKO_HELP = "neko_help";
}
