package nekoqq.utils.file;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.log4j.Logger;



public class UtilsFile {
    private static Logger logger =Logger.getLogger(UtilsFile.class);

    /**
     * 读取TXT文件
     * 换行符\r\n
     * @param path
     * @return
     */
    public static String readTxtFile(String path) {
        
        InputStreamReader inputReader = null;
        BufferedReader bufferReader = null;
        StringBuffer strBuffer = new StringBuffer();
        try
        {
            InputStream inputStream = new FileInputStream(path);
            inputReader = new InputStreamReader(inputStream,"utf8");
            bufferReader = new BufferedReader(inputReader);
            
            // 读取一行
            String line = null;
             
            while ((line = bufferReader.readLine()) != null)
            {
                strBuffer.append(line);
                strBuffer.append("\r\n");
            } 
            
        }
        catch (IOException e)
        {
            logger.error(e.getMessage());
        }
        finally
        {
            try {
                inputReader.close();
                bufferReader.close();
            } catch (Exception e2) {
                logger.error(e2.getMessage());
            }
        }
       

        return strBuffer.toString();

    }
    
    
    /**
     * 写入txt文件
     * @param path
     * @param str
     */
    public static void writeTxtFile(String path,String str) {
        FileWriter fw = null;
        BufferedWriter bw = null;
        try {
            fw = new FileWriter(path);
            bw = new BufferedWriter(fw);
            bw.write(str);
        } catch (Exception e) {
            logger.error(e.getMessage());
        } finally {
            try {
                fw.close();
                bw.close();
            } catch (Exception e2) {
                logger.error(e2.getMessage());
            }
            
        }

    }
    
   
}
