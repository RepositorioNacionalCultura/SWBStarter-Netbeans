<%-- 
    Document   : userRegistry
    Created on : 30/04/2018, 06:05:32 PM
    Author     : jose.jimenez
--%><%@page contentType="text/html; charset=ISO-8859-1" pageEncoding="UTF-8"%>
<%@page import="org.semanticwb.portal.api.SWBParamRequest, org.semanticwb.portal.api.SWBResourceException, org.semanticwb.portal.api.SWBResourceURL"%>
<%
    SWBParamRequest paramsRequest = (SWBParamRequest) request.getAttribute("paramsRequest");
    String swbAction = paramsRequest.getUser().isSigned() ? "editing" : "creating";
    SWBResourceURL url = paramsRequest.getActionUrl().setAction(swbAction);
    String btnChngImage;
    String errorMsg = null;
    
    try {
        btnChngImage = paramsRequest.getLocaleString("btn_chngImage");
    } catch (SWBResourceException swbe) {
        btnChngImage = "CAMBIAR IMAGEN";
    }
    String txtTitle;
    try {
        if (paramsRequest.getUser().isSigned()) {
            txtTitle = paramsRequest.getLocaleString("lbl_titleEdit");
        } else {
            txtTitle = paramsRequest.getLocaleString("lbl_titleCreate");
        }
    } catch (SWBResourceException swbe) {
        if (paramsRequest.getUser().isSigned()) {
            txtTitle = "Editar perfil";
        } else {
            txtTitle = "Crear perfil";
        }
    }
    String lblUserName;
    try {
        lblUserName = paramsRequest.getLocaleString("lbl_userName");
    } catch (SWBResourceException swbe) {
        lblUserName = "Nombre del Usuario";
    }
    String lblUserLastName;
    try {
        lblUserLastName = paramsRequest.getLocaleString("lbl_userLastName");
    } catch (SWBResourceException swbe) {
        lblUserLastName = "Apellido del Usuario";
    }
    String lblPlchldrName;
    try {
        lblPlchldrName = paramsRequest.getLocaleString("lbl_placeHldrName");
    } catch (SWBResourceException swbe) {
        lblPlchldrName = "Nombre(s)";
    }
    String lblPlchldrLastName;
    try {
        lblPlchldrLastName = paramsRequest.getLocaleString("lbl_placeHldrLastName");
    } catch (SWBResourceException swbe) {
        lblPlchldrLastName = "Apellido(s)";
    }
    String lblEMail;
    try {
        lblEMail = paramsRequest.getLocaleString("lbl_eMail");
    } catch (SWBResourceException swbe) {
        lblEMail = "Correo electrónico";
    }
    String lblPassword;
    try {
        if (paramsRequest.getUser().isSigned()) {
            lblPassword = paramsRequest.getLocaleString("lbl_newPwd");
        } else {
            lblPassword = paramsRequest.getLocaleString("lbl_pwd");
        }
    } catch (SWBResourceException swbe) {
        if (paramsRequest.getUser().isSigned()) {
            lblPassword = "Contraseña nueva";
        } else {
            lblPassword = "Contraseña";
        }
    }
    String lblPasswordConf;
    try {
        if (paramsRequest.getUser().isSigned()) {
            lblPasswordConf = paramsRequest.getLocaleString("lbl_newPwdConf");
        } else {
            lblPasswordConf = paramsRequest.getLocaleString("lbl_pwdConf");
        }
    } catch (SWBResourceException swbe) {
        if (paramsRequest.getUser().isSigned()) {
            lblPasswordConf = "Confirmar contraseña nueva";
        } else {
            lblPasswordConf = "Confirmar contraseña";
        }
    }
    String lblShow;
    try {
        lblShow = paramsRequest.getLocaleString("lbl_show");
    } catch (SWBResourceException swbe) {
        lblShow = "Mostrar";
    }
    String btnSave;
    try {
        btnSave = paramsRequest.getLocaleString("btn_save");
    } catch (SWBResourceException swbe) {
        btnSave = "Guardar";
    }
    
    if (null != request.getParameter("condition") && !request.getParameter("condition").isEmpty()) {
        errorMsg = paramsRequest.getLocaleString(request.getParameter("condition"));
    }
%>
<div class="container editarPerfil">
    <script type="text/javascript">
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
                    alert("La contraseña no coincide con la confirmación.");
                    retValue = false;
                }
                if (!isEmailValid(form.email.value)) {
                    alert("La cuenta de correo proporcionada no es válida.")
                    retValue = false;
                }
                if (form.usrname.value.trim() === "") {
                    alert("El nombre de usuario no es válido");
                    retValue = false;
                }
            } else {
                alert("Favor de proporcionar un nombre, cuenta de correo válida y contraseña con su confirmación.");
                retValue = false;
            }
            
            if (retValue) {
                form.submit();
            }
        }
        
        function showData(check) {
            if (check.checked) {
                check.form.pass2.type = "text";
                check.form.passConf.type = "text"
            } else {
                check.form.pass2.type = "password";
                check.form.passConf.type = "password"
            }
        }
    </script>
    <div class="row">
<%
    if (null != errorMsg) {
%>
        <div class="col-12 col-md-12 offset-1 editarPerfil-01">
            <span><%=errorMsg%></span>
        </div>
<%
    }
%>
        <div class="col-12 col-md-4 offset-1 editarPerfil-01">
            <!--img src="img/agregado-07.jpg" class="circle">
            <input type="file" name="photo" -->
            <!--button class="btn-cultura btn-blanco">< % =btnChngImage% ></button-->
        </div>
        <div class="col-12 col-md-7 editarPerfil-02">
            <h3 class="oswM uppercase"><%=txtTitle%></h3>
            <form id="regUser" method="post" action="<%=url.toString()%>"><!--  onsubmit="validate(this);" -->
                <div class="form-group">
                    <label for="usrname"><%=lblUserName%></label>
                    <input type="text" class="form-control" id="usrname" name="usrname" placeholder="<%=lblPlchldrName%>" required="required">
                </div>
                <div class="form-group">
                    <label for="usrLastName"><%=lblUserLastName%></label>
                    <input type="text" class="form-control" id="usrLastName" name="usrLastName" placeholder="<%=lblPlchldrLastName%>">
                </div>
                <div class="form-group">
                    <label for="email"><%=lblEMail%></label>
                    <input type="email" class="form-control" id="email" name="email" placeholder="usuario@servidor.mx" required="required">
                </div>
                <!--h4 class="rojo">Cambiar contraseña</h4>
                <div class="form-group">
                    <label for="pass1">Contraseña actual</label>
                    <input type="pass1" class="form-control" id="pass1" aria-describedby="emailHelp" placeholder="">
                </div-->
                <div class="form-group">
                    <label for="pass2"><%=lblPassword%></label>
                    <input type="password" class="form-control" id="pass2" name="pass2" required="required">
                </div>
                <div class="form-group">
                      <label for="passConf"><%=lblPasswordConf%></label>
                      <input type="password" class="form-control" id="passConf" name="passConf" required="required">
                </div>
                <div class="form-check">
                    <input type="checkbox" class="" id="checar" onclick="showData(this);">
                    <label class="form-check-label" for="checar"><%=lblShow%></label>
                </div>
                  <!--h4 class="rojo">Privacidad</h4>
                  <div class="form-check">
                        <input type="checkbox" class="" id="privado">
                        <label class="privado" for="privado"><span class="ion-locked"></span> Perfil Privado</label>
                  </div-->
                <div class="espacio"></div>
                <button type="button" class="btn-cultura btn-rojo" name="btnSubmit" onclick="validate(this.form);"><%=btnSave%></button>
                <!--button type="button" class="btn-cultura btn-blanco">Desactivar cuenta</button-->
            </form>
        </div>
    </div>
</div>
