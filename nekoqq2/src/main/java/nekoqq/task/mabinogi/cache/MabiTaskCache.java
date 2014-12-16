package nekoqq.task.mabinogi.cache;

public class MabiTaskCache {
    /**缓存时间**/
    private String date;
    
    /**完整的每日任务内容**/
    private String content;
    
    private static MabiTaskCache mabiTaskCache;
    
    private MabiTaskCache() {}
    
    public static synchronized MabiTaskCache getInstance() {
        if (mabiTaskCache == null) {
            mabiTaskCache = new MabiTaskCache();
        }
       return mabiTaskCache;
    }
    
    
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

}
