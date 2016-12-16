<%@ page language="java" contentType="text/html; charset=utf-8"%>
<%
String gid=request.getParameter("gid");
%>
<!DOCTYPE html>
<html>
<head>
<title>chat</title>
<link rel="stylesheet" type="text/css" href="style.css">
<style>
iframe{
    margin:0;
    width:100%;
    height:80%;
}

</style>
<script>console.log("aaa")
gid=window.location.search.split("=")[1]
console.log("gid="+gid)</script>
<script type="text/javascript" src="Chat.js"></script>
</head>
<body>
<iframe src=<%="ChatServlet?gid="+gid %> name="chat"></iframe>
<iframe src=<%="ChatServlet?gid="+gid %> name="hiddenchat" hidden></iframe>
<form id="chat" method="post" action="ChatServlet" target="hiddenchat">
<input type="text" name="user">
<input type="text" name="message">
<input type="hidden" name="gid" value=<%=gid %>>
<input type="submit">
</form>
<button onclick="location.reload()">更新</button>
</body>
</html>