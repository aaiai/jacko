<%@ page language="java" contentType="text/html; charset=utf-8"%>
<%request.setCharacterEncoding("UTF-8");
String id=request.getParameter("login_id");
String key=request.getParameter("login_key");
String message;
if(id!=null&&key!=null){
	if(id.length()==0||key.length()==0){
		message="入力されてない";
	}else{
		message="ID:"+id+" パスワード:"+key;
	}
}else{message="";}
%>

<html>
<head>
<style>
<--.fr {left: 0%; margin-left: 0px; position: absolute;}-->
body {text-align:center; background-image:url("aaa.png"); background-repeat: no-repeat; background-position: right bottom;}
.col_1 {display: inline-block; width: 6em; border: 0;}
.col_2 {display: inline-block; width: calc(80% - 6em); border: 0;}
.ibox {width: 100%; height: 2em;}
.login_button {width:80%; height:2em;}
</style>
<link rel="stylesheet" type="text/css" href="style.css">
</head>
<body>
<%@ include file="header.jsp" %>
<div class="fr">
<img src="top.png" width="80%">
<form action="" method="post">
<div>
<span class="col_1">ID/学籍番号</span>
<span class="col_2"><input class="ibox" type="text" name="login_id" size="8"></span>
</div>
<div>
<span class="col_1">パスワード</span>
<span class="col_2"><input class="ibox" type="password" name="login_key" size="20"></span>
</div>
<div class="error_msg"><%=message %></div>
<input class="login_button" type="submit" value="ログイン">
</form>
</div>
</body>
</html>