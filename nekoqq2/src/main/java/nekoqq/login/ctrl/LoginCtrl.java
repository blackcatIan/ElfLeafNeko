package nekoqq.login.ctrl;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLDecoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nekoqq.beans.ReceiveMsgList;
import nekoqq.beans.SendMsgList;
import nekoqq.helper.ApplicationContextHelper;
import nekoqq.helper.LoginHelper;
import nekoqq.helper.StatusHelper;
import nekoqq.login.cpt.LoginCpt;
import nekoqq.login.thread.ProtectRunnable;
import nekoqq.pull.cpt.PullCpt;
import nekoqq.push.cpt.PushCpt;
import nekoqq.pushDecide.cpt.PushDecideCpt;
import nekoqq.task.cpt.TaskCpt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageDecoder;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

@Controller
@RequestMapping("/")
public class LoginCtrl {

    @Autowired
    private LoginCpt loginCpt;
    
    @Autowired
    private PullCpt pullCpt;
    
    @Autowired
    private PushDecideCpt pushDecideCpt;

    @Autowired
    private PushCpt pushCpt;
    
    @Autowired
    private TaskCpt taskCpt;
    
    @Autowired
    private ApplicationContext applicationContext; 
    
    /**
     * 登陆QQ
     * 登陆用户名密码在LoginHelper中
     * @param request
     * @param response
     * @param model
     * @return
     */
    @RequestMapping("/login")
    public String login(HttpServletRequest request,HttpServletResponse response,Model model) {

        /*   
         * @STEP1 检查全局状态位是否已登录，若已登陆则直接返回登陆成功页
         * @STEP2 检查登录是否需要验证码
         *   @STEP 2.1 若需要验证码，则返回验证码输入页
         * @STEP3 执行无验证码登录
         * @STEP4 若全局状态位登陆成功，执行CHAT相关线程
         */
        //保存上下文，用于多线程注入
        ApplicationContextHelper.saveApplicationContext(applicationContext);
        
        //@STEP1 检查全局状态位是否已登录，若已登陆则直接返回登陆成功页
        if (!StatusHelper.isLogin) {
            //@STEP2 检查登录是否需要验证码
            Boolean requireVerify = loginCpt.checkRequiredVerify();
            
            // @STEP 2.1 若需要验证码，则返回验证码输入页
            if (requireVerify) {
                //需要验证码
                //重定向验证码输入页
                return "redirect:/inputVerify";
            } 
            //@STEP3 执行无验证码登录
            //不需要验证码
            //进行登陆
            loginCpt.catchDefaultVerifyCode();
            StatusHelper.isLogin = loginCpt.doLogin();
            
            if (StatusHelper.isLogin) {
                loginCpt.getContract();
                
                
                if (!StatusHelper.isProtected) {
                    System.out.println("开启掉线保护");
                    StatusHelper.isProtected = true;
                    ProtectRunnable pr = new ProtectRunnable();
                    Thread tid = new Thread(pr);
                    tid.start();
                }
            }

            //@STEP4 若全局状态位登陆成功，执行CHAT相关线程
            startNekoQQThreads();
            
            
            
        }
        //返回登陆成功页
        return "/index";
    }
    
    
    /**
     * 用户提交输入验证码(验证码登陆)
     * @return
     */
    @RequestMapping("/submitVerifyCode")
    public String submitVerifyCode(HttpServletRequest request,HttpServletResponse response,Model model) {
        
        //保存上下文，用于多线程注入
        ApplicationContextHelper.saveApplicationContext(applicationContext);
        
        if (StatusHelper.isLogin) {
            return "/index";
        }
        
        
        String verifyCode = request.getParameter("verifyCode");
        
        //用验证码登陆
        LoginHelper.loginUserInfoBean.setDefaultVerifyCode(verifyCode.toUpperCase());
        
        
        StatusHelper.isLogin = loginCpt.doLogin();
        if (StatusHelper.isLogin) {
            
            startNekoQQThreads();
            
            return "/index";
        } else {
            return "/inputVerify";
        }
    }
    
    
    
    
    
    
    
    
    
    
    /**
     * 验证码输入页
     * @param request
     * @param response
     * @param model
     * @return
     */
    @RequestMapping("/inputVerify")
    public String inputVerify(HttpServletRequest request,HttpServletResponse response,Model model) {
        return "/inputVerify";
    }
    
    /**
     * 页面获取验证码(页面显示验证码)
     * @return
     */
    @RequestMapping("/verifyCode")
    public void verifyCode(HttpServletRequest request,HttpServletResponse response,Model model) throws Exception{
        //从QQ官网获得验证码保存于本地
        loginCpt.saveVerifyImage();
        
        //读取验证码图片到输出流
        //图片路径
        String path = Thread.currentThread().getContextClassLoader()
                .getResource("").getPath() + "/verifycode.jpg";
        path = URLDecoder.decode(path, "utf-8").replaceAll("//", "/");       
        OutputStream output = response.getOutputStream();// 得到输出流  
        // 设定输出的类型  
        response.setContentType("image/jpeg");
        // 得到图片的文件流  
        File imageFile = new File(path);
        if (!imageFile.exists()) {
            imageFile.createNewFile();
        }
        
        InputStream imageIn = new FileInputStream(imageFile); 
        // 得到输入的编码器，将文件流进行jpg格式编码  
        JPEGImageDecoder decoder = JPEGCodec.createJPEGDecoder(imageIn); 
        // 得到编码后的图片对象  
        BufferedImage image = decoder.decodeAsBufferedImage();  
        // 得到输出的编码器  
        JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(output);  
        encoder.encode(image);// 对图片进行输出编码  
        imageIn.close();// 关闭文件流  
        output.close();
    }
    
    
    
    /*======================private==========================*/
    public void startNekoQQThreads() {
        if (StatusHelper.isLogin) {

            //关闭消息列队锁定
            ReceiveMsgList.getInstance().setMsgListLock(false);
            SendMsgList.getInstance().setMsgListLock(false);
            
            //开启推送消息
            pullCpt.startPull();
            //开启消息决策
            pushDecideCpt.startPushDecide();
            //开启消息发送
            pushCpt.startPush();
            //开启任务相关线程
            taskCpt.startTaskThread();
            
//            //开启聊天反应机制处理线程
//            InitChatRunnable initChatRunnable = new InitChatRunnable(httpclient, loginUserInfoBean);
//            Thread initChatThread = new Thread(initChatRunnable);
//            initChatThread.start();
        }
    }
    
    
}
