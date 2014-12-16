package nekoqq.utils.encrypt;

import java.util.Random;

import org.junit.Test;

/**
 * 反对话相似度查找(大致腾讯用的最短边距算法)
 */
public class DeSimlar {
    private final static String DESIMLAR_SAMPLE = " `-+、·¨′∶∵∴*┈┉ ";
    
    
    private static String getDeSimlarStr() {
        int length = DESIMLAR_SAMPLE.length();
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < 16; i++) {
            int rnd = random.nextInt(length);
            sb.append(DESIMLAR_SAMPLE.charAt(rnd));
        }
        return sb.toString();
        //DESIMLAR_SAMPLE.charAt(index)
    }
    
    public static String makeDeSimlar(String str) {
        String strArr[] = str.split("\\\\n");
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < strArr.length; i++) {
            sb.append(strArr[i]);
            sb.append("\\n");
            sb.append(getDeSimlarStr());
            sb.append("\\n");
        }
        return sb.toString();
    }
    
    @Test
    public void test(){
        String str = makeDeSimlar("Eve-market市场查询:花\n    \n物品名称: 女式'街美'背心(花纹棕)\nsell: 864,654,530   .00 ISK\nbuy: 464,654,528   .00 ISK\n更新时间: 2014-08-16 01:16:52\n【----------------】\n");
        System.out.println(str);
        
        String str1 = "abc\\ndef\\nfsf";
        System.out.println(str1);
        System.out.println(makeDeSimlar(str1));
    }
}
