package nekoqq.login.svc;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import nekoqq.beans.LoginUserInfoBean;
import nekoqq.constant.WebQQUrlConst;
import nekoqq.helper.LoginHelper;
import nekoqq.utils.encrypt.QQHash;
import nekoqq.utils.httpclient.UtilsHttpClient;
import nekoqq.utils.httpclient.beans.HttpResponseData;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.math.RandomUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

/**
 * 获得联系人列表
 */
@Service
public class GetContactsSvc {
    
    protected static Logger logger = Logger.getLogger(GetContactsSvc.class);
    
    /**
     * 获取个人信息
     * @param httpclient
     * @param loginUserInfoBean
     */
    public void getSelfInfo() {
        CloseableHttpClient httpclient = LoginHelper.httpclient;
        LoginUserInfoBean loginUserInfoBean = LoginHelper.loginUserInfoBean;
        
        //1000000000000
        Double t = 1000000000000.0;
        Double rnd = t*RandomUtils.nextDouble();
        Long lt = rnd.longValue();
        String url = WebQQUrlConst.GET_SELF_INFO_ULR.replaceAll("#t#", lt.toString());
        
        HttpResponseData responseData = UtilsHttpClient.analogGetMethod(httpclient,url, true);

        JSONObject respJsonObject = JSONObject.fromObject(responseData.getContent());
        String resultStr = respJsonObject.getString("result");
        JSONObject result = JSONObject.fromObject(resultStr);
        Integer faceId = Integer.valueOf(result.get("face").toString());
        loginUserInfoBean.setFaceId(faceId);
    }
    
    public void getFriendList() {
        
    }
    

    /**
     * 获得群列表
     * 需要计算hash码，入参为qq(正常十进制)，ptwebb
     * @param httpclient
     * @param loginUserInfoBean
     */
    public void getGroupList() {
        CloseableHttpClient httpclient = LoginHelper.httpclient;
        LoginUserInfoBean loginUserInfoBean = LoginHelper.loginUserInfoBean;
        
        
        String vfwebqq = loginUserInfoBean.getVfwebqq();
        //计算hash
        String hash = QQHash.getHashcode(loginUserInfoBean);
        
        //POST传递参数值
        String key = "r";
        String value = "{\"vfwebqq\":\"" + vfwebqq + "\",\"hash\":\"" + hash + "\"}";
        
        Map<String, String> params = new HashMap<String, String>();
        params.put(key, value);
        
        //POST请求群列表
        HttpResponseData responseData = UtilsHttpClient
                .analogPostMethod(httpclient, WebQQUrlConst.GROUP_NAME_LIST_URL, params , 5000);
        
        JSONObject respJsonObj = JSONObject.fromObject(responseData.getContent());
        
        
        if (Integer.valueOf(respJsonObj.getString("retcode")) == 0) {
            //获取成功
            //存入用户信息中
            String result = respJsonObj.getString("result");
            JSONObject resultObj = JSONObject.fromObject(result);
            String gnamelistStr = resultObj.getString("gnamelist");
   
            JSONArray gnamelist = JSONArray.fromObject(gnamelistStr);
            
            //获得联系人信息“引用”
            Map<Long,JSONObject> groupMap = loginUserInfoBean.getGroupMap();
            Set<Long> groupList = loginUserInfoBean.getGroupList();
            
            //将获得的列表存入联系人“引用”中
            for(Object peekObj : gnamelist) {
                JSONObject peekJsonObj = JSONObject.fromObject(peekObj);
                Long gid = Long.valueOf(peekJsonObj.getString("gid"));
                groupMap.put(gid, peekJsonObj);
                groupList.add(gid);
            }

        } else {
            //获取失败
            logger.error("获取群列表失败（┬＿┬）");
        }
    }
}
