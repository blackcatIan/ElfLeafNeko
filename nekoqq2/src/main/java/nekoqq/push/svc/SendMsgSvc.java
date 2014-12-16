package nekoqq.push.svc;

import java.util.HashMap;
import java.util.Map;

import nekoqq.beans.LoginUserInfoBean;
import nekoqq.constant.WebQQUrlConst;
import nekoqq.helper.LoginHelper;
import nekoqq.push.helper.SendMsgSvcHelper;
import nekoqq.utils.encrypt.DeSimlar;
import nekoqq.utils.formatData.UtilsUnicode;
import nekoqq.utils.httpclient.UtilsHttpClient;

import org.apache.commons.lang.StringUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

@Service
public class SendMsgSvc {
    
    
    private Logger logger = Logger.getLogger(SendMsgSvc.class);
    
    /**
     * 发送群消息，发送的时候必须为unicode
     * @param gid 群id
     * @param msg
     */
    public void sendGroupMsg(Long gid,String msg) {
                                
        CloseableHttpClient httpclient = LoginHelper.httpclient;
        LoginUserInfoBean loginUserInfoBean = LoginHelper.loginUserInfoBean;
        
        Integer msgCount = SendMsgSvcHelper.msgCountMap.get(gid);
        if (msgCount == null || msgCount > 0) {
            //更新发送消息数量列表
            //Integer msgCount = SendMsvSvcHelper.msgCountMap.get(gid);
            if (msgCount == null) {
                //限制列表不存在则创建，默认每个群1分钟N个消息
                SendMsgSvcHelper.msgCountMap.put(gid, SendMsgSvcHelper.maxCount);
                SendMsgSvcHelper.countWarningMap.put(gid, true);
                SendMsgSvcHelper.gidSet.add(gid);

            } else {
                //更新
                msgCount = msgCount - 1;
                if (msgCount >= 0) {
                    SendMsgSvcHelper.msgCountMap.put(gid, msgCount);
                } else {
                    SendMsgSvcHelper.msgCountMap.put(gid, 0);
                }
            }
            
        } else {
            
            Boolean countWarningFlag = SendMsgSvcHelper.countWarningMap.get(gid);
            
            if (countWarningFlag) {
                SendMsgSvcHelper.countWarningMap.put(gid, false);
                msg = "本群访问过频繁请稍等几十秒再访问w(°Д°)w";
            } else {
                msg = StringUtils.EMPTY;
            }
            
            
        }
                                
        if (StringUtils.isNotEmpty(msg) ) {
            logger.info("发送消息内容:" + msg);
            
            msg = formatQQSendMsg(msg);
            Integer faceId = loginUserInfoBean.getFaceId();
            Long clientId = loginUserInfoBean.getClientid();
            Long msgId = loginUserInfoBean.getMsgId();
            String psessionid = loginUserInfoBean.getPsessionid();
            String key = "r";
            //注意这里文字参数必须传递给服务端多一个"\"
            String value = "{\"group_uin\":" + gid + ",\"content\":\"[\\\"" + msg 
                    + "\\\",[\\\"font\\\",{\\\"name\\\":\\\"宋体\\\",\\\"size\\\":10," 
                    + "\\\"style\\\":[0,0,0],\\\"color\\\":\\\"000000\\\"}]]\",\"face\":" 
                    + faceId + ",\"clientid\":" + clientId 
                    + ",\"msg_id\":" + msgId + ",\"psessionid\":\"" + psessionid + "\"}";
            
            System.out.println(value);
            //msgId要+1
            loginUserInfoBean.setMsgId(++msgId);
    
            Map<String, String> params = new HashMap<String, String>();
            params.put(key, value);
            params.put("clientid",clientId.toString());
            params.put("psessionid",psessionid);
            UtilsHttpClient.analogPostMethod(httpclient, WebQQUrlConst.SEND_QUN_MSG2, params , 5000);
        }//if (StringUtils.isNotEmpty(msg) )    

      
        
        
//        ThreadPost threadPost = new ThreadPost(httpclient, WebQQUrlConst.SEND_QUN_MSG2,params);
//        threadPost.start();
//        try {
//            threadPost.join();
//        } catch (Exception e) {
//            logger.error("send msg",e);
//        }
        
//        HttpResponseData responseData = threadPost.getResponseData();
//        return responseData;
    }
    
    
    

    
    /**
     * 格式化要发送的消息
     */
    private String formatQQSendMsg(String msg) {
        //混淆文本
        //msg = DeSimlar.makeDeSimlar(msg);
        
        msg = msg.replaceAll("\\\\", "\\\\\\\\\\\\\\\\");
        msg = msg.replaceAll("\\\\\\\\\\\\\\\\n", "\\\\\\\\n");
        msg = msg.replaceAll("\"", "\\\\\\\"");

        return msg;
    }
    
    
    
    
}
