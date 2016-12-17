function handleFileSelect(e) {
    e.stopPropagation();
    e.preventDefault();
    var files = e.dataTransfer.files;
    var fd = new FormData();
    for(var i = 0; i< files.length;i++){
        fd.append("files", files[i])
        form = document.createElement("form");

        userinput=document.createElement("input");
        userinput.setAttribute("type","hidden")
        userinput.setAttribute("name","user");
        userinput.setAttribute("value","file");
        form.appendChild(userinput);

        nameinput=document.createElement("input");
        nameinput.setAttribute("type","hidden")
        nameinput.setAttribute("name","message");
        nameinput.setAttribute("value",files[i].name);
        form.appendChild(nameinput);

        gidinput=document.createElement("input");
        gidinput.setAttribute("type","hidden")
        gidinput.setAttribute("name","gid");
        gidinput.setAttribute("value",gid);
        form.appendChild(gidinput);

        form.setAttribute("method","post");
        form.setAttribute("action","ChatServlet");
        form.setAttribute("target","hiddenchat");
        form.submit();
    }
    req = new XMLHttpRequest();
    req.open("post","UploadServlet",true);
    req.setRequestHeader("gid",gid);
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
    gidinput.setAttribute("type","hidden");
    gidinput.setAttribute("name","gid");
    gidinput.setAttribute("value",gid);
    form.appendChild(gidinput);

    nameinput=document.createElement("input");
    nameinput.setAttribute("type","hidden")
    nameinput.setAttribute("name","name");
    nameinput.setAttribute("value",name);
    form.appendChild(nameinput);

    subinput=document.createElement("input");
    subinput.setAttribute("type","hidden")
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