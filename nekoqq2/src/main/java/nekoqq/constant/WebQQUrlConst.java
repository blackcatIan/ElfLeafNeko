package nekoqq.constant;

/**
 * 登录相关的url常量
 */
public class WebQQUrlConst {
    /**用来 检查 验证码  "是否需要验证"的地址，需要replaceAll添加QQ号**/
    public final static String CHECK_QQ_URL = "https://ssl.ptlogin2.qq.com/check?appid=501004106" 
                                        + "&u1=http%3A%2F%2Fw.qq.com%2Fproxy.html&r=0.9877801523543894&uin=#qq#";
    
    /**获得验证码的url,需要用replaceAll添加QQ号**/
    public final static String GET_IMAGE_URL = "https://ssl.captcha.qq.com/getimage?" 
                                        + "aid=501004106&r=0.2495213069487363&uin=#qq#";

    /**提取Verifysession的url**/
    public final static String PIC_VERIFYSESSION_URL = "http://captcha.qq.com/getimage?0.7712672651606319";

    /**第一次登录验证URL**/
    public final static String FIRST_LOGIN_URL = "https://ssl.ptlogin2.qq.com/login?u=" + "#qq#" + "&p=" + "#encryptPwd#" 
                                    + "&verifycode="+ "#defaultVerifyCode#"
                                    + "&webqq_type=10&remember_uin=1&login2qq=1&aid=501004106"
                                    + "&u1=http%3A%2F%2Fw.qq.com%2Fproxy.html%3Flogin2qq%3D1%26webqq_type%3D10&h=1"
                                    + "&ptredirect=0&ptlang=2052&daid=164&from_ui=1&pttype=1&dumy=&fp=loginerroralert" 
                                    + "&action=0-50-3411118&mibao_css=m_webqq&t=1&g=1";
    
    /**获取最新的vfwebqq**/
    public final static String GET_VFWEBQQ = "http://s.web2.qq.com/api/getvfwebqq?ptwebqq=#ptwebqq#&clientid=#clientid#&psessionid=&t=#t#";
    
    /**第二次登录验证的URL**/
    public final static String SECOND_LOGIN_URL = "http://d.web2.qq.com/channel/login2";
    
    /**推送请求地址**/
    public final static String POLL_URL = "http://d.web2.qq.com/channel/poll2";
    
    /**获取群列表URL**/
    public final static String GROUP_NAME_LIST_URL= "http://s.web2.qq.com/api/get_group_name_list_mask2";
    
    /**获取自身信息URL**/
    public final static String GET_SELF_INFO_ULR = "http://s.web2.qq.com/api/get_self_info2?t=#t#";
    
    /**发送群消息url**/
    public final static String SEND_QUN_MSG2 = "http://d.web2.qq.com/channel/send_qun_msg2";
    
    
}










