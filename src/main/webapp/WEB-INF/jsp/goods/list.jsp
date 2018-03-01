<%@ page pageEncoding="utf-8" %>
<%
    String path = request.getContextPath();
%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport"
          content="width=device-width,initial-scale=1,minimum-scale=1.0,maximum-scale=1.0,user-scalable=0">
    <!-- 屏蔽浏览器自动识别数字为电话号码 -->
    <meta name="format-detection" content="telephone=no"/>
    <!-- 屏蔽浏览器自动识别邮件 -->
    <meta name="format-detection" content="email=no"/>
    <title>订单列表</title>
    <script src="<%=path%>/assets/js/jquery-2.1.0.js"></script>
</head>

    <body>
        当前页面: <script>document.write(window.location.href)</script> <br />
        openid: ${openid} <br />

    </body>
</html>