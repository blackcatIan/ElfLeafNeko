package nekoqq.task.eve.svc;

import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nekoqq.beans.ReceiveMsgBean;
import nekoqq.beans.SendMsgBean;
import nekoqq.beans.SendMsgList;
import nekoqq.constant.CommandRegx;
import nekoqq.helper.ThreadHttpClientHelper;
import nekoqq.task.eve.constant.EveMarketUrl;
import nekoqq.utils.formatData.UtilsUnicode;
import nekoqq.utils.httpclient.UtilsHttpClient;
import nekoqq.utils.httpclient.beans.HttpResponseData;
import nekoqq.utils.httpclient.thread.ThreadHttpClient;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

@Service
public class EveSearchPriceSvc {
    
    private static Logger logger = Logger.getLogger(EveSearchPriceSvc.class);
    
    
    /**
     * 查询物价
     * @param name 物品名称
     */
    public void search(ReceiveMsgBean receiveMsgBean) {
        //返回结果
        String retString;
        
        //发送消息列队
        SendMsgList msgList = SendMsgList.getInstance();
        
        //要返回的结果的缓冲字
        StringBuffer searchRet = new StringBuffer();
        
        String recevieMsg = receiveMsgBean.getReceiveMsg();
        
        String regx = CommandRegx.evePriceRegx;
        Pattern p = Pattern.compile(regx);
        Matcher m = p.matcher(recevieMsg);
        //搜索物品字段名
        String name =StringUtils.EMPTY;
        String matchStr =StringUtils.EMPTY;
        Integer beginIndex = 0;
        if (m.matches()) {
            matchStr = m.group(0);
            beginIndex = matchStr.lastIndexOf(".eve ") + ".eve ".length();
            name = matchStr.substring(beginIndex, matchStr.length()).trim();;
        }
        
        //兼容.jita
        regx = CommandRegx.evePriceCompatibleRegx;
        p = Pattern.compile(regx);
        m = p.matcher(recevieMsg);
        if (m.matches()) {
            matchStr = m.group(0);
            beginIndex = matchStr.lastIndexOf(".jita ") + ".jita ".length();
            name = matchStr.substring(beginIndex, matchStr.length()).trim();;
            //searchRet.append("目前兼容.jita标准指令为【.eve 物品名】，查询帮助手册请输入:小猫帮助\\n");
        }
        
        
        
        if (name.equals(StringUtils.EMPTY)) {
            retString = ".eve 物品名称不能为空";
        } else {

            searchRet.append("Eve-market市场查询:"+ name +"\\n");
            
            //CloseableHttpClient httpClient = ThreadHttpClient.getInstance().getClient();
            CloseableHttpClient httpClient = ThreadHttpClientHelper.httplientMap.get("eve_market");
            
            //中文编码为url
            String encodeName = StringUtils.EMPTY;
            try {
                encodeName = URLEncoder.encode(name,"UTF-8");
            } catch (Exception e) {
                logger.info("eveSearchSvc",e);
            }
            String url = EveMarketUrl.SEARCH_PRICE.replaceAll("#name#", encodeName).replaceAll("#regionid#", "0");
            
            
//            ThreadGet threadGet = new ThreadGet(httpClient, url);
//            threadGet.start();
//            try {
//                threadGet.join();
//            } catch (Exception e) {
//                logger.error("EveSearchPriceSvc",e);
//            }
//            
//            HttpResponseData responseData = threadGet.getResponseData();
            
            HttpResponseData responseData = UtilsHttpClient.analogGetMethod(httpClient, url, false);
            
            //访问市场失败
            if (responseData.getContent() != null) {
    
                //处理返回信息
                JSONArray resultArray = JSONArray.fromObject(responseData.getContent());
                if (resultArray.isEmpty()) {
                    searchRet.append("没有符合条件的结果,支持模糊搜索,可尝试不用全名");
                }
                //结果小于5条时输出价位单，大于5条时做过多结果处理
                if (resultArray.size() > 5) {
                    //过多结果分两种情况，100条以内作为备选输出，100条以上提示给出更多关键字
                    if (resultArray.size() < 70) {
                        searchRet.append("结果超过5条,请从" + resultArray.size() + "个候选中给出更精确的关键字\\n");
                        
                        Object tempObj = null;;
                        for(Object peekObj : resultArray) {
                            //结果中的一条
                            JSONObject reusltItem = JSONObject.fromObject(peekObj);
        
                            //物品全名
                            String typeName = UtilsUnicode.unicode2str(reusltItem.getString("typename"));
                            if (typeName.equals(name)) {
                                //当有一条结果是完全符合的时候存入缓存
                                tempObj = peekObj;
                            }
                            
                            typeName = "【" + typeName + "】";
                            searchRet.append(typeName);
                        }
                        
                        //有时完全匹配字段应该显示出来
                        if (tempObj != null) {
                            searchRet.append("\\n");
                            JSONObject reusltItem = JSONObject.fromObject(tempObj);
                            appendItemResult(reusltItem, searchRet);
                        }
                        
                    } else {
                        searchRet.append("结果超过70个,请给出更精确的关键字\\n");
                    }//{} end of if (resultArray.size() < 100)
                   
                } else {
                    for(Object peekObj : resultArray) {
                        //结果中的一条
                        JSONObject reusltItem = JSONObject.fromObject(peekObj);
                        
                        appendItemResult(reusltItem, searchRet);
                    }
                }
                
                searchRet.append("更多指令请用:小猫帮助\\n");
                //处理超长问题
                retString = searchRet.toString();
        
                Integer cutMarkIndex = cutMarkIndex(searchRet.toString());
                
                if (cutMarkIndex >= 0) {
                    retString = searchRet.substring(0, cutMarkIndex);
                    //插入切下的头一段文字
                    SendMsgBean insertHeadBean =  new SendMsgBean();
                    insertHeadBean.setGid(receiveMsgBean.getGid());
                    insertHeadBean.setSendMsg(retString);
                    //msgList.insetMsgBean(insertBean, 0);
                    msgList.add(insertHeadBean);
                    
                    //残余文本
                    String residualString = searchRet.substring(cutMarkIndex,searchRet.length());
                    while(residualString.length() > 350) {
                        //循环分割插入消息列队
                        Integer residualCutIndex = cutMarkIndex(residualString);
                        String cutResidual = residualString.substring(0,residualCutIndex);
                        residualString = residualString.substring(residualCutIndex,residualString.length());
                        
                        //插入消息列队
                        SendMsgBean insertBean =  new SendMsgBean();
                        insertBean.setGid(receiveMsgBean.getGid());
                        //设置为续文
                        //insertBean.setMsgCommandType(MsgCommandType.CONTINUE_CONTENT);
                        //发送内容为分割文本
                        insertBean.setSendMsg(cutResidual);
                        //msgList.insetMsgBean(insertBean, 0);
                        msgList.add(insertBean);
                    }
                    //最后将最后最后最后残余文本插入列队
                    if (residualString.length() > 0) {
                        SendMsgBean insertBean =  new SendMsgBean();
                        insertBean.setGid(receiveMsgBean.getGid());
                        //设置为续文
                        //insertBean.setMsgCommandType(MsgCommandType.CONTINUE_CONTENT);
                        //注意这里是残余文本
                        insertBean.setSendMsg(residualString);
                        //msgList.insetMsgBean(insertBean, 0);
                        msgList.add(insertBean);
                    }
                } else {
                    SendMsgBean insertBean =  new SendMsgBean();
                    insertBean.setGid(receiveMsgBean.getGid());
                    insertBean.setSendMsg(retString);
                    msgList.add(insertBean);
                }//end else of if (cutMarkIndex >= 0) {
                

            } else {
                retString = "访问eve-market失败，请重试";
                SendMsgBean insertBean =  new SendMsgBean();
                insertBean.setGid(receiveMsgBean.getGid());
                insertBean.setSendMsg(retString);
                msgList.add(insertBean);
                
            }//end of  if (responseData.getContent() != null) 
        }//end of  name.equals(StringUtils.EMPTY)
        
    }
    
    /**
     * 添加查询结果
     */
    private void appendItemResult(JSONObject reusltItem,StringBuffer searchRet) {
        String sell = (new DecimalFormat(",###.00").format(reusltItem.getDouble("sell"))).toString();
        //防止0错
        if (!sell.contains(".")) {
            sell += ".00";
        }
        
        String buy = (new DecimalFormat(",###.00").format(reusltItem.getDouble("buy"))).toString();
        //防止0错
        if (!buy.contains(".")) {
            buy += ".00";
        }
        
        //物品全名
        String typeName = UtilsUnicode.unicode2str(reusltItem.getString("typename"));
        //更新时间
        String time = reusltItem.getString("time");
        //searchRet.append("----------------\\n");
        
        searchRet.append("    \\n");
        searchRet.append("物品名称: " + typeName + "\\n");
        
        String sellArray[] = sell.split("\\.");
        searchRet.append("sell: " + sellArray[0] + "   ." + sellArray[1] + " ISK\\n");
        
        String buyArray[] = buy.split("\\.");
        searchRet.append("buy: " + buyArray[0] + "   ." + buyArray[1] + " ISK\\n");
        
        searchRet.append("更新时间: " + time + "\\n");
        searchRet.append("【----------------】\\n");
    }
    
    
    
    /**
     * 当文本超长时获得截断的位置
     * @return
     */
    private Integer cutMarkIndex(String str) {
        Integer lastMark = -1;
        if (str.length() > 350) {
            String cutString = str.substring(0, 350);
            lastMark = cutString.lastIndexOf("】") + 1;
        }
        return lastMark;
    }
    
    
    
    
}
