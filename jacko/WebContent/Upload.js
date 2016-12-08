function handleFileSelect(e) {
    e.stopPropagation();
    e.preventDefault();
    var files = e.dataTransfer.files;
    var fd = new FormData();
    for(var i = 0; i< files.length;i++){
        fd.append("files", files[i])
        req = new XMLHttpRequest();
        req.open("post","ChatServlet",true);
        req.setRequestHeader("Content-Type","application/x-www-form-urlencoded;charset=UTF-8");
        req.send("user=file&message=<button onclick=\"download(1,'"+files[i].name+"',0)\">"+files[i].name+"</button>");
    }
    req = new XMLHttpRequest();
    req.open("post","UploadServlet",true);
    req.send(fd);
}
function handleDragOver(e) {
    e.stopPropagation();
    e.preventDefault();
    e.dataTransfer.dropEffect = "copy";
}
var dropZone = document.getElementById("dropzone");
dropZone.addEventListener("dragover", handleDragOver);
dropZone.addEventListener("drop", handleFileSelect);

function download(gid,name,sub){
    form = document.createElement("form");
    gidinput=document.createElement("input");
    gidinput.setAttribute("type","text");
    gidinput.setAttribute("name","gid");
    gidinput.setAttribute("value",gid);
    form.appendChild(gidinput);

    nameinput=document.createElement("input");
    nameinput.setAttribute("type","text")
    nameinput.setAttribute("name","name");
    nameinput.setAttribute("value",name);
    form.appendChild(nameinput);

    subinput=document.createElement("input");
    subinput.setAttribute("type","text")
    subinput.setAttribute("name","sub");
    subinput.setAttribute("value",sub);
    form.appendChild(subinput);

    form.setAttribute("method","post");
    form.setAttribute("action","DownloadServlet");
    form.setAttribute("target","_top");
    form.submit();
    /*
    req.open("post","DownloadServlet",true);
    req.setRequestHeader("Content-Type","application/x-www-form-urlencoded;charset=UTF-8");
    console.log("gid="+gid+"&name="+name+"&sub="+sub);
    req.send("gid="+gid+"&name="+name+"&sub="+sub);*/
}