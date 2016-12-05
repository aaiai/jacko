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
</head>
<body>
<iframe src="ChatServlet" name="chat"></iframe>
<div id="dropzone"></div>
<script type="text/javascript" src="Upload.js"></script>
<iframe src="ChatServlet" name="hiddenChat" hidden></iframe>
<form method="post" action="ChatServlet" target="hiddenChat">
<input type="text" name="user">
<input type="text" name="message">
<input type="submit">
</form>

</body>
</html
>