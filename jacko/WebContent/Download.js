function download(gid,name,sub){
    req = new XMLHttpRequest();
    req.open("post","DownloadServlet",true);
    req.setRequestHeader("Content-Type","application/x-www-form-urlencoded;charset=UTF-8");
    req.send("gid="+gid+"name="+name+"sub="+sub);
}