package nekoqq.beans;

public class SendMsgBean {
//    /** 机器收到的消息 非unicode **/
//    private String receiveMsg;

    private Long gid;

    /**机器准备发送的消息，非unicode**/
    private String sendMsg;
//    /** 附带指令类型 **/
//    private String msgCommandType;

//    public String getReceiveMsg() {
//        return receiveMsg;
//    }
//
//    public void setReceiveMsg(String receiveMsg) {
//        this.receiveMsg = receiveMsg;
//    }

    public Long getGid() {
        return gid;
    }

    public void setGid(Long gid) {
        this.gid = gid;
    }

     public String getSendMsg() {
     return sendMsg;
     }
     public void setSendMsg(String sendMsg) {
     this.sendMsg = sendMsg;
     }
//    public String getMsgCommandType() {
//        return msgCommandType;
//    }
//
//    public void setMsgCommandType(String msgCommandType) {
//        this.msgCommandType = msgCommandType;
//    }
}
