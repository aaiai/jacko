function handleFileSelect(e) {
    e.stopPropagation();
    e.preventDefault();
    var files = e.dataTransfer.files;
    var fd = new FormData()
    for(var i = 0; i< files.length;i++){ fd.append("files", files[i]) }
    req = new XMLHttpRequest();
    req.open("post","UploadServlet",false);
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