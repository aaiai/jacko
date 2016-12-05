<%@ page language="java" contentType="text/html; charset=utf-8"%>
<!DOCTYPE html>
<html>
<head>
<style>
html,body{height:100%}
</style>
</head>
<body>
<iframe src="ChatServlet" name="chat" width="100%" height="80%"></iframe>
<iframe src="ChatServlet" name="hiddenChat" hidden></iframe>
<form method="post" action="ChatServlet" target="hiddenChat">
<input type="text" name="user">
<input type="text" name="message">
<input type="submit">
</form>
</body>
</html
>