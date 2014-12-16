package nekoqq.pull.svc;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nekoqq.beans.ReceiveMsgBean;
import nekoqq.beans.ReceiveMsgList;
import nekoqq.constant.CommandRegx;
import nekoqq.constant.MsgCommandType;
import nekoqq.helper.StatusHelper;
import nekoqq.helper.ThreadHttpClientHelper;
import nekoqq.helper.ThreadManagerHelper;
import nekoqq.utils.formatData.UtilsUnicode;
import nekoqq.utils.httpclient.beans.HttpResponseData;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 * 接收到消息，进行预处理，并加入消息列队
 */
@Service()
public class ReceiveMsgSvc {
    
    private static Logger logger = Logger.getLogger(ReceiveMsgSvc.class);
    

    /**
     * @param responseData
     */
    public void receiveMsg(HttpResponseData responseData) {
        JSONObject respJson;
        JSONArray resultArray;
        JSONObject result;
        JSONObject jsonValue;
        JSONArray conetentArray = null;
        Long gid = null;
        
        
        respJson = JSONObject.fromObject(responseData.getContent());
        resultArray = JSONArray.fromObject(respJson.get("result")); 
        
        
        
        for(Object peekResult : resultArray) {
            result = JSONObject.fromObject(peekResult);
            
            try {
                
                jsonValue = result.getJSONObject("value"); 
                //消息来自 gid
                gid = jsonValue.getLong("from_uin");
                
                conetentArray = JSONArray.fromObject(jsonValue.get("content"));
            } catch (Exception e) {
                e.printStackTrace();
                //发生数据异常，可能掉线了
                StatusHelper.isLogin = false;
                ThreadManagerHelper.StopAll();
            }

            
            
            String msg = StringUtils.EMPTY;
            if (conetentArray != null) {
                String unicodeMsg = conetentArray.get(conetentArray.size() - 1).toString();
                //消息内容msg
                msg = UtilsUnicode.unicode2str(unicodeMsg);
                logger.info("收到消息:" + UtilsUnicode.unicode2str(msg));
            }
           

            //消息结构体
            ReceiveMsgBean msgBean = new ReceiveMsgBean();
            msgBean.setReceiveMsg(msg);
            msgBean.setGid(gid);
            
            //判断消息
            //eve
            String eveRegx = CommandRegx.evePriceRegx;
            String eveCompatible = CommandRegx.evePriceCompatibleRegx;
            //mabinogi
            String lqPriceRegx = CommandRegx.lqPriceRegx;
            String lqTaskRegx = CommandRegx.lqTaskRegx;
            String lqTradeRegx = CommandRegx.lqTradeRegx;
            //help帮助信息
            String nekoHelpRegx = CommandRegx.nekoHelpRegx;
            
            /*---------eve-----------*/
            //判断是不是查询eve
            Pattern p = Pattern.compile(eveRegx);
            Matcher msgMatcher = p.matcher(msg);
            if (msgMatcher.matches()) {
                msgBean.setMsgCommandType(MsgCommandType.EVE_PRICE);
            }
            
            //判断是不是查询eve
            p = Pattern.compile(eveCompatible);
            msgMatcher = p.matcher(msg);
            if (msgMatcher.matches()) {
                msgBean.setMsgCommandType(MsgCommandType.EVE_PRICE);
            }
            
            
            /*---------mabinogi-------*/
            //判断是否是查询洛奇物价
            p = Pattern.compile(lqPriceRegx);
            msgMatcher = p.matcher(msg);
            if (msgMatcher.matches()) {
                msgBean.setMsgCommandType(MsgCommandType.MABINOGI_PRICE);
            }
            
            //判断是否是查询洛奇每日任务
            p = Pattern.compile(lqTaskRegx);
            msgMatcher = p.matcher(msg);
            if (msgMatcher.matches()) {
                msgBean.setMsgCommandType(MsgCommandType.MABINOGI_TASK);
            }
            
            //判断是否是查询洛奇走私看板
            p = Pattern.compile(lqTradeRegx);
            msgMatcher = p.matcher(msg);
            if (msgMatcher.matches()) {
                msgBean.setMsgCommandType(MsgCommandType.MABINOGI_TRADE);
            }
            
            
            /*---------help-------*/
            //帮助信息
            p = Pattern.compile(nekoHelpRegx);
            msgMatcher = p.matcher(msg);
            if (msgMatcher.matches()) {
                msgBean.setMsgCommandType(MsgCommandType.NEKO_HELP);
            }
            
            //default
            if (msgBean.getMsgCommandType() == null) {
                msgBean.setMsgCommandType(MsgCommandType.COMMON_MSG);
            }
            
            
            //STEP 加入消息列队等待处理
            ReceiveMsgList msgList = ReceiveMsgList.getInstance();
            //当消息列队没有锁定时(默认锁定，在登录后苏醒完毕后解除锁定)
            if (msgList.getMsgListLock() == false) {
                msgList.add(msgBean);
            }
            
            
        }//for(Object peekResult : resultArray) {
        
        
        
        
    }
        

}
