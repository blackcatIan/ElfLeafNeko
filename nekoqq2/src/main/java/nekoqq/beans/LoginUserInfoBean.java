package nekoqq.beans;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.sf.json.JSONObject;

import org.apache.commons.lang.math.RandomUtils;

/**
 * 登陆者信息
 */
public class LoginUserInfoBean {
    /**qq号**/
    private Integer qq;
    
    /**qq密码**/
    private String pwd;
    
    /**登录获得的check_sig的url,第一次登陆验证后返回值中提取的地址**/
    private String check_sigUrl;
    
    /**检查是否需要验证码后返回的验证信息**/
    private String checkInfo;
    
    /**默认验证码(有该值时无需输入验证码登录)**/
    private String defaultVerifyCode;
    
    /**十六进制的QQ号**/
    private String hexQQNum;
    
    /**经过WEBQQ专用的MD5加密后的密码**/
    private String encryptPwd;
    
    /**第一次登陆登录验证后，完成check_sig后的webqq的令牌码**/
    private String ptwebqq;
    
    /**登陆者全局唯一标示**/
    private Long clientid;
    
    /**第二次登录验证通过后得到的令牌ID**/
    private String psessionid;
    
    /**第二次登录通过后得到的验证令牌**/
    private String vfwebqq;
    
    /**群列表，k=qq服务端全局gid,v=相关信息的JSONObject **/
    private Map<Long,JSONObject> groupMap = new HashMap<Long, JSONObject>(); 
    
    /**群纯gid的列表**/
    private Set<Long> groupList = new HashSet<Long>();
    
    /**post群消息时候的参数(初始化时随机生成，每次发送+1)**/
    private Long msgId;
    
    /**发送消息时的参数，头像id**/
    private Integer faceId;
    
    /**
     * constructor
     * @param qq
     * @param pwd
     */
    public LoginUserInfoBean(Integer qq,String pwd) {
        this.qq = qq;
        this.pwd = pwd;
        Double rnd = RandomUtils.nextDouble()*10000;
        msgId = rnd.longValue()*10000;
    }
    
    public Integer getQq() {
        return qq;
    }

    public void setQq(Integer qq) {
        this.qq = qq;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getCheck_sigUrl() {
        return check_sigUrl;
    }

    public void setCheck_sigUrl(String check_sigUrl) {
        this.check_sigUrl = check_sigUrl;
    }

    public String getCheckInfo() {
        return checkInfo;
    }

    public void setCheckInfo(String checkInfo) {
        this.checkInfo = checkInfo;
    }

    public String getDefaultVerifyCode() {
        return defaultVerifyCode;
    }

    public void setDefaultVerifyCode(String defaultVerifyCode) {
        this.defaultVerifyCode = defaultVerifyCode;
    }

    public String getHexQQNum() {
        return hexQQNum;
    }

    public void setHexQQNum(String hexQQNum) {
        this.hexQQNum = hexQQNum;
    }

    public String getEncryptPwd() {
        return encryptPwd;
    }

    public void setEncryptPwd(String encryptPwd) {
        this.encryptPwd = encryptPwd;
    }

    public String getPtwebqq() {
        return ptwebqq;
    }

    public void setPtwebqq(String ptwebqq) {
        this.ptwebqq = ptwebqq;
    }

    public String getPsessionid() {
        return psessionid;
    }

    public void setPsessionid(String psessionid) {
        this.psessionid = psessionid;
    }

    public Long getClientid() {
        return clientid;
    }

    public void setClientid(Long clientid) {
        this.clientid = clientid;
    }

    public String getVfwebqq() {
        return vfwebqq;
    }

    public void setVfwebqq(String vfwebqq) {
        this.vfwebqq = vfwebqq;
    }

    public Map<Long, JSONObject> getGroupMap() {
        return groupMap;
    }

    public void setGroupMap(Map<Long, JSONObject> groupMap) {
        this.groupMap = groupMap;
    }

    public Set<Long> getGroupList() {
        return groupList;
    }

    public void setGroupList(Set<Long> groupList) {
        this.groupList = groupList;
    }

    public Long getMsgId() {
        return msgId;
    }

    public void setMsgId(Long msgId) {
        this.msgId = msgId;
    }

    public Integer getFaceId() {
        return faceId;
    }

    public void setFaceId(Integer faceId) {
        this.faceId = faceId;
    }

 
   


}
