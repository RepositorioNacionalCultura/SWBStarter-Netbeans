<%-- 
    Document   : sessionInitializerMenu
                Presenta la interface para que el usuario escoja el medio de autenticacion de su preferencia.
    Created on : 13/02/2018, 05:43:59 PM
    Author     : jose.jimenez
--%><%@ page contentType="text/html; charset=ISO-8859-1" pageEncoding="UTF-8"%>
<%@page import="org.semanticwb.portal.api.SWBParamRequest, org.semanticwb.SWBPlatform, org.semanticwb.SWBPortal, org.semanticwb.model.WebPage"%>
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
    java.security.KeyPair key = SWBPortal.getUserMgr().getSessionKey(request);
    
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
    
    if (paramsRequest.getUser().isSigned()) {
        if (null != request.getSession(false) &&
                request.getSession(false).getAttribute("isSocialNetUser") != null) {
            isSocialNetUser = Boolean.parseBoolean(
                    (String) request.getSession(false)
                            .getAttribute("isSocialNetUser"));
            source = isSocialNetUser ? (String) request.getSession(false).getAttribute("source") : "";
        }
    } else {
        if (SWBPlatform.getSecValues().isEncrypt()) {
%>
        <script language="JavaScript" type="text/javascript" src="/swbadmin/js/crypto/jsbn.js"></script>
        <script language="JavaScript" type="text/javascript" src="/swbadmin/js/crypto/prng4.js"></script>
        <script language="JavaScript" type="text/javascript" src="/swbadmin/js/crypto/rng.js"></script>
        <script language="JavaScript" type="text/javascript" src="/swbadmin/js/crypto/rsa.js"></script>
<%
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
            WebPage wpRegistry = paramsRequest.getWebPage().getWebSite().getWebPage("Registro");
%>
            if (response.status && response.status === 'connected') {
              FB.logout();
            }
          }
          function openSWBSession(user, source) {
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
                });
              }
            });
          }
          var userRegistered = false;
          window.originalContent = "";
          
          function checkIfUsed() {
            if (userRegistered) {
                document.getElementsByClassName("modal-content")[0].innerHTML = window.originalContent;
                userRegistered = false;
            }
          }
        </script>
        <div class="sesion">
            <button class="btn-sesion" data-toggle="modal" data-target="#modal-sesion" onclick="javascript:checkIfUsed();">
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
                dialogTitle = "Inicia sesión con tu cuenta";
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
                userplaceHldr = "Ingresa tu correo elctrónico";
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
                pwdplaceHldr = "Ingresa tu contraseña";
            }
            String submitBtn;
            try {
                submitBtn = paramsRequest.getLocaleString("lbl_submitBtn");
            } catch (SWBResourceException swbe) {
                submitBtn = "Iniciar";
            }
            String socialLogin;
            try {
                socialLogin = paramsRequest.getLocaleString("lbl_socialLogin");
            } catch (SWBResourceException swbe) {
                socialLogin = "Inicia con: ";
            }
            String userAccountTxt;
            try {
                userAccountTxt = paramsRequest.getLocaleString("lbl_userAccount");
            } catch (SWBResourceException swbe) {
                userAccountTxt =  "¿No tienes cuenta?";
            }
            String createAccountTxt;
            try {
                createAccountTxt = paramsRequest.getLocaleString("lbl_createAccount");
            } catch (SWBResourceException swbe) {
                createAccountTxt = "Regístrate aquí";
            }
            String mailPlaceHldrTxt;
            try {
                mailPlaceHldrTxt = paramsRequest.getLocaleString("lbl_mailPlaceHldr");
            } catch (SWBResourceException swbe) {
                mailPlaceHldrTxt = "Ingresa tu correo electrónico";
            }
            String pwdPlaceHldrTxt;
            try {
                pwdPlaceHldrTxt = paramsRequest.getLocaleString("lbl_passwrdPlaceHldr");
            } catch (SWBResourceException swbe) {
                pwdPlaceHldrTxt = "Crea tu contraseña";
            }
            String pwdConfPlaceHldrTxt;
            try {
                pwdConfPlaceHldrTxt = paramsRequest.getLocaleString("lbl_passwrdConfPlaceHldr");
            } catch (SWBResourceException swbe) {
                pwdConfPlaceHldrTxt = "Repite tu contraseña";
            }
            String useTermsTxt;
            try {
                useTermsTxt = paramsRequest.getLocaleString("lbl_useTerms");
            } catch (SWBResourceException swbe) {
                useTermsTxt = "términos de uso y la política de privacidad";
            }
            String useTermsAcceptTxt;
            try {
                useTermsAcceptTxt = paramsRequest.getLocaleString("lbl_useTermsAcceptText");
            } catch (SWBResourceException swbe) {
                useTermsAcceptTxt = "Acepto los ";
            }
            String registryBtnTxt;
            try {
                registryBtnTxt = paramsRequest.getLocaleString("lbl_registryBtn");
            } catch (SWBResourceException swbe) {
                registryBtnTxt = "Continuar";
            }
            String url2RegisterUser = SessionInitializer.getRegisterDirectLink(paramsRequest);
            
            String pswdMismatchTxt;
            try {
                pswdMismatchTxt = paramsRequest.getLocaleString("msg_pswdMismatch");
            } catch (SWBResourceException swbe) {
                pswdMismatchTxt = "La contraseña no coincide con la confirmación.";
            }
            String emailNotValidTxt;
            try {
                emailNotValidTxt = paramsRequest.getLocaleString("msg_emailNotValid");
            } catch (SWBResourceException swbe) {
                emailNotValidTxt = "La cuenta de correo proporcionada no es válida.";
            }
            String allValidationsTxt;
            try {
                allValidationsTxt = paramsRequest.getLocaleString("msg_allValidations");
            } catch (SWBResourceException swbe) {
                allValidationsTxt = "Favor de proporcionar un nombre, cuenta de correo válida y contraseña con su confirmación.";
            }
            String userRegisteredTxt;
            try {
                userRegisteredTxt = paramsRequest.getLocaleString("lbl_userRegistered");
            } catch (SWBResourceException swbe) {
                userRegisteredTxt = "¿Ya tienes una cuenta?";
            }
            String loginAltTxt;
            try {
                loginAltTxt = paramsRequest.getLocaleString("lbl_loginAlt");
            } catch (SWBResourceException swbe) {
                loginAltTxt = "Inicia sesión aquí";
            }
            String registryModalTitleTxt;
            try {
                registryModalTitleTxt = paramsRequest.getLocaleString("lbl_registryTitle");
            } catch (SWBResourceException swbe) {
                registryModalTitleTxt = "Regístrate";
            }
            String termsPolicyCheckedTxt;
            try {
                termsPolicyCheckedTxt = paramsRequest.getLocaleString("msg_registryTitle");
            } catch (SWBResourceException swbe) {
                termsPolicyCheckedTxt = "Los términos de uso y políticas de privacidad, deben ser aceptados.";
            }
            String registryProblemTxt;
            try {
                registryProblemTxt = paramsRequest.getLocaleString("msg_registryProblem");
            } catch (SWBResourceException swbe) {
                registryProblemTxt = "Hubo un problema al registrarte:";
            }
            String regCatastropheTxt;
            try {
                regCatastropheTxt = paramsRequest.getLocaleString("msg_regCatastrophe");
            } catch (SWBResourceException swbe) {
                regCatastropheTxt = "El servidor no pudo crear el registro. :(";
            }
            String titleRegSuccessTxt;
            try {
                titleRegSuccessTxt = paramsRequest.getLocaleString("lbl_titleRegSuccess");
            } catch (SWBResourceException swbe) {
                titleRegSuccessTxt = "¡Gracias!";
            }
            String registryCont1Txt;
            try {
                registryCont1Txt = paramsRequest.getLocaleString("lbl_registryCont1");
            } catch (SWBResourceException swbe) {
                registryCont1Txt = "Te hemos enviado un correo de verificación";
            }
            String registryCont2Txt;
            try {
                registryCont2Txt = paramsRequest.getLocaleString("lbl_registryCont2");
            } catch (SWBResourceException swbe) {
                registryCont2Txt = "Por favor revisa tu correo y accede al link para finalizar tu registro.";
            }
            String registryCont3Txt;
            try {
                registryCont3Txt = paramsRequest.getLocaleString("lbl_registryCont3");
            } catch (SWBResourceException swbe) {
                registryCont3Txt = "Si no has recibido nuestro correo de confirmación, te pedimos revisar en la carpeta de spam o ";
            }
            String contactLinkTxt;
            try {
                contactLinkTxt = paramsRequest.getLocaleString("lbl_contactLink");
            } catch (SWBResourceException swbe) {
                contactLinkTxt = "ponte en contacto con nosotros.";
            }
            String regCloseBtnTxt;
            try {
                regCloseBtnTxt = paramsRequest.getLocaleString("lbl_regCloseBtn");
            } catch (SWBResourceException swbe) {
                regCloseBtnTxt = "Cerrar";
            }

%>
            <div class="modal fade" id="modal-sesion" tabindex="-1" role="dialog" aria-labelledby="modalTitle" aria-hidden="true">
                <div class="modal-dialog modal-exh" role="document">
                    <div class="modal-content">
                        <div class="modal-header">
                            <h3 class="modal-title oswM"><%=dialogTitle%></h3>
                            <button type="button" class="close" data-dismiss="modal">
                                <span class="ion-ios-close-outline"></span>
                            </button>
                        </div>
                        <div class="modal-body modal-sesion">
                            <div id="modalFormContainer">
                                <form name="loginForm" action="<%=loginUrl%>" method="post">
                                    <div class="form-group form-ses-01">
                                        <span class="ion-android-mail"></span>
                                        <input type="email" class="form-control" id="wb_username" name="wb_username" aria-describedby="emailHelp" placeholder="<%=userplaceHldr%>">
                                    </div>
                                    <div class="form-group form-ses-02">
                                        <span class="ion-android-lock"></span>
                                        <input type="password" class="form-control" id="wb_password" name="wb_password" placeholder="<%=pwdplaceHldr%>">
                                    </div>
                                    <div class="form-ses-03">
                                        <button type="submit" class="btn-cultura btn-rojo"><%=submitBtn%></button>
                                        <%--a href="#" class="modal-olvidaste">¿Olvidaste tu contraseña?</a--%>
                                    </div>
                                </form>
                            </div>
<%
            if (showFB) {
%>
                                <div class="iniciaFace">
                                    <%=SessionInitializer.getFacebookLink(paramsRequest)%>
                                </div>
<%          }
            if (showTwitter) {
%>
                                <div class="iniciaTwit">
                                    <%=SessionInitializer.getTwitterLink(paramsRequest)%>
                                </div>
<%
            }
%>
                            <div id="modalFooter">
                                <p class="oswM"><%=userAccountTxt%> <a href="#" onclick="javascript:changeModal(1);" class="rojo"><%=createAccountTxt%></a></p>
                            </div>
                        </div>
<%--
                        <%=showGPlus ? SessionInitializer.getGoogleLink(paramsRequest.getWebPage().getWebSiteId()) : ""%>
--%>
                    </div>
                </div>
<%
            if (SWBPlatform.getSecValues().isEncrypt()) {
%>
        <script>
            var rsa = new RSAKey();
            rsa.setPublic("<%=((java.security.interfaces.RSAPublicKey)key.getPublic()).getModulus().toString(16)%>", "<%=((java.security.interfaces.RSAPublicKey)key.getPublic()).getPublicExponent().toString(16)%>");
            function encrypt() {
                var value = false;
                var res = rsa.encrypt(document.loginForm.wb_password.value);
                if (res) { document.loginForm.wb_password.value = res; value = true; }
                return value;
            }
            document.loginForm.onsubmit = encrypt;
        </script>
<%
            }
%>
            </div>
            <script type="text/javascript">
                window.originalContent = document.getElementsByClassName("modal-content")[0].innerHTML;
                
                function isEmailValid(val) {
                    var filter = /^([a-zA-Z0-9\_\-\.])+@(([a-zA-Z0-9\-\_])+\.)+([a-zA-Z0-9]{2,4})$/;
                    var ret = true;
                    if (val === "" || !filter.test(val)) {
                        ret = false;
                    }
                    return ret;
                }

                function validate(form) {
                    var retValue = true;
                    if (form.checkValidity()) {

                        if (form.pass2.value !== form.passConf.value) {
                            alert("<%=pswdMismatchTxt%>");
                            retValue = false;
                        }
                        if (!isEmailValid(form.email.value)) {
                            alert("<%=emailNotValidTxt%>")
                            retValue = false;
                        }
                        if (!form.termsPrivacy.checked) {
                            alert("<%=termsPolicyCheckedTxt%>");
                            retValue = false;
                        }
                    } else {
                        alert("<%=allValidationsTxt%>");
                        retValue = false;
                    }

                    if (retValue) {
                        var data = "email=" + form.email.value + "&pass2=" + form.pass2.value +
                                "&passConf=" + form.passConf.value + "&termsPrivacy=" + form.termsPrivacy.value;
                        var xhttp = new XMLHttpRequest();
                        xhttp.onreadystatechange = function() {
                          if (this.readyState === 4 && this.status === 200) {                              
                              if (this.responseText === "msgSent") {
                                changeModal(3);
                              } else {
                                var result = this.responseText;
                                form.reset();
                                alert("<%=registryProblemTxt%> " + result);
                              }
                          } else if (this.readyState === 4 && this.status === 500) {
                              alert("<%=regCatastropheTxt%>");
                              form.reset();
                          }
                        };
                        xhttp.open("POST", form.action, true);
                        xhttp.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
                        xhttp.send(encodeURI(data));
                    
                        //form.submit();
                    }
                }
                var newContent1 = "<form id=\"regUser\" method=\"post\" action=\"<%=url2RegisterUser%>\">" +
                                "<div class=\"form-group form-reg-01\">" +
                                "<span class=\"ion-android-mail\"></span>" +
                                "<input type=\"email\" class=\"form-control\" id=\"email\" name=\"email\" aria-describedby=\"emailHelp\" placeholder=\"<%=mailPlaceHldrTxt%>\" required=\"required\">" +
                                "</div>" +
                                "<div class=\"form-group form-reg-02\">" +
                                "<span class=\"ion-android-lock\"></span>" +
                                "<input type=\"password\" class=\"form-control\" id=\"pass2\" name=\"pass2\" placeholder=\"<%=pwdPlaceHldrTxt%>\" required=\"required\">" +
                                "</div>" +
                                "<div class=\"form-group form-reg-0\">" +
                                "<span class=\"ion-android-lock\"></span>" +
                                "<input type=\"password\" class=\"form-control\" id=\"passConf\" name=\"passConf\" placeholder=\"<%=pwdConfPlaceHldrTxt%>\" required=\"required\">" +
                                "</div>" +
                                "<div class=\"form-group form-check form-reg-04\">" +
                                "<input type=\"checkbox\" class=\"form-check-input\" id=\"termsPrivacy\" name=\"termsPrivacy\" value=\"true\">" +
                                "<label class=\"form-check-label\" for=\"termsPrivacy\"><%=useTermsAcceptTxt%> <a href=\"#\" target=\"_blank\"><%=useTermsTxt%></a></label>" +
                                "<button type=\"button\" class=\"btn-cultura btn-rojo\" onclick=\"validate(this.form);\"><%=registryBtnTxt%></button>" +
                                "</div>" +
                                "</form>";
                var newContent2 = "<p class=\"oswM\"><%=userRegisteredTxt%> <a href=\"#\" onclick=\"javascript:changeModal(2);\" class=\"rojo\"><%=loginAltTxt%></a></p>";
                var newContent3 = "<%=registryModalTitleTxt%>";
                var otherContent = "<form name=\"loginForm\" action=\"<%=loginUrl%>\" method=\"post\">" +
                            "<div class=\"form-group form-ses-01\">" +
                            "<span class=\"ion-android-mail\"></span>" +
                            "<input type=\"email\" class=\"form-control\" id=\"wb_username\" name=\"wb_username\" aria-describedby=\"emailHelp\" placeholder=\"<%=userplaceHldr%>\">" +
                            "</div>" +
                            "<div class=\"form-group form-ses-02\">" +
                            "<span class=\"ion-android-lock\"></span>" +
                            "<input type=\"password\" class=\"form-control\" id=\"wb_password\" name=\"wb_password\" placeholder=\"<%=pwdplaceHldr%>\">" +
                            "</div>" +
                            "<div class=\"form-ses-03\">" +
                            "<button type=\"submit\" class=\"btn-cultura btn-rojo\"><%=submitBtn%></button>" +
                            "</div>" +
                            "</form>";
                var otherContent2 = "<p class=\"oswM\"><%=userAccountTxt%> <a href=\"#\" onclick=\"javascript:changeModal(1);\" class=\"rojo\"><%=createAccountTxt%></a></p>";
                var otherContent3 = "<%=dialogTitle%>";
                
                function changeModal(interface2Show) {
                    var container = document.getElementById("modalFormContainer");
                    var container2 = document.getElementById("modalFooter");
                    var container3 = document.getElementsByClassName("modal-title oswM");
                    if (container !== undefined && container2 !== undefined && container3.length > 0) {
                        if (interface2Show === 1) {
                            container3[0].innerHTML = newContent3;
                            container.innerHTML = newContent1;
                            container2.innerHTML = newContent2;
                        } else if (interface2Show === 2) {
                            container3[0].innerHTML = otherContent3;
                            container.innerHTML = otherContent;
                            container2.innerHTML = otherContent2;
                        } else if (interface2Show === 3) {
                            container = document.getElementsByClassName("modal-body modal-sesion");
                            container3[0].classList.add("center");
                            container3[0].innerHTML = "<%=titleRegSuccessTxt%>";
                            var greetings = "<p class=\"center rojo bold\"><%=registryCont1Txt%></p>" + 
                                    "<p class=\"center\"><%=registryCont2Txt%></p>" +
                                    "<p class=\"center\"><%=registryCont3Txt%>" +
                                    "<a href=\"#\" class=\"rojo\"><%=contactLinkTxt%></a></p>" +
                                    "<button type=\"submit\" class=\"btn-cultura btn-rojo\" data-dismiss=\"modal\"><%=regCloseBtnTxt%></button>";
                            container[0].innerHTML = greetings;
                            userRegistered = true;
                        }
                    }
                }
            </script>
<%
        } else {  //si si existe el usuario en sesion de SWB
            WebPage wpCollections = paramsRequest.getWebPage().getWebSite().getWebPage("miscolecciones");
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
                mainLabel = "Cerrar sesión";
            }
            StringBuilder initials = new StringBuilder(2);
            if (paramsRequest.getUser().getFirstName()!=null && !paramsRequest.getUser().getFirstName().isEmpty()) {
                initials.append(paramsRequest.getUser().getFirstName().charAt(0));
            }
            if (paramsRequest.getUser().getLastName()!=null && !paramsRequest.getUser().getLastName().isEmpty()) {
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
                    <a class="dropdown-item" href="<%=wpCollections.getRealUrl(paramsRequest.getUser().getLanguage())%>"><%=wpCollections.getTitle(paramsRequest.getUser().getLanguage())%></a>
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
