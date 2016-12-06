<%@ page language="java" contentType="text/html; charset=utf-8"%>
<!DOCTYPE html>
<html>
<head>
<link rel="stylesheet" type="text/css" href="style.css">
<style>
iframe{
    margin:0;
    width:100%;
    height:80%;
}
#dropzone{
    width:100%;
    height:80%;
    top:0px;
    position:absolute;
}
</style>
<script type="text/javascript" src="ChatSend.js"></script>
</head>
<body>
<iframe src="ChatServlet" name="chat"></iframe>
<div id="dropzone"></div>
<script type="text/javascript" src="Upload.js"></script>
<form method="post">
<input type="text" name="user">
<input type="text" name="message">
<input type="button" value="送信" onclick="post(user.value,message.value)">
</form>
</body>
</html
>