<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    
    <title>My JSP 'MyJsp.jsp' starting page</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->
    <script type="text/javascript" src="/static/js/common/jquery-1.9.1.min.js"></script>
    <script type="text/javascript">
    $(document).ready(function(){
    	$("#verifyCodeImg").click(function(){
    		$("#verifyCodeImg").attr("src","/verifyCode?t=" + Math.random());
    	});
    	
    });
    </script>
  </head>
  
  <body>
    <form action="/submitVerifyCode" method="post">
	    <div> 请输入验证码:</div>
	    <div><img id="verifyCodeImg" src="/verifyCode?t=<%=Math.random()%>" /></div>
	    <div> <input type="text" name="verifyCode"/></div>
	    <div> <input type="submit" value="登陆"/></div>
    </form>
  </body>
</html>
