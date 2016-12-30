
function selectyear(){
    year=document.getElementById("year");
    while(year.firstChild) year.removeChild(year.firstChild)
    d=document.getElementById("did")
    a=d.options[d.selectedIndex].attributes["year"].value;
    for(i=0;i<=a;i++){
        opt=document.createElement("option")
        opt.text=i
        year.appendChild(opt)
    }
}