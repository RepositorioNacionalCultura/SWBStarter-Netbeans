<%-- 
    Document   : sessionInitializerMenu
                Presenta la interface para que el usuario escoja el medio de autenticacion de su preferencia.
    Created on : 13/02/2018, 05:43:59 PM
    Author     : jose.jimenez
--%><%@ page contentType="text/html; charset=ISO-8859-1" pageEncoding="UTF-8"%>
<%@page import="org.semanticwb.portal.api.SWBParamRequest, org.semanticwb.SWBPlatform, org.semanticwb.model.WebPage"%>
<%@page import="mx.gob.cultura.portal.resources.SessionInitializer, org.semanticwb.portal.api.SWBResourceException"%>
<%
    SWBParamRequest paramsRequest = (SWBParamRequest) request.getAttribute("paramRequest");
    StringBuilder text = new StringBuilder(256);
    String mainLabel;
    String faceAppId;
    String faceVersion;
    String twitConsumerKey;
    String twitConsumerSecret;
    String gpCliendId;
    
    boolean isSocialNetUser = false;
    boolean showFB = false;
    boolean showTwitter = false;
    boolean showGPlus = false;
    String source = "";

    try {
        mainLabel = paramsRequest.getLocaleString("lbl_main");
    } catch (SWBResourceException swbe) {
        mainLabel = "Iniciar sesión";
    }
    faceAppId = paramsRequest.getWebPage().getWebSite().getModelProperty("facebook_appid");
    if (null != faceAppId && !faceAppId.isEmpty()) {
        showFB = true;
    }
    try {
        faceVersion = paramsRequest.getLocaleString("vl_facebook_version");
    } catch (SWBResourceException swbe) {
        faceVersion = "v2.11";
    }
    
    twitConsumerKey = paramsRequest.getWebPage().getWebSite().getModelProperty("twitter_consumerKey");
    twitConsumerSecret = paramsRequest.getWebPage().getWebSite().getModelProperty("twitter_consumerSecret");
    if (null != twitConsumerKey && !twitConsumerKey.isEmpty() && null != twitConsumerSecret && !twitConsumerSecret.isEmpty()) {
        showTwitter = true;
    }
    
    gpCliendId = paramsRequest.getWebPage().getWebSite().getModelProperty("google_clientid");
    if (null != gpCliendId && !gpCliendId.isEmpty()) {
        showGPlus = true;
    }
    System.out.println("showGPlus=" + showGPlus + "\nUsuario firmado: " + paramsRequest.getUser().isSigned());
    
    if (paramsRequest.getUser().isSigned()) {
        if (null != request.getSession(false) &&
                request.getSession(false).getAttribute("isSocialNetUser") != null) {
            isSocialNetUser = Boolean.parseBoolean(
                    (String) request.getSession(false)
                            .getAttribute("isSocialNetUser"));
            source = isSocialNetUser ? (String) request.getSession(false).getAttribute("source") : "";
        }
    }
    
    if (showGPlus) {
%>
        <script src="https://apis.google.com/js/platform.js?onload=initGP" async defer></script>
        <script>
          var googleUser = {};
          function initGP() {
              console.log("Iniciando api de G+");
              gapi.load('auth2', function() {
                  auth2 = gapi.auth2.init({
                      client_id : '<%=gpCliendId%>'
                  });
              });
<%      if (!paramsRequest.getUser().isSigned()) {%>
//              gapi.signin2.render('googlePButton', {
//                  scope: 'profile email openid',
//                  onsuccess: function(googleUser) {
//                                console.log('Usuario firmado!\n' + JSON.stringify(googleUser));
//                                alert('Usuario firmado!\n' + JSON.stringify(googleUser));
//                                var user = {
//                                    id : googleUser.getAuthResponse().id_token,
//                                    name : googleUser.getBasicProfile().getName(),
//                                    email : googleUser.getBasicProfile().getEmail()
//                                };
//                                console.log('Usuario firmado!\n' + JSON.stringify(googleUser));
//                                openSWBSession(user, '<%=SessionInitializer.GOOGLEP%>');
//                            },
//                  onfailure: function() {
//                                alert("No se podrá iniciar sesión con Google+!");
//                            }
//              });
//              loginGoogleUser(document.getElementById("loginWithGoogle"));
          }
          
          function loginGPByClick() {
              var auth2 = gapi.auth2.getAuthInstance();
              if (!auth2) {
                  console.log("No hay instancia de gapi.Authentication!");
              }
              auth2.signIn({
                  prompt: 'select_account'
              }).then(function(googleUser) {
                  var user = {
                      id : googleUser.getBasicProfile().getId(),
                      name : googleUser.getBasicProfile().getName(),
                      email : googleUser.getBasicProfile().getEmail()
                  };
                  console.log('Usuario firmado!\n' + JSON.stringify(googleUser));
                  alert('antes de enviar peticion a SWB - user token: ' + googleUser.getAuthResponse().id_token);
                  openSWBSession(user, '<%=SessionInitializer.GOOGLEP%>');
              });
          }
          function loginGoogleUser(element) {
              console.log("sobre el elemento: " + element.id);
              var auth2 = gapi.auth2.getAuthInstance();
              if (auth2) {
              auth2.attachClickHandler(element,
                    {},
                    function(googleUser) {
                        alert('Usuario firmado!\n' + JSON.stringify(googleUser));
                        var user = {
                            id : googleUser.getBasicProfile().getId(),
                            name : googleUser.getBasicProfile().getName(),
                            email : googleUser.getBasicProfile().getEmail()
                        };
                        console.log('Usuario firmado!\n' + JSON.stringify(googleUser));
                        openSWBSession(user, '<%=SessionInitializer.GOOGLEP%>');
                    },
                    function(error) {
                        alert(JSON.stringify(error, undefined, 2));
              });
              } else {
                console.log('*** auth2 = null');
              }
              alert("Listo!!!");
          }
          
<%      } else { %>
          }
          function signOutGP() {
              var auth2 = gapi.auth2.getAuthInstance();
              auth2.signOut().then(function () {
                  closeSWBSession('<%=source%>');
                  console.log('User signed out');
              });
          }
<%
        }
    } else {
%>
        <script>
<%
    }
%>
          (function(d, s, id){
             var js, fjs = d.getElementsByTagName(s)[0];
             if (d.getElementById(id)) {return;}
             js = d.createElement(s); js.id = id;
             js.src = "https://connect.facebook.net/es_MX/sdk.js";
             fjs.parentNode.insertBefore(js, fjs);
          }(document, 'script', 'facebook-jssdk'));
          window.fbAsyncInit = function() {
            FB.init({
              appId      : '<%=faceAppId%>',
              cookie     : true,
              xfbml      : true,
              version    : '<%=text.append(faceVersion)%>'
            });
            FB.getLoginStatus(function(response) {mystatusChangeCallback(response);});
          };
          function mystatusChangeCallback(response) {
<%
        //si no hay sesion con SWB:
        if (!paramsRequest.getUser().isSigned()) {
            //si hay sesion con FB, terminarla, no iniciarla automagicamente
            String sessionUrl = paramsRequest.getActionUrl().setAction("openSession")
                    .setCallMethod(SWBParamRequest.Call_DIRECT).toString();
%>
            if (response.status && response.status === 'connected') {
              FB.logout();
            }
          }
          function openSWBSession(user, source) {
//            FB.api('/me', function(response) {
//              var name = response.name ? response.name : '';
//              var faceId = response.id ? response.id : '';
//              var email = response.email ? response.email : '';
              var xhttp = new XMLHttpRequest();
              xhttp.onreadystatechange = function() {
                if (this.readyState === 4 && this.status === 200) {
                  location.reload();
                } else {
                    console.log("status del envio: " + this.status);
                }
              };
              xhttp.open("POST", "<%=sessionUrl%>", false); //false = sincrona
              xhttp.setRequestHeader("Content-type", "application/x-www-form-urlencoded;charset=UTF-8");
              xhttp.send("id=" + user.id + "&email=" + user.email + "&name=" + user.name + "&source=" + source);
//            });
          }
          function faceLogin() {
            FB.login(function(response) {
              if (response.authResponse) {
                FB.api('/me', function(response) {
                  var user = {
                      id : response.id ? response.id : '',
                      name : response.name ? response.name : '',
                      email : response.email ? response.email : ''
                  };
                  openSWBSession(user, '<%=SessionInitializer.FACEBOOK%>');
//                  var xhttp = new XMLHttpRequest();
//                  xhttp.onreadystatechange = function() {
//                    if (this.readyState === 4 && this.status === 200) {
//                      location.reload();
//                    } else {
//                        console.log("status del envio: " + this.status);
//                    }
//                  };
//                  xhttp.open("POST", "<%=sessionUrl%>", false); //false = sincrona
//                  xhttp.setRequestHeader("Content-type", "application/x-www-form-urlencoded;charset=UTF-8");
//                  xhttp.send("id=" + faceId + "&email=" + email + "&name=" + name + "&source=<%=SessionInitializer.FACEBOOK%>");
                });
              }
            });
          }
        </script>
        <div class="sesion">
            <button class="btn-sesion" data-toggle="modal" data-target="#modal-sesion"><!-- id="dropdownMenuLink" -->
                <span class="ion-person"></span>
                <i><%=mainLabel%></i>
            </button>
        </div>
<%
            String loginUrl = new StringBuilder(128).append(SWBPlatform.getContextPath())
                    .append("/login/")
                    .append(paramsRequest.getWebPage().getWebSiteId())
                    .append("/")
                    .append(paramsRequest.getWebPage().getId()).toString();
            String dialogTitle;
            try {
                dialogTitle = paramsRequest.getLocaleString("lbl_dialogTitle");
            } catch (SWBResourceException swbe) {
                dialogTitle = "Si tienes una cuenta, inicia ahora";
            }
            String userField;
            try {
                userField = paramsRequest.getLocaleString("lbl_userField");
            } catch (SWBResourceException swbe) {
                userField = "Usuario:";
            }
            String userplaceHldr;
            try {
                userplaceHldr = paramsRequest.getLocaleString("lbl_userplaceHldr");
            } catch (SWBResourceException swbe) {
                userplaceHldr = "nombre de usuario";
            }
            String pswdField;
            try {
                pswdField = paramsRequest.getLocaleString("lbl_pswdField");
            } catch (SWBResourceException swbe) {
                pswdField = "Contraseña:";
            }
            String pwdplaceHldr;
            try {
                pwdplaceHldr = paramsRequest.getLocaleString("lbl_pwdplaceHldr");
            } catch (SWBResourceException swbe) {
                pwdplaceHldr = "********";
            }
            String submitBtn;
            try {
                submitBtn = paramsRequest.getLocaleString("lbl_submitBtn");
            } catch (SWBResourceException swbe) {
                submitBtn = "Iniciar sesión";
            }
            String socialLogin;
            try {
                socialLogin = paramsRequest.getLocaleString("lbl_socialLogin");
            } catch (SWBResourceException swbe) {
                socialLogin = "Inicia con: ";
            }
%>
            <div class="modal fade" id="modal-sesion">
                <div class="modal-dialog">
                    <div class="modal-content">
                        <div class="modal-header">
                            <h3 class="modal-title oswM rojo"><%=dialogTitle%></h3>
                            <button type="button" class="close" data-dismiss="modal">
                                <span class="ion-close-round"></span>
                            </button>
                        </div>
                        <div class="modal-body modal-sesion">
                            <form action="<%=loginUrl%>" method="post">
                                <div class="form-group">
                                    <label for="wb_username" class="rojo"><%=userField%></label>
                                    <input type="text" id="wb_username" class="form-control" name="wb_username" aria-describedby="usernameHelp" placeholder="<%=userplaceHldr%>"/>
                                </div>
                                <div class="form-group">
                                    <label for="wb_password" class="rojo"><%=pswdField%></label>
                                    <input type="password" id="wb_password" name="wb_password" class="form-control" placeholder="<%=pwdplaceHldr%>"/>
                                </div>
<%--                            <div class="form-check">
                                    <input type="checkbox" class="form-check-input" id="dropdownCheck" >
                                    <label class="form-check-label" for="dropdownCheck">Recuérdame</label>
                                </div> --%>
                                <button type="submit" class="btn-cultura btn-negro"><%=submitBtn%></button>
                            </form>
<%--                            <p><a href="#" class="link">Olvidé mi usuario o contraseña</a></p>
                                <hr> --%>
                            <p class="oswM rojo inicia"><%=socialLogin%>
<%--
                      <div class="fb-login-button" data-max-rows="1" 
                     data-size="small" data-button-type="login_with"
                     data-show-faces="false" data-auto-logout-link="false" 
                     data-scope="public_profile,email" 
                     data-use-continue-as="false" onlogin="openSWBSession();"></div>  --%>
                        <%=showFB ? SessionInitializer.getFacebookLink(paramsRequest.getWebPage().getWebSiteId()) : ""%>
                        <%=showTwitter ? SessionInitializer.getTwitterLink(paramsRequest) : ""%>
                        <%-- =showGPlus ? SessionInitializer.getGoogleLink(paramsRequest.getWebPage().getWebSiteId()) : "" --%>
                        <a href="#" id="loginWithGoogle" onclick="javascript:loginGPByClick();"><img src="/work/models/cultura/img/icono-goo.png" /></a>
                            </p>
                        </div>
<%--
//                    <div class="nocuenta rojo-bg">
//                      <h5>¿No tienes una cuenta?</h5>
//                      <p><a href="#" class="btn-cultura btn-negro">Crea aquí tu cuenta</a></p>
//                    </div>  --%>
                    </div>
                </div>
            </div>
<%
        } else {  //si si existe el usuario en sesion de SWB
            WebPage wpCollections = paramsRequest.getWebPage().getWebSite().getWebPage("mis_colecciones");
            if (isSocialNetUser) {
                //revisar que la sesion de la red social este activa tambien
                //esto es parte de la funcion mystatusChangeCallback
                String url = paramsRequest.getActionUrl().setAction("closeSession")
                        .setCallMethod(SWBParamRequest.Call_DIRECT).toString();
                String sessionAlert;
                try {
                    sessionAlert = paramsRequest.getLocaleString("msg_sessionAlert");
                } catch (SWBResourceException swbe) {
                    sessionAlert = "La sesión de Facebook ha terminado. Favor de iniciar sesión de nuevo.";
                }
%>
            var source = "<%=source%>";
            if (source === "<%=SessionInitializer.FACEBOOK%>" && response.status && response.status !== 'connected') {
              //reenviar a seccion con id de usuario de facebook y crear sesion con SWB
              alert('<%=sessionAlert%>');
              closeSWBSession(source);
            }
          }

          function closeSWBSession(source) {
            //var source = "<%=source%>";
            if (source === "<%=SessionInitializer.FACEBOOK%>") {
                try {
                    FB.logout();
                } catch(err) {
                    console.log('Sesion terminada primero en Facebook');
                }
            }
            var xhttp = new XMLHttpRequest();
            xhttp.onreadystatechange = function() {
              if (this.readyState === 4 && this.status === 200) {
                location.reload();
              }
            };
            xhttp.open("GET", "<%=url%>", false); //false = sincrona
            xhttp.send();
<%
            }
            //dar la opcion a terminar la sesion
            try {
                mainLabel = paramsRequest.getLocaleString("lbl_out");
            } catch (SWBResourceException swbe) {
                mainLabel = "Terminar sesión";
            }
            StringBuilder initials = new StringBuilder(2);
            if (!paramsRequest.getUser().getFirstName().isEmpty()) {
                initials.append(paramsRequest.getUser().getFirstName().charAt(0));
            }
            if (!paramsRequest.getUser().getLastName().isEmpty()) {
                initials.append(paramsRequest.getUser().getLastName().charAt(0));
            }
%>
          }
            </script>
            <div class="sesion btn-group" role="group">
                <button id="sesionDrop" type="button" class="btn-sesion btn-rojo" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                    <span><%=initials.toString()%></span>
                </button>
                <div class="dropdown-menu sesiondisplay gris21-bg" aria-labelledby="sesionDrop">
<%
            if (wpCollections != null) {
%>
                    <a class="dropdown-item" href="<%=wpCollections.getWebPageURL(paramsRequest.getUser().getLanguage())%>"><%=wpCollections.getTitle(paramsRequest.getUser().getLanguage())%></a>
<%
            }
            if (isSocialNetUser) {
%>
                    <a class="dropdown-item" href="#" onclick="closeSWBSession();"><%=mainLabel%></a>
<%
            } else {
                String logoutUrl = new StringBuilder()
                        .append(SWBPlatform.getContextPath())
                        .append("/login/")
                        .append(paramsRequest.getWebPage().getWebSiteId())
                        .append("/")
                        .append(paramsRequest.getWebPage().getId()).toString();
%>
                    <a class="dropdown-item" href="<%=logoutUrl%>?wb_logout=true"><%=mainLabel%></a>
<%
            }
%>
                </div>
            </div>
<%        }
%>
