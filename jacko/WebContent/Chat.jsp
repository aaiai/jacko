<%@ page language="java" contentType="text/html; charset=utf-8"%>
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
<script type="text/javascript" src="Chat.js"></script>
</head>
<body>
<iframe src="ChatServlet" name="chat"></iframe>
<form method="post">
<input type="text" name="user">
<input type="text" name="message">
<input type="button" value="送信" onclick="post(user.value,message.value)">
</form>
<button onclick="download(1,'aa.txt',0)">aa.txt</button>
</body>
</html>