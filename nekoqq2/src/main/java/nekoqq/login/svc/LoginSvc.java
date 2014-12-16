package nekoqq.login.svc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.net.URLDecoder;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import nekoqq.beans.LoginUserInfoBean;
import nekoqq.constant.WebQQUrlConst;
import nekoqq.helper.LoginHelper;
import nekoqq.utils.formatData.Uint32;
import nekoqq.utils.httpclient.UtilsHttpClient;
import nekoqq.utils.httpclient.beans.HttpResponseData;
import net.sf.json.JSONObject;

import org.apache.commons.lang.math.RandomUtils;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

@Service
public class LoginSvc {
    
    
    protected static Logger logger = Logger.getLogger(LoginSvc.class);
    
    /*===============public=====================*/
    /**
     * main service
     * 登录流程的主要服务程序入口
     * @param httpclient
     * @param loginUserInfoBean
     * @return 登录成功返回TRUE
     */
    public Boolean login() {
        
        CloseableHttpClient httpclient = LoginHelper.httpclient;
        LoginUserInfoBean loginUserInfoBean = LoginHelper.loginUserInfoBean;

        //STEP获得十六进制的QQ号
        catchHexQQNum(loginUserInfoBean);
        
        //STEP加密登录密码
        encryptPwd(loginUserInfoBean);
        
        //STEP进行第一次登陆验证
        firstLogin(httpclient, loginUserInfoBean);
        
        //STEP进行check_sig,若缺少该步骤则无法拿到cookies的pskey
        List<Cookie> cookies = checksig(httpclient, loginUserInfoBean);
        
        //STEP 取得最新的vfwebqq，无该步骤无法正确取得联系人列表，无法正确发出消息
        getVfwebqq(httpclient, loginUserInfoBean,cookies);
        
        //STEP进行第二次登录验证
        Boolean loginResult = secondLogin(httpclient, loginUserInfoBean, cookies);
        
        //至此已登录成功
        return loginResult;
    }
    
    
    /**
     * 取得cookie中的Verifysession
     */
    public void getVerifysessionCookie(CloseableHttpClient httpclient) {
        logger.info("获取cookies中的Verifysession...");
        
        UtilsHttpClient.analogGetMethod(httpclient, WebQQUrlConst.PIC_VERIFYSESSION_URL, true);  
    }
    
    
    /**
     * 检查登录是否需要验证码
     * true 需要，false不需要
     * @param httpclient
     * @param loginUserInfoBean
     * @return
     */
    public Boolean checkRequiredVerify()
    {
        logger.info("检测是否需要验证码...");
        
        String checkQQUrl = WebQQUrlConst.CHECK_QQ_URL.replaceAll("#qq#", LoginHelper.loginUserInfoBean.getQq().toString());
        HttpResponseData responseData = UtilsHttpClient.analogGetMethod(LoginHelper.httpclient, checkQQUrl, true);
     
        String checkInfo = responseData.getContent();
        //若匹配这个正则则表示需要验证码
        String regx = "ptui_checkVC\\(\\'1\\'[ -~]+";
        
        Boolean isRequiredVerify = checkInfo.matches(regx);
        
        LoginHelper.loginUserInfoBean.setCheckInfo(checkInfo);
        
        return isRequiredVerify;
    }
    
    /**
     * 获得默认验证码
     * @param loginUserInfoBean 登陆者信息BEAN
     */
    public void catchDefaultVerifyCode() {
        String currentCheckInfo = LoginHelper.loginUserInfoBean.getCheckInfo();
        String checkInfoArray[] = currentCheckInfo.split(",");

        //默认验证码
        String defaultVerifyCode = checkInfoArray[1].substring(1, checkInfoArray[1].length() - 1);
        
        //存入登录者信息中
        LoginHelper.loginUserInfoBean.setDefaultVerifyCode(defaultVerifyCode);
    }
    
    /**
     * 将验证码保存到本地,用于稍后登陆页显示出验证码让用户输入
     */
    public void saveVerifyImage() {
        
        CloseableHttpClient httpclient = LoginHelper.httpclient;
        LoginUserInfoBean loginUserInfoBean = LoginHelper.loginUserInfoBean;
        
        
        //获得验证码图片
        String imageUrl = WebQQUrlConst.GET_IMAGE_URL.replaceAll("#qq#", loginUserInfoBean.getQq().toString());
        
        HttpResponseData responseData = UtilsHttpClient.analogGetMethod(httpclient, imageUrl, true);
        
        FileOutputStream fos = null;
        
        try {

            String path = Thread.currentThread().getContextClassLoader()
                        .getResource("").getPath() + "/verifycode.jpg";
            path = URLDecoder.decode(path, "utf-8");       
            File file = new File(path);
            fos = new FileOutputStream(file);
            fos.write(responseData.getContentByte());
            fos.flush();
        } catch (Exception e) {
            logger.error("loginEnry service",e);
        } finally {
            try {
                fos.close();
            } catch (Exception e2) {
                logger.error("loginEnry service",e2);
            }
        }
    }
    
    
    
    /*===============private=====================*/
    
    private void getVfwebqq(CloseableHttpClient httpclient,LoginUserInfoBean loginUserInfoBean,List<Cookie> cookies) {
        //从cookie获取必要参数
        String ptwebqqRegx = "\\[name: ptwebqq\\]\\[value: [0-9a-z]+";
        Pattern p = Pattern.compile(ptwebqqRegx);
        Matcher matcher = p.matcher(cookies.toString());
        matcher.find();
        //ptwebqq: 42f1cc2b7ac4238ff2845ae7d32ca328847cc79ea27ee95995a1c658cfa95981
        String ptwebqqStr = matcher.group(0);
        String ptwebqq = ptwebqqStr.split("value: ")[1];
        //存入登录者信息
        loginUserInfoBean.setPtwebqq(ptwebqq);
        
        //生成clientid
        String clientid = GetClientID().toString();
        loginUserInfoBean.setClientid(Long.valueOf(clientid));
        
        Double t = 1000000000000.0;
        Double rnd = t*RandomUtils.nextDouble();
        Long lt = rnd.longValue();

        String url = WebQQUrlConst.GET_VFWEBQQ.replaceAll("#ptwebqq#", ptwebqq)
                .replaceAll("#clientid#", clientid).replaceAll("#t#", lt.toString());
        
        HttpResponseData responseData = UtilsHttpClient.analogGetMethod(httpclient, url, true);
        JSONObject respJson = JSONObject.fromObject(responseData.getContent());
        JSONObject result = JSONObject.fromObject(respJson.get("result"));
        String vfwebqq = result.getString("vfwebqq");
        loginUserInfoBean.setVfwebqq(vfwebqq);
    }
    
    
    
    /**
     * 获得十六进制的QQ号
     * @param loginUserInfoBean 登陆者信息BEAN
     */
    private void catchHexQQNum(LoginUserInfoBean loginUserInfoBean) {
        String currentCheckInfo = loginUserInfoBean.getCheckInfo();
        String checkInfoArray[] = currentCheckInfo.split(",");
        String hexQQNum = checkInfoArray[2].substring(1, checkInfoArray[2].length() - 1);
        
        //存入登录者信息中
        loginUserInfoBean.setHexQQNum(hexQQNum);
    }
    
    /**
     * 加密登录密码
     * @param loginUserInfoBean 登陆者信息BEAN
     */
    private void encryptPwd(LoginUserInfoBean loginUserInfoBean) {

        //加载脚本读取器
        ScriptEngineManager m = new ScriptEngineManager();
        //设置为JS脚本
        ScriptEngine jsScriptEngine = m.getEngineByName("javascript");
        
        //打开JS文件
        FileReader reader = null;
        File file = null;
        try {
            // 用官网的JS计算加密密码
            String path = Thread.currentThread().getContextClassLoader().getResource("").getPath() + "/QQMD5/MD5.JS";
            path = URLDecoder.decode(path, "utf-8");
            file = new File(path);
            reader = new FileReader(file);
            //加载MD5 JS文件
            jsScriptEngine.eval(reader);
            //执行JS文件中的函数
            Object t = jsScriptEngine.eval("QXWEncodePwd(\"" 
                    + loginUserInfoBean.getHexQQNum() 
                    + "\",\"" + loginUserInfoBean.getPwd() + "\",\"" 
                    + loginUserInfoBean.getDefaultVerifyCode() + "\");");
            
            // 加密结果保存到登陆者信息中
            loginUserInfoBean.setEncryptPwd(t.toString());
            
        } catch (Exception e) {
            logger.error("读取js脚本出错", e);
        } finally {
            try {
                reader.close();
            } catch (Exception e2) {
                logger.error("释放io文件出错", e2);
            }
        }//{}end of finally  
    }
    


    /**
     * 第一次登陆验证
     * @param httpclient httpclient实例
     * @param loginUserInfoBean 登陆者信息BEAN
     */
    private void firstLogin(CloseableHttpClient httpclient, LoginUserInfoBean loginUserInfoBean) {
        logger.info("第一次登录验证...");
        
        //本次登录的用户第一次验证地址
        String firstLoginUrl = makeFirstLoginUrl(loginUserInfoBean);
        
        HttpResponseData responseData = UtilsHttpClient.analogGetMethod(httpclient, firstLoginUrl,true);
        
        //截获check_sig的url
        String content = responseData.getContent();
        String catchUrl = content.split(",")[2];
        //存入登陆者信息中
        loginUserInfoBean.setCheck_sigUrl(catchUrl.substring(1,catchUrl.length()-1));
    }
    

    /**
     * 进行check_sig
     * @param httpclient httpclient实例
     * @param loginUserInfoBean 登陆者信息BEAN
     * @return 返回cookies列表
     */
    private List<Cookie> checksig(CloseableHttpClient httpclient, LoginUserInfoBean loginUserInfoBean) {
        logger.info("进行check_sig...");
        
        String check_sigUrl = loginUserInfoBean.getCheck_sigUrl();
        
        HttpResponseData responseData = UtilsHttpClient.analogGetMethod(httpclient, check_sigUrl, false);
        
        return responseData.getCookies();
    }
    
    


    /**
     * 第二次登陆验证
     * @param httpclient httpclient实例
     * @param loginUserInfoBean 登陆者信息BEAN
     * @param cookies 上次操作取得的cookies
     * @return 登录成功返回true
     */
    private Boolean secondLogin(CloseableHttpClient httpclient, LoginUserInfoBean loginUserInfoBean, List<Cookie> cookies) {
        logger.info("第二次登录验证...");
        
        //PART 第二次登录
        String secondLoginUrl = WebQQUrlConst.SECOND_LOGIN_URL;

        String ptwebqq = loginUserInfoBean.getPtwebqq();
        
        //重新取出clientid
        String clientid = loginUserInfoBean.getClientid().toString();

        //POST的键值参数
        String key = "r";
        String value = "{\"ptwebqq\":\"" + ptwebqq + "\",\"clientid\":" + clientid + ",\"psessionid\":\"\",\"status\":\"online\"}";
        Map<String,String> params = new HashMap<String, String>();
        params.put(key, value);
        
        //进行登录验证..
        HttpResponseData responseData = UtilsHttpClient.analogPostMethod(httpclient, secondLoginUrl, params, 5000);
        
        JSONObject jsonObj = JSONObject.fromObject(responseData.getContent());

        //保存返回结果中的psessionid,vfwebqq
        JSONObject jsonResult = JSONObject.fromObject(jsonObj.get("result"));
        String psessionid = jsonResult.get("psessionid").toString();

        //存入登录者信息
        loginUserInfoBean.setPsessionid(psessionid); 

        
        Integer retcode = Integer.valueOf(jsonObj.get("retcode").toString());
        return retcode == 0 ? true : false;
    }
    

    
    
    /**
     * 生成本次用户的第一次验证登录的URL
     * @param loginUserInfoBean 登陆者信息BEAN
     * @return
     */
    private String makeFirstLoginUrl(LoginUserInfoBean loginUserInfoBean) {
        return WebQQUrlConst.FIRST_LOGIN_URL
                .replaceAll("#qq#", loginUserInfoBean.getQq().toString())
                .replaceAll("#encryptPwd#", loginUserInfoBean.getEncryptPwd())
                .replaceAll("#defaultVerifyCode#", loginUserInfoBean.getDefaultVerifyCode().toUpperCase());
    }
    
    /**
     * 生成全局唯一的标示ID
     * @return
     */
    private static Uint32 GetClientID()
    {
        Calendar c = Calendar.getInstance();
        UUID.randomUUID().toString();
        Long seed1 = new Double(new Random(UUID.randomUUID().toString().hashCode()).nextDouble()*99).longValue();
        Long seed2 = new Long(c.getTime().getTime()) / 10000000;

        Long newLong = new Long(Long.valueOf(seed1.toString() + seed2.toString()));
        Uint32 clientid=new Uint32(newLong);
        return clientid;
    }

}
