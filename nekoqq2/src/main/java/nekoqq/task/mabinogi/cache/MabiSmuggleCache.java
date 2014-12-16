package nekoqq.task.mabinogi.cache;

public class MabiSmuggleCache {
    /**缓存时间**/
    private String date;
    
    /**完整的每日任务内容**/
    private String content;
    
    private static MabiSmuggleCache mabiSmuggleCache;
    
    private MabiSmuggleCache() {}
    
    public static synchronized MabiSmuggleCache getInstance() {
        if (mabiSmuggleCache == null) {
            mabiSmuggleCache = new MabiSmuggleCache();
        }
       return mabiSmuggleCache;
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

    public static MabiSmuggleCache getMabiSmuggleCache() {
        return mabiSmuggleCache;
    }

    public static void setMabiSmuggleCache(MabiSmuggleCache mabiSmuggleCache) {
        MabiSmuggleCache.mabiSmuggleCache = mabiSmuggleCache;
    }
}
