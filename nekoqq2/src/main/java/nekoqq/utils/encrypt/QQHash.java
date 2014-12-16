package nekoqq.utils.encrypt;

import nekoqq.beans.LoginUserInfoBean;


/**
 * 获取联系人列表时计算hash
 * 原JS相关地址请参考
 * http://0.web.qstatic.com/webqqpic/pubapps/0/50/eqq.all.js?t=20140514001
 */
public class QQHash {


    /**
     * 
     * @param qq 十进制qq号
     * @return
     */
    public static String getHashcode(LoginUserInfoBean loginUserInfoBean) {
        Integer qq = loginUserInfoBean.getQq();
        String ptwebqq = loginUserInfoBean.getPtwebqq();
        String a = ptwebqq + "password error";
        String i = "";
        char E[];
        while(true) {
            if (i.length() < a.length()) {
                i += qq;
                if (i.length() == a.length()) {
                    break;
                }
            } else {
                i = i.substring(0, a.length());
                break;
            }//{}end of if (i.length() < a.length())
        }//end of while
        
        E = new char[i.length()];
        for (int c = 0; c < i.length(); c++) {
            E[c]= (char) (i.charAt(c) ^ a.charAt(c));
        }
        
        String d[] = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F"};
        i = "";
        
        for (int c = 0; c < E.length; c++) {
            i += d[(E[c] >> 4 & 15)];
            i += d[E[c] & 15];
        }
        
        return i;
    }
}
