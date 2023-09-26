var monH1=document.createElement("h1");
var nb=0;
monH1.innerHTML="0";
document.getElementById("monBody").appendChild(monH1);
function allCookies(){
	var tab=document.cookie.split("; ");
	var cookies=[];
	for (var i=0;i<tab.length;i++){
		var name=tab[i].substring(0,tab[i].indexOf("="));
		var value=tab[i].substring(tab[i].indexOf("=")+1);
		cookies[name]=value;
	}
	return cookies;
}
console.log(allCookies());