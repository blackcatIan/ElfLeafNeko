package nekoqq.pushDecide.svc;

import java.net.URLDecoder;
import java.util.Random;

import nekoqq.beans.ReceiveMsgBean;
import nekoqq.beans.SendMsgBean;
import nekoqq.beans.SendMsgList;
import nekoqq.constant.MsgCommandType;
import nekoqq.push.helper.SendMsgSvcHelper;
import nekoqq.task.eve.svc.EveSearchPriceSvc;
import nekoqq.task.mabinogi.cache.MabiSmuggleCache;
import nekoqq.task.mabinogi.svc.MabiSmuggleSvc;
import nekoqq.task.mabinogi.svc.MabiTaskSvc;
import nekoqq.utils.file.UtilsFile;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 构建回答消息的决策
 */
@Service
public class DecideMsgSvc {
    
    private static Logger logger = Logger.getLogger(DecideMsgSvc.class);

    @Autowired
    private EveSearchPriceSvc eveSearchPriceSvc;
    
    @Autowired
    private MabiTaskSvc mabiTaskSvc;
    
    @Autowired
    private MabiSmuggleSvc mabiSmuggleSvc;

    public void decideMsg(ReceiveMsgBean receiveMsgBean) {

        String commandType = receiveMsgBean.getMsgCommandType();

        /*------eve market-------*/
        if (commandType.equals(MsgCommandType.EVE_PRICE)) {
            eveSearchPriceSvc.search(receiveMsgBean);
        }
        
        /*------mabinogi task-------*/
        if (commandType.equals(MsgCommandType.MABINOGI_TASK)) {
            try {
                mabiTaskSvc.getTask(receiveMsgBean);
            } catch (Exception e) {
                SendMsgList msgList = SendMsgList.getInstance();
                SendMsgBean errorMsgBean = new SendMsgBean();
                errorMsgBean.setGid(receiveMsgBean.getGid());
                errorMsgBean.setSendMsg("访问数据失败，请过1分钟后再重试获取数据");
                msgList.add(errorMsgBean);
            }
        }
        
        /*---------mabinogi trade--------*/
        if (commandType.equals(MsgCommandType.MABINOGI_TRADE)) {
            mabiSmuggleSvc.getSmuggleInfo(receiveMsgBean);
        }
            
        
        /*------help-------*/
        if (commandType.equals(MsgCommandType.NEKO_HELP)) {
            //输出帮助信息
            String path = Thread.currentThread().getContextClassLoader()
                                .getResource("").getPath() + "/HELP/HELP";
            try {
                path = URLDecoder.decode(path, "utf-8");
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
            
            String helpMsg = UtilsFile.readTxtFile(path);
            helpMsg = helpMsg.replaceAll("\r\n", "\\\\n");
            
            SendMsgList msgList = SendMsgList.getInstance();
            SendMsgBean helpMsgBean = new SendMsgBean();
            helpMsgBean.setGid(receiveMsgBean.getGid());
            helpMsgBean.setSendMsg(helpMsg);
            msgList.add(helpMsgBean);
        }
        
        /*-----common msg---------*/
        //对普通消息，触发小几率重复
        if (commandType.equals(MsgCommandType.COMMON_MSG)) {
            String receiveMsg = receiveMsgBean.getReceiveMsg();
            if (StringUtils.isNotBlank(receiveMsg)) {
                double rnd = RandomUtils.nextDouble();
                if (rnd < 0.05) {
                    SendMsgList msgList = SendMsgList.getInstance();
                    SendMsgBean helpMsgBean = new SendMsgBean();
                    helpMsgBean.setGid(receiveMsgBean.getGid());
                    helpMsgBean.setSendMsg(receiveMsg);
                    msgList.add(helpMsgBean);
                    
                    //免费送一条消息限制(因为不是指令消息)
                    Integer count = SendMsgSvcHelper.msgCountMap.get(receiveMsgBean.getGid());
                    count = count + 1;
                    SendMsgSvcHelper.msgCountMap.put(receiveMsgBean.getGid(), count);
                }
            }//if (StringUtils.isNotBlank(receiveMsg)) 
        }//if (commandType.equals(MsgCommandType.COMMON_MSG))
        
        
    }
    
    @Test
    public void test() {
        System.out.println(RandomUtils.nextDouble());
    }
}
