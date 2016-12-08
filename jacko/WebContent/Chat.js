function post(user,message){
    req = new XMLHttpRequest();
    req.open("post","ChatServlet",true);
    req.setRequestHeader("Content-Type","application/x-www-form-urlencoded;charset=UTF-8");
    if((user+message).search(/[\<|\>]/)==-1){req.send("user="+user+"&message="+message);}
}