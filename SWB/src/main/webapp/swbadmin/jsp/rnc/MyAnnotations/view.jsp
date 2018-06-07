<%-- 
    Document   : view
    Created on : 23/04/2018, 05:35:01 PM
    Author     : rene.jara
--%>
<%@page import="org.semanticwb.model.UserRepository"%>
<%@page import="org.semanticwb.model.User"%>
<%@page import="mx.gob.cultura.portal.resources.MyAnnotations"%>
<%@page import="org.semanticwb.portal.api.SWBResourceURL"%>
<%@page import="mx.gob.cultura.portal.response.Annotation"%>
<%@page import="java.util.List"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<jsp:useBean id="paramRequest" scope="request" type="org.semanticwb.portal.api.SWBParamRequest" />
<%
    List<Annotation> annotations = (List<Annotation>) request.getAttribute("annotations");
    User user = paramRequest.getUser();
    String id = (String)request.getAttribute("id");
    UserRepository ur=paramRequest.getWebPage().getWebSite().getUserRepository();

    if(user!=null && user.isSigned()){
        SWBResourceURL saveURL = paramRequest.getRenderUrl();
        saveURL.setMode(MyAnnotations.ASYNC_ADD);
        saveURL.setCallMethod(SWBResourceURL.Call_DIRECT);
           
%>
<script>
    function addAnnotation(){
    console.log("addAnnotation");
        if($('#bodyValue').val().length>0){
            $.getJSON('<%=saveURL.toString()%>',{'id':'<%=id%>','bodyValue':$('#bodyValue').val()}, function (data) {
                ulContent ='';
                $.each(data, function(index, element) {
                    ulContent += '<li><em>'+element.creatorName+'</em>'+element.bodyValue+'</li>';
                });
                $('#annotationList').html(ulContent);
                $('#bodyValue').val('');
            }).fail(function( jqxhr, textStatus, error ) {
                var err = textStatus + ", " + error;
                console.log( "Request Failed: " + err );
                $("#dialog-message-tree").text(err);
                $("#dialog-message-tree").dialog("open");
            });

        }
    }        
</script>   
<form>
    <textarea name="bodyValue" id="bodyValue"></textarea>
    <button type="button" onclick="addAnnotation();return false;" name="btnSubmit" id="btnSubmit" value="submit">Enviar</button>
</form>    
<%
    }
%>
<ul id="annotationList">
    <%   
 
    String creatorName;
    for(Annotation annotation:annotations){
        creatorName="";
        if(ur.getUser(annotation.getCreator())!=null){
            creatorName=ur.getUser(annotation.getCreator()).getFullName();
        }
     %>
     <li><em><%=creatorName%></em>
         <%=annotation.getBodyValue()%>         
     </li>           
    <%      
    }
    
    %>
    
</ul>
 <a href="/es/repositorio/AdmAnotaciones">Administrar mis anotaciones</a>
   
