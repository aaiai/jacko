<%@ page language="java" contentType="text/html; charset=utf-8"%>
<%
request.setCharacterEncoding("UTF-8");
String text=request.getParameter("text");
if(text!=null){
}
%>
<!DOCTYPE html>
<html>
<head>
</head>
<iframe src="ChatServlet" name="chat"></iframe>
<iframe src="ChatServlet" name="hiddenChat" hidden></iframe>
<form method="post" action="ChatServlet" target="hiddenChat">
<input type="text" name="user">
<input type="text" name="message">
<input type="submit">
</form>
</html>