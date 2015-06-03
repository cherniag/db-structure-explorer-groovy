/*
 Leanplum Javascript SDK v1.1.7.
 Copyright 2015, Leanplum, Inc. All rights reserved.
 */
(function(){function r(a,b,d,q){if(a===b)return 0!==a||1/a==1/b;if(null==a||null==b)return a===b;a instanceof m&&(a=a.ta);b instanceof m&&(b=b.ta);var k=Object.prototype.toString.call(a);if(k!=Object.prototype.toString.call(b))return!1;switch(k){case "[object String]":return a==String(b);case "[object Number]":return a!=+a?b!=+b:0==a?1/a==1/b:a==+b;case "[object Date]":case "[object Boolean]":return+a==+b;case "[object RegExp]":return a.source==b.source&&a.global==b.global&&a.multiline==b.multiline&&
a.ignoreCase==b.ignoreCase}if("object"!=typeof a||"object"!=typeof b)return!1;for(var g=d.length;g--;)if(d[g]==a)return q[g]==b;d.push(a);q.push(b);var g=0,e=!0;if("[object Array]"==k){if(g=a.length,e=g==b.length)for(;g--&&(e=r(a[g],b[g],d,q)););}else{var k=a.constructor,h=b.constructor;if(k!==h&&!(m.Z(k)&&k instanceof k&&m.Z(h)&&h instanceof h))return!1;for(var f in a)if(m.I(a,f)&&(g++,!(e=m.I(b,f)&&r(a[f],b[f],d,q))))break;if(e){for(f in b)if(m.I(b,f)&&!g--)break;e=!g}}d.pop();q.pop();return e}
function s(){this.H=this.G=!1}function n(){this.l="";this.V={}}function m(){}var e={};this.Leanplum||(this.Leanplum=e);var t="https://www.leanplum.com/api",u="dev.leanplum.com",v=10,b={ga:!0,S:[],r:[],k:[],L:{},F:"",M:!0,u:5};e.setApiPath=function(a){t=a};e.setEmail=function(a){b.Aa=a};e.setUpdateCheckingEnabledInDevelopmentMode=function(a){b.ga=a};e.setNetworkTimeout=function(a){v=a};e.setAppIdForDevelopmentMode=function(a,c){b.t=a;b.B=c;b.m=!0};e.setAppIdForProductionMode=function(a,c){b.t=a;b.B=
c;b.m=!1};e.setSocketHost=function(a){u=a};e.setDeviceId=function(a){b.j=a};e.setAppVersion=function(a){b.sa=a};e.setDeviceName=function(a){b.ia=a};e.setDeviceModel=function(a){b.ha=a};e.setSystemName=function(a){b.qa=a};e.setSystemVersion=function(a){b.ra=a};e.setVariables=function(a){b.U=a;if(b.m){var c={};c.vars=a;b.e("setVars",(new n).body(JSON.stringify(c)),{n:!0})}};e.setRequestBatching=function(a,c){b.M=a;b.u=c};e.getVariables=function(){return void 0!==b.R?b.R:b.U};e.getVariable=function(a){for(var b=
e.getVariables(),d=0;d<arguments.length;d++)b=b[arguments[d]];return b};e.addStartResponseHandler=function(a){b.k.push(a);b.Q&&a(b.q)};e.addVariablesChangedHandler=function(a){b.r.push(a);b.la&&a()};e.removeStartResponseHandler=function(a){a=b.k.indexOf(a);0<=a&&b.k.splice(a,1)};e.removeVariablesChangedHandler=function(a){a=b.r.indexOf(a);0<=a&&b.r.splice(a,1)};e.start=function(a,c,d){"function"==typeof a?(d=a,c={},a=null):"object"==typeof a&&null!==a&&void 0!==a?(d=c,c=a,a=null):"function"==typeof c&&
(d=c,c={});b.g=a;d&&e.addStartResponseHandler(d);b.e("start",(new n).add("userAttributes",JSON.stringify(c)).add("country","(detect)").add("region","(detect)").add("city","(detect)").add("location","(detect)").add("systemName",b.qa||p.da).add("systemVersion",""+(b.ra||"")).add("browserName",p.X).add("browserVersion",""+p.version).add("locale","(detect)").add("deviceName",b.ia||p.X+" "+p.version).add("deviceModel",b.ha||"Web Browser").add("includeDefaults",!1),{d:!0,n:!0,response:function(a){b.Q=!0;
a=b.P(a);if(b.ma(a)){b.q=!0;if(b.m){var c=a.latestVersion;c&&console.log("A newer version of Leanplum, "+c+", is available. Go to leanplum.com to download it.");WebSocket?b.T():console.log("Your browser doesn't support WebSockets.")}b.J(a.vars,a.actionMetadata);b.F=a.token}else b.q=!1,b.$();for(a=0;a<b.k.length;a++)b.k[a](b.q)}})};e.startFromCache=function(a,c,d){"function"==typeof a?(d=a,a=null):"object"==typeof a&&null!==a&&void 0!==a?(d=c,a=null):"function"==typeof c&&(d=c);b.g=a;d&&e.addStartResponseHandler(d);
b.Q=!0;b.q=!0;b.m&&(WebSocket?b.T():console.log("Your browser doesn't support WebSockets."));b.$();for(a=0;a<b.k.length;a++)b.k[a](b.q)};b.T=function(){var a=new s,c=!1;a.onopen=function(){if(!c){console.log("Leanplum: Connected to development server.");var d={};d.appId=b.t;d.deviceId=b.j;a.wa(d);c=!0}};a.onerror=function(a){console.log("Leanplum: Socket error",a)};a.onmessage=function(a,c){"updateVars"==a?b.e("getVars",(new n).add("includeDefaults",!1),{d:!1,n:!0,response:function(a){a=b.P(a).vars;
m.isEqual(a,b.O)||b.J(a)}}):"registerDevice"==a&&alert("Your device has been registered to "+c[0].email+".")};a.onclose=function(){console.log("Leanplum: Disconnected to development server.");c=!1};a.Y();setInterval(function(){a.G||a.H||a.Y()},5E3)};e.stop=function(){b.e("stop",void 0,{n:!0,d:!0})};e.pauseSession=function(){b.e("pauseSession",void 0,{n:!0,d:!0})};e.resumeSession=function(){b.e("resumeSession",void 0,{n:!0,d:!0})};e.pauseState=function(){b.e("pauseState",void 0,{d:!0})};e.resumeState=
function(){b.e("resumeState",void 0,{d:!0})};e.setUserAttributes=function(a,c){void 0===c&&(c=a,a=null);b.e("setUserAttributes",(new n).add("userAttributes",JSON.stringify(c)).add("newUserId",a),{d:!0});a&&(b.g=a,b.f("__leanplum_user_id",b.g))};

 //e.track=function(a,c,d,e){
 //     "object" == typeof c && null!==c && void 0!==c
 //         ?
 //     (e=c,c=d=void 0)
 //         :
 //     "string"==typeof c      ?      (e=d,d=c,c=void 0)      :      "object"==typeof d && (null!==d&&void 0!==d) && (e=d,d=void 0);
 //
 //    b.e(
 //         "track",
 //         (new n).add("event",a).add("value",c||0).add("info",d).add("params", JSON.stringify(e)),
 //         {d:!0}
 //     )
 //};

 e.track=function(event, data, callback){
  b.e(
      "track",
      (new n).add("event",event).add("value",0).add("info", void 0).add("params", JSON.stringify(data)),
      {Da: callback, error: callback}
  )
 };


e.advanceTo=function(a,c,d){"object"==typeof c&&(null!==c&&void 0!==c)&&(d=c,c=void 0);b.e("advance",(new n).add("state",a).add("info",c).add("params",JSON.stringify(d)),{d:!0})};b.J=function(a,c){b.O=a;b.L=c;b.la=!0;b.R=b.K(b.U,a);b.za();for(var d=0;d<b.r.length;d++)b.r[d]()};b.K=function(a,c){function d(a){return function(b){if(a instanceof Array)for(var c=0;c<a.length;c++)b(a[c]);else for(c in a)b(c)}}if("number"==typeof c||"boolean"==typeof c||"string"==typeof c)return c;
if(null===c||void 0===c)return a;var e=d(a),k=d(c),g=!1;if(null==a&&!(c instanceof Array)){var g=null,l;for(l in c){null===g&&(g=!0);if("string"!=typeof l){g=!1;break}if(3>l.length||"["!=l.charAt(0)||"]"!=l.charAt(l.length-1)){g=!1;break}var h=l.substring(1,l.length-1);if(!(""+parseInt(h))==h){g=!1;break}}}if(a instanceof Array||g){var f=[];e(function(a){f.push(a)});k(function(a){var d=parseInt(a.substring(1,a.length-1));for(a=c[a];d>=f.length;)f.push(null);f[d]=b.K(f[d],a)});return f}f={};e(function(b){if(null===
c[b]||void 0===c[b])f[b]=a[b]});k(function(d){f[d]=b.K(null!=a?a[d]:null,c[d])});return f};b.$=function(){try{b.J(JSON.parse(b.i("__leanplum_variables")||null),JSON.parse(b.i("__leanplum_action_metadata"))||null),b.F=b.i("__leanplum_token")}catch(a){console.log("Leanplum: Invalid diffs: "+a)}};b.za=function(){b.f("__leanplum_variables",JSON.stringify(b.O||{}));b.f("__leanplum_action_metadata",JSON.stringify(b.L||{}));b.f("__leanplum_token",b.F)};b.pa=function(a){var c=b.i("__leanplum_unsynced")||
0;b.f("__leanplum_unsynced_"+c,JSON.stringify(a));c++;b.f("__leanplum_unsynced",c)};b.oa=function(){var a=[],c=b.i("__leanplum_unsynced")||0;b.D("__leanplum_unsynced");for(var d=0;d<c;d++){var e="__leanplum_unsynced_"+d;try{var k=JSON.parse(b.i(e));a.push(k)}catch(g){}b.D(e)}return a};

 b.e=function(a,c,d){
  d=d||{};
  c=c||new n;
  b.j||(b.j=b.i("__leanplum_device_id"));

  if(!b.j){
   for(var e="",k=0;16>k;k++)e+="ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789".charAt(Math.floor(62*Math.random()));
   b.j=e;
   b.f("__leanplum_device_id",e)
  }

  b.g||(b.g=b.i("__leanplum_user_id"),b.g||(b.g=b.j));
  b.f("__leanplum_user_id",b.g);
  a=c.W().add("sdkVersion","1.1.7").add("deviceId",b.j).add("userId",b.g).add("action",a).add("versionName",b.sa).add("devMode",b.m).add("time",""+(new Date).getTime()/1E3);
  var g=d.Da||d.response,l=d.error||d.response;

  if(b.t&&b.B)
   if(c.body())
    b.s("POST",t+"?"+a.l,c.body(),g,l,d.d);
   else{
    c=b.m||d.n||!b.M;
    var h=function(){
     var a=b.oa();
     if(0<a.length){
      var a=JSON.stringify({data:a}),
          c=(new n).W().add("sdkVersion", "1.1.7").add("action","multi").add("time",""+(new Date).getTime()/1E3).l;
      b.s("POST",t+"?"+c,a,g,l,d.d)
     }
    };

    !c&&b.u&&(e=(new Date).getTime()/1E3,!b.v||e-b.v>=b.u?(c=!0,b.v=e):b.N||(b.N=setTimeout(function(){b.N=null;b.v=(new Date).getTime()/1E3;h()},1E3*(b.u-(e-b.v)))));

    b.pa(a.V);c&&h()
   }else console.error("Leanplum App ID and client key are not set. Make sure you are calling setAppIdForDevelopmentMode or setAppIdForProductionMode before issuing API calls."), l&&l("Leanplum App ID and client key are not set. Make sure you are calling setAppIdForDevelopmentMode or setAppIdForProductionMode before issuing API calls.")
 };

b.na=function(a){return a&&a.response?a.response.length:0};b.ka=function(a,b){return a&&a.response?a.response[b]:null};b.P=function(a){var c=b.na(a);return 0<c?b.ka(a,c-1):null};b.ma=function(a){return a?a.success?!0:!1:!1};b.Ba=function(a){return a?(a=a.error)?a.message:null:null};b.p=void 0;b.A={};b.i=function(a){return!1===b.p?b.A[a]:localStorage[a]};b.f=function(a,c){if(!1===b.p)b.A[a]=c;else try{localStorage[a]=c}catch(d){b.p=!1,b.f(a,c)}};b.D=function(a){if(!1===b.p)delete b.A[a];else try{localStorage.removeItem(a)}catch(c){b.p=
!1,b.D(a)}};n.prototype.add=function(a,b){if("undefined"===typeof b)return this;this.l&&(this.l+="&");this.l+=a+"="+encodeURIComponent(b);this.V[a]=b;return this};n.prototype.body=function(a){return a?(this.fa=a,this):this.fa};n.prototype.W=function(){return this.add("appId",b.t).add("client","js").add("clientKey",b.B)};s.prototype.wa=function(a){this.h.send("5:::"+JSON.stringify({name:"auth",args:a}))};s.prototype.Y=function(){var a=this;a.H=!0;b.s("POST","http://"+u+"/socket.io/1","",function(b){b=
b.split(":");var d=1E3*(parseInt(b[1])/2);a.h=new WebSocket("ws://"+u+"/socket.io/1/websocket/"+b[0]);var e=null;a.h.onopen=function(){a.G=!0;a.H=!1;if(a.onopen)a.onopen();e=setInterval(function(){a.h.send("2:::")},d)};a.h.onclose=function(){a.G=!1;clearInterval(e);if(a.onclose)a.onclose()};a.h.onmessage=function(b){var c=b.data.split(":"),d=parseInt(c[0]);if(2==d)a.h.send("2::");else if(5==d){if(d=c[1],c=JSON.parse(c.slice(3).join(":")),b=c.name,c=c.args,d&&a.h.send("6:::"+d),a.onmessage)a.onmessage(b,
c)}else 7==d&&console.log("Socket error: "+b.data)};a.h.onerror=function(b){a.h.close();if(a.onerror)a.onerror(b)}},null,!1,!0)};b.ea=function(a,c,d,e,k,g,l){var h=new XDomainRequest;h.onload=function(){var a,c=!1;if(l)a=h.responseText;else try{a=JSON.parse(h.responseText)}catch(d){setTimeout(function(){k&&k(null,h)},0),c=!0}c||setTimeout(function(){e&&e(a,h)},0);g&&(b.w=!1,b.C())};h.onerror=h.ontimeout=function(){setTimeout(function(){k&&k(null,h)},0);g&&(b.w=!1,b.C())};h.onprogress=function(){};
h.open(a,c);h.timeout=1E3*v;h.send(d)};b.ja=function(a){b.S.push(a)};b.C=function(){var a=b.S.shift();a&&b.s.apply(null,a)};

 b.s=function(a,c,d,e,k,g,l){
  if(g){
   if(b.w)return b.ja(arguments);
   b.w=!0
  }

  if("undefined"!==typeof XDomainRequest)
   return"http:"===location.protocol&&0==c.indexOf("https:")&&(c="http:"+c.substring(6)),b.ea.apply(null,arguments);

  var h=!1,
      f=new XMLHttpRequest;
  f.onreadystatechange=function(){
   if(4===f.readyState&&!h){
    h=!0;
    var a,c=!1;
    if(l)
     a=f.responseText;
    else
     try{
      a=JSON.parse(f.responseText)
     }catch(d){
      setTimeout(function(){k&&k(null,f)},0),c=!0
     }
    c || (200<=f.status && 300>f.status ? setTimeout(function(){e&&e(a,f)},0) : setTimeout(function(){k&&k(a,f)},0));

    g&&(b.w=!1,b.C())
   }
  };

  f.open(a,c,!0);
  f.setRequestHeader("Content-Type","text/plain");f.send(d);setTimeout(function(){h||f.abort()},1E3*v)
 }

;m.Ca=function(a){each(slice.call(arguments,1),function(b){for(var d in b)a[d]=b[d]});return a};m.Z=function(a){return"function"===typeof a};m.I=function(a,b){return hasOwnProperty.call(a,b)};m.isEqual=function(a,b){return r(a,b,[],[])};var p=
{xa:function(){this.X=this.aa(this.ua)||"An unknown browser";this.version=this.ba(navigator.userAgent)||this.ba(navigator.appVersion)||"an unknown version";this.da=this.aa(this.va)||"an unknown OS"},aa:function(a){for(var b=0;b<a.length;b++){var d=a[b].b,e=a[b].ya;this.ca=a[b].o||a[b].a;if(d){if(-1!=d.indexOf(a[b].c))return a[b].a}else if(e)return a[b].a}},ba:function(a){var b=a.indexOf(this.ca);if(-1!=b)return parseFloat(a.substring(b+this.ca.length+1))},ua:[{b:navigator.userAgent,c:"Chrome",a:"Chrome"},
{b:navigator.userAgent,c:"OmniWeb",o:"OmniWeb/",a:"OmniWeb"},{b:navigator.vendor,c:"Apple",a:"Safari",o:"Version"},{ya:window.opera,a:"Opera",o:"Version"},{b:navigator.vendor,c:"iCab",a:"iCab"},{b:navigator.vendor,c:"KDE",a:"Konqueror"},{b:navigator.userAgent,c:"Firefox",a:"Firefox"},{b:navigator.vendor,c:"Camino",a:"Camino"},{b:navigator.userAgent,c:"Netscape",a:"Netscape"},{b:navigator.userAgent,c:"MSIE",a:"Explorer",o:"MSIE"},{b:navigator.userAgent,c:"Gecko",a:"Mozilla",o:"rv"},{b:navigator.userAgent,
c:"Mozilla",a:"Netscape",o:"Mozilla"}],va:[{b:navigator.platform,c:"Win",a:"Windows"},{b:navigator.platform,c:"Mac",a:"Mac OS"},{b:navigator.userAgent,c:"iPhone",a:"iPhone OS"},{b:navigator.platform,c:"Linux",a:"Linux"}]};p.xa()}).call(this);
