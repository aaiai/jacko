<%@ page language="java" contentType="text/html; charset=utf-8"%>
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