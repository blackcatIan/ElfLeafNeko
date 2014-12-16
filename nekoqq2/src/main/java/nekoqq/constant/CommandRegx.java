package nekoqq.constant;

public interface CommandRegx {
    
    /**help帮助信息**/
    public final static String nekoHelpRegx = "[\\w\\W]*小猫帮助[\\w\\W]*";
    
    /**eve物价**/
    public final static String evePriceRegx = "[\\w\\W]*\\.eve [\\w\\W]+";
    
    /**eve物价兼容**/
    public final static String evePriceCompatibleRegx = "[\\w\\W]*\\.jita [\\w\\W]+";
    
    /**mabinogi物价**/
    public final static String lqPriceRegx = "[\\w\\W]*\\.lq -ad [\\w\\W]+";
    
    /**mabinogi每日任务**/
    public final static String lqTaskRegx = "[\\w\\W]*\\.lq -task[\\w\\W]*";
   
    /**mabinogi走私看板**/
    public final static String lqTradeRegx = "[\\w\\W]*\\.lq -trade[\\w\\W]*";
}
