<%@ page language="java" contentType="text/html; charset=utf-8"%>
<%
String gid=request.getParameter("gid");
%>
<!DOCTYPE html>
<html>
<head>
<%@ include file="aaa.html" %>
<style>
iframe{
    margin:0;
    width:100%;
    height:80%;
}
#menu{
    right:0;
    top:0;
    height:200px;
    width:200px;
    position:absolute;
}
input[type="checkbox"]{
    height:100px;
    width:100px;
    top:0;
    right:0;
    position:absolute;

}
ul{
    transform:scale(0);
    left:0;
}
input[type="checkbox"]:checked+ul{
    transform:translateY(100px) scale(1);
}
li{
    border:solid 1px black;

}
</style>
<script>gid=window.location.search.split("=")[1]</script>
<script type="text/javascript" src="Chat.js"></script>
</head>
<body>
<iframe src=<%="ChatServlet?gid="+gid %> name="chat"></iframe>
<iframe src=<%="ChatServlet?gid="+gid %> name="hiddenchat" hidden></iframe>
<div id="menu">
<input type="checkbox">
<ul>
<li>テスト1</li>
<li>テスト2</li>
<li>テスト3</li>
<li>テスト4</li>
<li>テスト5</li>
</ul>
</div>
<form id="chat" method="post" action="ChatServlet" target="hiddenchat">
<input type="text" name="user">
<input type="text" name="message">
<input type="hidden" name="gid" value=<%=gid %>>
<input type="submit">
</form>
<button onclick="location.reload()">更新</button>
</body>
</html>