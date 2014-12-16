package nekoqq.task.mabinogi.svc;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import nekoqq.beans.ReceiveMsgBean;
import nekoqq.beans.SendMsgBean;
import nekoqq.beans.SendMsgList;
import nekoqq.helper.ThreadHttpClientHelper;
import nekoqq.task.mabinogi.cache.MabiTaskCache;
import nekoqq.task.mabinogi.constant.MabinogiUrl;
import nekoqq.utils.httpclient.UtilsHttpClient;
import nekoqq.utils.httpclient.beans.HttpResponseData;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

/**
 * 获得每日任务
 */
@Service
public class MabiTaskSvc {
    
    private static Logger logger = Logger.getLogger(MabiTaskSvc.class);
    
    public void getTask(ReceiveMsgBean receiveMsgBean) {
        
        
        SendMsgList msgList = SendMsgList.getInstance();
        SendMsgBean sendMsgBean = new SendMsgBean();
        
        Calendar c = Calendar.getInstance();
        //由于mabinogi日常是早上7点更新，所以系统计算时间应该逆推7小时
        c.add(Calendar.HOUR_OF_DAY, -7);
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd");
        //系统日期
        String systemDate = sdf.format(c.getTime()).toString();
        
        
        
        if (systemDate.equals(MabiTaskCache.getInstance().getDate())) {
          //存在缓存
            sendMsgBean.setGid(receiveMsgBean.getGid());
            sendMsgBean.setSendMsg(MabiTaskCache.getInstance().getContent());
            msgList.add(sendMsgBean);
        } else {
          //不存在缓存
            StringBuilder sb = new StringBuilder();

            CloseableHttpClient httpclient = ThreadHttpClientHelper.httplientMap.get("mabinogi_task");
            
            
//            ThreadGet threadGet = new ThreadGet(httpClient, MabinogiUrl.TASK_URL);
//            threadGet.start();
//            try {
//                threadGet.join();
//            } catch (Exception e) {
//                logger.error("EveSearchPriceSvc",e);
//            }
//            HttpResponseData responseData = threadGet.getResponseData();
            
            HttpResponseData responseData = UtilsHttpClient.analogGetMethod(httpclient, MabinogiUrl.TASK_URL, false);
            
            String html = responseData.getContent();
            if (html != null) {
                Document doc = Jsoup.parse(html);
                Elements today_quests_Date = doc.getElementsByClass("today_quest");
                if(today_quests_Date != null) {
                    Element questDate = today_quests_Date.get(0);
                    //网站上的日期
                    String webQuestDate = questDate.text().substring(4,questDate.text().length());
                    
                    if (systemDate.equals(webQuestDate)) {
                        //任务元素列表
                        Elements tasks = doc.getElementsByClass("area_qst");

                        sb.append("今日任務:\\n");
                        sb.append(tasks.get(0).text() + "\\n");
                        sb.append(tasks.get(1).text() + "\\n");
                        sb.append("----------------------\\n");
                        sb.append("VIP任務:\\n");
                        sb.append(tasks.get(2).text() + "\\n");
                        sb.append(tasks.get(3).text() + "\\n");
                        
                        //记录缓存中
                        MabiTaskCache.getInstance().setDate(systemDate);
                        MabiTaskCache.getInstance().setContent(sb.toString());
                        
                    } else {
                        sb.append("今日任务看板还未更新...请等一段时间后查询");
                    }
                } else {
                    sb.append("每日看板格式已经更新，请通知主人这个BUG");
                }
            } else {
                sb.append("访问每日看板失败..请重试");
            }
            
            sb.append("\\n更多指令请用:小猫帮助");
            
            sendMsgBean.setGid(receiveMsgBean.getGid());
            sendMsgBean.setSendMsg(sb.toString());
            msgList.add(sendMsgBean);
        }

    }
    
    
}
