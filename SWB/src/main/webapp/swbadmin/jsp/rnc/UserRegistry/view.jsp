<%-- 
    Document   : userRegistry
    Created on : 30/04/2018, 06:05:32 PM
    Author     : jose.jimenez
--%><%@page import="mx.gob.cultura.portal.resources.UserRegistry"%>
<%@page import="com.hp.hpl.jena.rdf.model.Statement"%>
<%@page import="com.hp.hpl.jena.ontology.OntModel"%>
<%@page import="org.semanticwb.SWBPlatform"%>
<%@page import="org.semanticwb.model.User"%>
<%@page import="org.semanticwb.portal.api.SWBParamRequest, org.semanticwb.portal.api.SWBResourceException, org.semanticwb.portal.api.SWBResourceURL"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<jsp:useBean id="paramsRequest" scope="request" type="org.semanticwb.portal.api.SWBParamRequest" />
<%
    //String swbAction = paramsRequest.getUser().isSigned() ? "editing" : "creating";
    User user = paramsRequest.getUser();
    SWBResourceURL url = paramsRequest.getActionUrl();   
//System.out.println("-------------------------------------------view.jsp");
    
    for(Statement st:user.getSemanticObject().getRDFResource().listProperties().toList()){
//System.out.println(st);        
    }
    boolean editMode;  
    boolean isAnnotator = (boolean)request.getAttribute("isAnnotator");
    boolean toBeAnnotator =false;
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
    String errorMsg = null;
    
    if (null != request.getParameter("condition") && !request.getParameter("condition").isEmpty()) {
//System.out.println("jsp:condition:"+request.getParameter("condition"));        
        errorMsg = paramsRequest.getLocaleString(request.getParameter("condition"));
//System.out.println("jsp:errorMsg:"+errorMsg);        
    }else{
//System.out.println("jsp:sin condition:"); 
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
    if (editMode) {
%>            
                if (form.pass2 && form.pass2.value !== form.passConf.value) {
                    alert("La contraseña no coincide con la confirmación.");
                    retValue = false;
                }
<%
   }else{
%>  
                if (form.pass2.value !== form.passConf.value) {
                    alert("La contraseña no coincide con la confirmación.");
                    retValue = false;
                }            
<%
    }
%>            
                if (!isEmailValid(form.email.value)) {
                    alert("La cuenta de correo proporcionada no es válida.")
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
            } else {
                alert("revisa tu información por favor de proporcionar un nombre, cuenta de correo válida y contraseña con su confirmación.");
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
            <h3 class="oswM uppercase"><%=user.isSigned()?paramsRequest.getLocaleString("lbl_titleEdit"):paramsRequest.getLocaleString("lbl_titleCreate")%></h3>
            <form id="regUser" method="post" action="<%=url.toString()%>"><!--  onsubmit="validate(this);" -->
                <%=user.getLogin()!=null?user.getLogin():"---"%>
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
                OntModel ont = SWBPlatform.getSemanticMgr().getSchema().getRDFOntModel();
                String tmpValue;

                Statement tmpStat=user.getSemanticObject().getRDFResource().getProperty(ont.createDatatypeProperty(UserRegistry.ORGANITATION_NAME_URI));
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
                if(editMode){
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
