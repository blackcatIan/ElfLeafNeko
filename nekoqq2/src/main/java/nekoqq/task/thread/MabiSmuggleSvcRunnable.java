package nekoqq.task.thread;

import nekoqq.helper.ThreadHttpClientHelper;
import nekoqq.inter.CanStopThread;
import nekoqq.task.mabinogi.cache.MabiSmuggleCache;
import nekoqq.task.mabinogi.constant.MabinogiUrl;
import nekoqq.task.mabinogi.constant.SmuggleTranslate;
import nekoqq.utils.httpclient.UtilsHttpClient;
import nekoqq.utils.httpclient.beans.HttpResponseData;

import org.apache.commons.lang.StringUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class MabiSmuggleSvcRunnable implements Runnable, CanStopThread {

    private boolean continueFlag = true;

    public void cancel() {

        continueFlag = false;
    }

    public void run() {
        // 刷新走私看板缓存
        while (continueFlag) {
            StringBuilder sb = new StringBuilder();

            CloseableHttpClient httpclient = ThreadHttpClientHelper.httplientMap.get("mabinogi_smuggle");

            HttpResponseData responseData = null;
            boolean tryGetInfoFlag = true;
            Integer tryGetInfoCount = 0;

            while (tryGetInfoFlag) {
                if (tryGetInfoCount >= 5 && responseData == null) {
                    tryGetInfoFlag = false;
                    break;
                }
                if (responseData != null) {
                    break;
                }
                try {
                    tryGetInfoCount++;
                    responseData = UtilsHttpClient.analogGetMethod(httpclient, MabinogiUrl.SMUGGLE_URL, false);
                } catch (Exception e) {
                    if (tryGetInfoCount >= 5) {
                        tryGetInfoFlag = false;
                    }
                }

            }

            if (responseData != null) {
                String html = responseData.getContent();
                if (html != null) {

                    sb.append("mabinogi走私时间表:\\n");

                    Document doc = Jsoup.parse(html);
                    Elements tables = doc.getElementsByAttributeValue("bgcolor", "#f5b814");
                    if (tables != null) {
                        Element smuggleTable = tables.get(0);

                        Elements trs = smuggleTable.getElementsByTag("tr");

                        // 网站上是倒序的...
                        for (int i = 2; i >= 0; i--) {

                            // 防止数据量不够数组越界
                            if (i > trs.size() - 1) {
                                i = trs.size() - 1;
                            }
                            Element peekTr = trs.get(i);
                            Elements tds = peekTr.getElementsByTag("td");

                            String smuggleTime = tds.get(0).text();

                            sb.append("现实世界时间:" + smuggleTime + "\\n");

                            String smugStringArea = tds.get(1).text();
                            smugStringArea = SmuggleTranslate.SmuggleWordMap.get(smugStringArea);
                            smugStringArea = smugStringArea == null ? StringUtils.EMPTY : smugStringArea;
                            String smuStringGoods = tds.get(2).text();
                            smuStringGoods = SmuggleTranslate.SmuggleWordMap.get(smuStringGoods);
                            smuStringGoods = smuStringGoods == null ? StringUtils.EMPTY : smuStringGoods;
                            // 地点 货物名
                            sb.append("地点:" + smugStringArea + "      货物:" + smuStringGoods + "\\n");

                            sb.append("---------------------------------\\n");


                        }// end of for (int i = 2; i >= 0; i--)
                        
                        
                        //刷新缓存中的看板内容
                        MabiSmuggleCache.getInstance().setContent(sb.toString());

                    } else {
                        sb.append("数据格式变更，请通知作者这个bug");
                    }
                }// end of if (html != null) {

               // sendMsgBean.setSendMsg(sb.toString());
            }//if (responseData != null)
            
            try {
                Thread.sleep(60*1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            
        }//while (continueFlag) 

    }

}
