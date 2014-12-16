package nekoqq.beans;

import java.util.Vector;

import nekoqq.constant.MsgCommandType;

public class SendMsgList {
    private static SendMsgList msgList = null;

    // 最大消息存储量(消息反应速度0.2~1秒)
    private final static Integer maxMsgList = 100;

    /**
     * 接收消息列队锁，默认开启，当为true 锁定不允许接收消息（请在登录后并苏醒完成后改为false）
     * 当为false时候，解除锁定recevieMsgSvc可以对他添加MsgBean
     */
    private Boolean msgListLock = true;

    /** 消息列队 **/
    private static Vector<SendMsgBean> msgListVector = new Vector<SendMsgBean>();

    private SendMsgList() {
    }

    // 单例
    public synchronized static SendMsgList getInstance() {
        if (msgList == null) {
            msgList = new SendMsgList();
        }

        return msgList;
    }

    /**
     * 加入消息列队
     */
    public synchronized void add(SendMsgBean msgBean) {
        if (msgListVector.size() < maxMsgList) {
            msgListVector.add(msgBean);
        }

        if (msgListVector.size() >= maxMsgList && msgListVector.size() < 2 * maxMsgList) {
             msgBean.setSendMsg("忙ing,请稍后@o@...");
            //msgBean.setMsgCommandType(MsgCommandType.BUSY);
            msgListVector.add(msgBean);
        }
    }

    /**
     * 提取一条消息进行处理
     */
    public synchronized SendMsgBean peekMsgBean() {
        SendMsgBean msgBean = msgListVector.size() > 0 ? msgListVector.get(0) : null;
        msgListVector.remove(msgBean);
        return msgBean;
    }

    /**
     * 插入一条消息
     * 
     * @param msgBean
     * @param index
     *            列队中的序号
     */
    public synchronized void insetMsgBean(SendMsgBean msgBean, int index) {
        msgListVector.insertElementAt(msgBean, index);
    }

    public Boolean getMsgListLock() {
        return msgListLock;
    }

    public void setMsgListLock(Boolean msgListLock) {
        this.msgListLock = msgListLock;
    }
}
