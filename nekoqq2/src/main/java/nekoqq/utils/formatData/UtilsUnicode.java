package nekoqq.utils.formatData;

public class UtilsUnicode {

    private UtilsUnicode() {
        
    }
    
    public static String unicode2str(String str){
        char[]c=str.toCharArray();
        String resultStr= "";
        for(int i=0;i<c.length;i++)
          resultStr += String.valueOf(c[i]);
        return resultStr;
   }
    
   public static String str2unicode(String str) {
       String result = "";
       for(int i = 0; i < str.length(); i++) {
           String temp = "";
           int strInt = str.charAt(i);
           if(strInt > 127) {
               temp += "\\u" + Integer.toHexString(strInt);
           } else {
               temp = String.valueOf(str.charAt(i));
           }
           
           result += temp;
       }
       return result;
   }
}



