<%-- 
    Document   : userRegistry
    Created on : 30/04/2018, 06:05:32 PM
    Author     : jose.jimenez
--%><%@page import="mx.gob.cultura.portal.resources.UserRegistry"%>
<%@page import="mx.gob.cultura.portal.resources.SessionInitializer"%>
<%@page import="com.hp.hpl.jena.rdf.model.Statement"%>
<%@page import="com.hp.hpl.jena.ontology.OntModel"%>
<%@page import="org.semanticwb.SWBPlatform"%>
<%@page import="org.semanticwb.SWBPortal"%>
<%@page import="org.semanticwb.model.User"%>
<%@page import="org.semanticwb.portal.api.SWBParamRequest, org.semanticwb.portal.api.SWBResourceException, org.semanticwb.portal.api.SWBResourceURL"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<jsp:useBean id="paramsRequest" scope="request" type="org.semanticwb.portal.api.SWBParamRequest" />
<%
    //String swbAction = paramsRequest.getUser().isSigned() ? "editing" : "creating";
    User user = paramsRequest.getUser();
    SWBResourceURL url = paramsRequest.getActionUrl();
    SWBResourceURL url_actPhoto = paramsRequest.getActionUrl().setCallMethod(SWBResourceURL.Call_DIRECT).setAction(UserRegistry.ACTION_UPLOAD_PHOTO);
    
    boolean editMode;  
    boolean isAnnotator = (boolean)request.getAttribute("isAnnotator");
    boolean toBeAnnotator =false;
    boolean canChangePwd = user.getLogin().startsWith(SessionInitializer.FACEBOOK) || 
            user.getLogin().startsWith(SessionInitializer.GOOGLEP) ||
            user.getLogin().startsWith(SessionInitializer.TWITTER) ? false : true;
    
    if (user.isSigned()){
        url.setAction(SWBParamRequest.Action_EDIT);
        editMode=true;
    }else{
        //url.setAction(SWBParamRequest.Action_ADD);
        url.setAction(UserRegistry.ACTION_REGISTER);
        editMode=false;
    }
    if(request.getParameter(UserRegistry.ACTION_BE_ANNOTATOR)!=null){
        toBeAnnotator=true;        
    }
    //String btnChngImage;
    String errorCode = null;
    String errorMsg = null;
    if (null != request.getParameter("condition") && !request.getParameter("condition").isEmpty()) {
        errorCode = request.getParameter("condition");
        if ("msgSent".equals(errorCode)){
            errorMsg = paramsRequest.getLocaleString("msgSent");
        }else{
            errorMsg = paramsRequest.getLocaleString(errorCode);
        }    
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
<%
    if (canChangePwd) {
        if (editMode) {
%>            
                if (form.pass2 && form.pass2.value !== form.passConf.value) {
                    alert("La contraseña no coincide con la confirmación.");
                    retValue = false;
                }
<%
        } else {
%>  
                if (form.pass2.value !== form.passConf.value) {
                    alert("La contraseña no coincide con la confirmación.");
                    retValue = false;
                }            
<%
        }
    }
%>            
                if (!isEmailValid(form.email.value)) {
                    alert("La cuenta de correo proporcionada no es válida.");
                    retValue = false;
                }
                if (form.usrFirstName.value.trim() === "") {
                    alert("El nombre de usuario no es válido");
                    retValue = false;
                }
                if (form.usrLastName.value.trim() === "") {
                    alert("El nombre de usuario no es válido");
                    retValue = false;
                }
            } else if (!document.forms["photoUpload"].photo) {
                alert("Revisa tu información por favor: proporciona un nombre, cuenta de correo válida y contraseña con su confirmación.");
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
        
        var uploads_in_progress = 0;

        function beginAsyncUpload(ul, sid) {
            ul.form.submit();
            uploads_in_progress = uploads_in_progress + 1;
            var pb = document.getElementById(ul.name + "_progress");
            pb.parentNode.style.display = 'block';
            document.getElementById("fileNameDisplay").innerHTML = "Archivo a subir: " + ul.value;
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
<%
    OntModel ont = SWBPlatform.getSemanticMgr().getSchema().getRDFOntModel();
    Statement tmpStat = null;
    String tmpValue = user.getPhoto();
//    if (tmpStat != null) {
//        tmpValue = tmpStat.getString();
//    } else {
//        tmpValue = "";
//    }
    if (null != tmpValue && !"".equalsIgnoreCase(tmpValue)) {
%>
            <img src="<%=SWBPortal.getWebWorkPath() + tmpValue%>" class="circle">
<%
    }
    if (editMode) {
%>
            <form id="photoUpload" name="photoUpload" enctype="multipart/form-data" action="<%=url_actPhoto%>" method="post" target="pictureTransferFrame">
                <iframe id="pictureTransferFrame" name="pictureTransferFrame" src="" style="display:none" ></iframe>
                <input type="file" name="photo" onchange="beginAsyncUpload(this, 'photo');" style="display: none;" />
                <div class="progresscontainer" style="display: none;">
                    <div class="progressbar" id="photo_progress"></div>
                </div>
            </form>
            <button class="btn-cultura btn-blanco" onclick="document.forms['photoUpload'].photo.click()"><%=paramsRequest.getLocaleString("btn_chngImage")%></button>
            <div id="fileNameDisplay" class="fileName"></div>
<%
    }
%>
        </div>
        <div class="col-12 col-md-7 editarPerfil-02">
            <h3 class="oswM uppercase"><%=user.isSigned()?paramsRequest.getLocaleString("lbl_titleEdit"):paramsRequest.getLocaleString("lbl_titleCreate")%></h3>
            <form id="regUser" method="post" action="<%=url.toString()%>"><!--  onsubmit="validate(this);" -->
                <%-- =user.getLogin()!=null?user.getLogin():"---" --%>
                <div class="form-group">
                    <label for="usrFirstName"><%=paramsRequest.getLocaleString("lbl_userName")%></label>
                    <input type="text" class="form-control" id="usrFirstName" name="usrFirstName" placeholder="<%=paramsRequest.getLocaleString("lbl_placeHldrName")%>" required="required" pattern="[a-zA-Z\u00C0-\u00FF' ]+"  value="<%=user.getFirstName()!=null?user.getFirstName():""%>">
                </div>
                <div class="form-group">
                    <label for="usrLastName"><%=paramsRequest.getLocaleString("lbl_userLastName")%></label>
                    <input type="text" class="form-control" id="usrLastName" name="usrLastName" placeholder="<%=paramsRequest.getLocaleString("lbl_placeHldrLastName")%>" required="required" pattern="[a-zA-Z\u00C0-\u00FF' ]+" value="<%=user.getLastName()!=null?user.getLastName():""%>">
                </div>
                <div class="form-group">
                    <label for="email"><%=paramsRequest.getLocaleString("lbl_eMail")%></label>
                    <input type="email" class="form-control" id="email" name="email" placeholder="usuario@servidor.com" required="required" value="<%=user.getEmail()!=null?user.getEmail():""%>">
                </div>
<%
                if(isAnnotator || toBeAnnotator){
%>                   
                <%
                tmpStat=user.getSemanticObject().getRDFResource().getProperty(ont.createDatatypeProperty(UserRegistry.ORGANITATION_NAME_URI));
                if (tmpStat!=null){
                    tmpValue=tmpStat.getString();
                }else{
                    tmpValue="";
                }                     
                %>                  
                <div class="form-group">
                    <label for="usrOrg"><%=paramsRequest.getLocaleString("lbl_userOrg")%></label>
                    <input type="text" class="form-control" id="usrOrg" name="usrOrg" placeholder="<%=paramsRequest.getLocaleString("lbl_placeHldrOrg")%>" pattern="[a-zA-Z0-9\u00C0-\u00FF'. ]+" required="required" value="<%=tmpValue%>">
                </div>
                <%
                tmpStat=user.getSemanticObject().getRDFResource().getProperty(ont.createDatatypeProperty(UserRegistry.POSITION_TITLE_URI));
                if (tmpStat!=null){
                    tmpValue=tmpStat.getString();
                }else{
                    tmpValue="";
                }                     
                %>                 
                <div class="form-group">
                    <label for="usrTitle"><%=paramsRequest.getLocaleString("lbl_userTitle")%></label>
                    <input type="text" class="form-control" id="usrTitle" name="usrTitle" placeholder="<%=paramsRequest.getLocaleString("lbl_placeHldrTitle")%>" pattern="[a-zA-Z0-9\u00C0-\u00FF'. ]+" required="required" value="<%=tmpValue%>">
                </div>               
                <%
                tmpStat=user.getSemanticObject().getRDFResource().getProperty(ont.createDatatypeProperty(UserRegistry.AREA_INTEREST_URI));
                if (tmpStat!=null){
                    tmpValue=tmpStat.getString();
                }else{
                    tmpValue="";
                }                     
                %>                 
                <div class="form-group">
                    <label for="usrInter"><%=paramsRequest.getLocaleString("lbl_userInter")%></label>
                    <input type="text" class="form-control" id="usrInter" name="usrInter" placeholder="<%=paramsRequest.getLocaleString("lbl_placeHldrInter")%>" pattern="[a-zA-Z0-9\u00C0-\u00FF', ]+" required="required" value="<%=tmpValue%>">
                </div>               
                <%
                tmpStat=user.getSemanticObject().getRDFResource().getProperty(ont.createDatatypeProperty(UserRegistry.TELEPHONE_NUMBER_URI));
                if (tmpStat!=null){
                    tmpValue=tmpStat.getString();
                }else{
                    tmpValue="";
                }                     
                %>                 
                <div class="form-group">
                    <label for="usrTel"><%=paramsRequest.getLocaleString("lbl_userTel")%></label>
                    <input type="text" class="form-control" id="usrTel" name="usrTel" placeholder="<%=paramsRequest.getLocaleString("lbl_placeHldrTel")%>" maxlength="10"  pattern="\d{10}" required="required" value="<%=tmpValue%>">
                </div>  
<%
                }
%>                 
<%
        if (canChangePwd) {
                if (editMode) {
%>                 <h4 class="rojo">Cambiar contraseña</h4>
<%
                }
%>                 
                <!--div class="form-group">
                    <label for="pass1">Contraseña actual</label>
                    <input type="pass1" class="form-control" id="pass1" aria-describedby="emailHelp" placeholder="">
                </div-->
                <div class="form-group">
                    <label for="pass2"><%=user.isSigned()?paramsRequest.getLocaleString("lbl_newPwd"):paramsRequest.getLocaleString("lbl_pwd")%></label>
                    <input type="password" class="form-control" id="pass2" name="pass2" <%=!editMode?"required=\"required\"":""%>>
                </div>
                <div class="form-group">
                      <label for="passConf"><%=user.isSigned()?paramsRequest.getLocaleString("lbl_newPwdConf"):paramsRequest.getLocaleString("lbl_pwdConf")%></label>
                      <input type="password" class="form-control" id="passConf" name="passConf" <%=!editMode?"required=\"required\"":""%>>
                </div>                
                <div class="form-check">
                    <input type="checkbox" class="" id="checar" onclick="showData(this);">
                    <label class="form-check-label" for="checar"><%=paramsRequest.getLocaleString("lbl_show")%></label>
                </div>
                  <!--h4 class="rojo">Privacidad</h4>
                  <div class="form-check">
                        <input type="checkbox" class="" id="privado">
                        <label class="privado" for="privado"><span class="ion-locked"></span> Perfil Privado</label>
                  </div-->
<%
        }
                if(!editMode){
%>        
                <div class="form-check">
                      <input type="checkbox" class="" id="termsPrivacy"  name="termsPrivacy" value="true" required="required" >
                      <label class="" for="termsPrivacy"><%=paramsRequest.getLocaleString("lbl_termsPrivacy")%></label>
                </div>
<%
                }
%>    
                <div class="espacio"></div>
<%
                if(toBeAnnotator){
%>                 <input type="hidden" name="<%=UserRegistry.ACTION_BE_ANNOTATOR%>" value="true"> 
<%
                }
%> 
                <button type="submit" class="btn-cultura btn-rojo" name="btnSubmit" onclick="validate(this.form); return false;"><%=paramsRequest.getLocaleString("btn_save")%></button>
                <!--button type="button" class="btn-cultura btn-blanco">Desactivar cuenta</button-->
            </form>
        </div>
    </div>
</div>
