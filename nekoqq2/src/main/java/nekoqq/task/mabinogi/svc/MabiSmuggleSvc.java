package nekoqq.task.mabinogi.svc;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import nekoqq.beans.ReceiveMsgBean;
import nekoqq.beans.SendMsgBean;
import nekoqq.beans.SendMsgList;
import nekoqq.helper.ThreadHttpClientHelper;
import nekoqq.task.mabinogi.cache.MabiSmuggleCache;
import nekoqq.task.mabinogi.constant.MabinogiUrl;
import nekoqq.task.mabinogi.constant.SmuggleTranslate;
import nekoqq.utils.httpclient.UtilsHttpClient;
import nekoqq.utils.httpclient.beans.HttpResponseData;

import org.apache.commons.lang.StringUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;
import org.springframework.stereotype.Service;


/**
 * 洛奇走私看板服务
 */
@Service
public class MabiSmuggleSvc {
//bgcolor="#f5b814"
//http://weather.erinn.biz/smuggler.php
    
    private static Logger logger = Logger.getLogger(MabiSmuggleSvc.class);
    
    
    public void getSmuggleInfo(ReceiveMsgBean receiveMsgBean) {
        String content = MabiSmuggleCache.getInstance().getContent();
        SendMsgList msgList = SendMsgList.getInstance();
        SendMsgBean sendMsgBean = new SendMsgBean();
        sendMsgBean.setGid(receiveMsgBean.getGid());
        
        if (StringUtils.isNotBlank(content)) {
            sendMsgBean.setSendMsg(content);
        } else {
            sendMsgBean.setSendMsg("走私板数据刷新中需要等待1分钟...稍后请重新使用指令查询");
        }
        msgList.add(sendMsgBean);
    }
    
    
//    public void getSmuggleInfo(ReceiveMsgBean receiveMsgBean) {
//        SendMsgList msgList = SendMsgList.getInstance();
//        SendMsgBean sendMsgBean = new SendMsgBean();
//        sendMsgBean.setGid(receiveMsgBean.getGid());
//        
//        Calendar c = Calendar.getInstance();
//        c.add(Calendar.MINUTE, -36);
//        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd");
//        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
//        SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
//        //系统日期
//        String systemDate = sdf.format(c.getTime()).toString();
//        
//        String cacheTime = MabiSmuggleCache.getInstance().getDate();
//
//        
//        //标记是否使用缓存数据
//        boolean useCacheFlag = false;
//        
//        //检测缓存数据是否过期
//        if (StringUtils.isNotBlank(cacheTime)) {
//            Calendar c2 = Calendar.getInstance();
//            try {
//                c2.setTime(sdf3.parse(cacheTime));
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            
//            
//            if (c.before(c2)) {
//                //当前时间还未超过缓存预测记录时间，使用缓存
//                useCacheFlag = true;
//            }
//        }
//        
//        
//        
//        
//        if (useCacheFlag) {
//            sendMsgBean.setSendMsg(MabiSmuggleCache.getInstance().getContent());
//        } else {
//            StringBuilder sb = new StringBuilder();
//
//            CloseableHttpClient httpclient = ThreadHttpClientHelper.httplientMap.get("mabinogi_smuggle");
//            
//            
//            SendMsgBean waitMsgBean = new SendMsgBean();
//            waitMsgBean.setGid(receiveMsgBean.getGid());
//            waitMsgBean.setSendMsg("maibnogi走私信息正在刷新查询中，刷新需要1-3分钟，请等待结果...");
//            msgList.add(waitMsgBean);
//            
//            
//            HttpResponseData responseData = null;
//            boolean tryGetInfoFlag = true;
//            Integer tryGetInfoCount = 0;
//            
//            while (tryGetInfoFlag) {
//                if (tryGetInfoCount >= 3 && responseData == null) {
//                    tryGetInfoFlag = false;
//                    break;
//                }
//                if (responseData != null) {
//                    break;
//                }
//                try {
//                    tryGetInfoCount++;
//                    responseData = UtilsHttpClient.analogGetMethod(httpclient, MabinogiUrl.SMUGGLE_URL, false);
//                } catch (Exception e) {
//                    if (tryGetInfoCount >= 3) {
//                        tryGetInfoFlag = false;
//                    }
//                }
//                
//            }
//            
//            if (responseData != null) {
//                String html = responseData.getContent();
//                if (html != null) {
//                    
//                    sb.append("mabinogi走私时间表:\\n");
//                    
//                    Document doc = Jsoup.parse(html);
//                    Elements  tables = doc.getElementsByAttributeValue("bgcolor", "#f5b814");
//                    if (tables != null) {
//                        Element smuggleTable = tables.get(0);
//                        
//                        Elements trs = smuggleTable.getElementsByTag("tr");
//                        
//                        
//                        //此标志位用来表示记录第一条时间的标志
//                        boolean isSavedDate = false;
//                        //网站上是倒序的...
//                        for (int i = 2; i >= 0; i--) {
//                            
//                            //防止数据量不够数组越界
//                            if (i > trs.size() - 1) {
//                                i = trs.size() - 1;
//                            }
//                            Element peekTr = trs.get(i);
//                            Elements tds = peekTr.getElementsByTag("td");
//                            
//                            String smuggleTime = tds.get(0).text();
//                            
//                            sb.append("现实世界时间:" + smuggleTime + "\\n");
//                            
//                            
//                            String smugStringArea = tds.get(1).text();
//                            smugStringArea = SmuggleTranslate.SmuggleWordMap.get(smugStringArea);
//                            smugStringArea = smugStringArea == null ? StringUtils.EMPTY : smugStringArea;
//                            String smuStringGoods = tds.get(2).text();
//                            smuStringGoods = SmuggleTranslate.SmuggleWordMap.get(smuStringGoods);
//                            smuStringGoods = smuStringGoods == null ? StringUtils.EMPTY : smuStringGoods;
//                            //地点   货物名
//                            sb.append("地点:" + smugStringArea + "      货物:" + smuStringGoods + "\\n");
//                            
//                            sb.append("---------------------------------\\n");
//                            
//                            
//                            if (isSavedDate == false) {
//                                isSavedDate = true;
//                                String cacheFullTime = null;
//                                try {
//                                    cacheFullTime = sdf2.format(c.getTime()) + " " + smuggleTime;
//                                } catch (Exception e) {
//                                    e.printStackTrace();
//                                }
//                                
//                                MabiSmuggleCache.getInstance().setDate(cacheFullTime);
//                            }
//                           
//
//                        }//end of for (int i = 2; i >= 0; i--)
//              
//                        MabiSmuggleCache.getInstance().setContent(sb.toString());
//                        
//                    } else {
//                        sb.append("数据格式变更，请通知作者这个bug");
//                    }
//                }//end of  if (html != null) {
//                
//                sendMsgBean.setSendMsg(sb.toString());
//            } else{//if (responseData != null) 
//                sendMsgBean.setSendMsg("获取数据超时，请稍等1-2分钟后重试请求数据");
//            }
//            
//            
//            
//            
//            
//            
//        }
//        
//     
//     msgList.add(sendMsgBean);
//        
//     
//        
//    }
    
    @Test
    public void test() {
        getSmuggleInfo(null);
        getSmuggleInfo(null);
    }
    
    
}
