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
    boolean isAnnotator = (boolean)request.getAttribute("isAnnotator");
    UserRepository ur=paramRequest.getWebPage().getWebSite().getUserRepository();
    if(user!=null && user.isSigned()&&isAnnotator){
        SWBResourceURL saveURL = paramRequest.getRenderUrl();
        saveURL.setMode(MyAnnotations.ASYNC_ADD);
        saveURL.setCallMethod(SWBResourceURL.Call_DIRECT);
           
%>
<script>
    function addAnnotation(){
    console.log("addAnnotation");
        if($('#bodyValue').val().length>0){
            $.getJSON('<%=saveURL.toString()%>',{'id':'<%=id%>','bodyValue':$('#bodyValue').val()}, function (data) {
                lContent ='';
                //$.each(data, function(index, element) {
                element=data;
                    lContent += '<div class="media">'+
                                '<img class="align-self-start mr-3" src="/work/models/repositorio/img/usuario.jpg">'+
                                '<div class="media-body">'+
                                '<p class="mt-0 rojo">'+element.creatorName+'</p>';                        
                    bvs=element.bodyValue;
                    if(bvs){
                        bva=bvs.split('\n');
                        for (i=0;i<bva.length;i++){
                            console.log(i+"-"+bva.length);
                            if (i<2){
                                lContent +='<p>'+bva[i]+'</p>';
                            }else{
                                if(i==2){
                                    lContent +='<div class="collapse" id="vermas-'+element.id+'">';
                                }
                                lContent +='<p>'+bva[i]+'</p>';                      
                                if(i==bva.length-1){
                                    lContent +='</div>'+
                                        '<p class="vermas vermas-0 vermas-rojo vermas-'+element.id+'">'+
                                        '<button aria-controls="vermas" aria-expanded="false" class="btn-vermas '+
                                        'btn-vermas-'+element.id+'" data-target="#vermas-'+element.id+'" data-toggle="collapse" '+
                                        'type="button">Ver más <span class="ion-plus-circled"></span></button>'+
                                        '<span class="linea"></span>'+
                                        '</p>';
                                }
                            }    
                        }       
                    }                    
                    lContent += '</div>'+
                                '</div>'; 
                //});
                $('#annotationList').append(lContent);
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
<%
    }
%>    
    <section id="anotaciones">
        <div class="container">
            <h3 class="oswB rojo">Anotaciones</h3>
            <p><strong>¿Qué son las anotaciones?</strong></p>
            <p>Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.</p>
<%
    if(user==null || !user.isSigned()){
%>    
            <p><a href="#modal-sesion" data-toggle="modal" data-target="#modal-sesion" class="oswM">Inicia sesión</a></p>     
<%
    }else if(user.isSigned()&& !isAnnotator){
%>    
            <p><a href="/es/repositorio/Registro?beannotator" data-toggle="modal" data-target="#modal-sesion" class="oswM">Registrate</a></p>     
<%
    }else if(isAnnotator){
%>                
            <div class="media">
                <img class="align-self-start mr-3" src="/work/models/repositorio/img/usuario.jpg">
                <div class="media-body">
                    <textarea  name="bodyValue" id="bodyValue"></textarea>
                    <button class="btn btn-rojo" onclick="addAnnotation();return false;">Agregar anotación</button>
                 </div>
            </div> 
<%
    }
%>
            <div id="annotationList">
<%   
 
    String creatorName;
    for(Annotation annotation:annotations){
        creatorName="";
        if(ur.getUser(annotation.getCreator())!=null){
            creatorName=ur.getUser(annotation.getCreator()).getFullName();
        }
        
%>
                <div class="media">
                    <img class="align-self-start mr-3" src="/work/models/repositorio/img/usuario.jpg">
                    <div class="media-body">
                        <p class="mt-0 rojo"><%=creatorName%></p>
<%  
        String[] bvs=annotation.getBodyValue().split("\n");
        for(int i =0; i < bvs.length;i++){
            if (i<2){
%>                        
                        <p><%=bvs[i]%></p>                       
<%
            }else{
                if(i==2){
%>                        
                        <div class="collapse" id="vermas-<%=annotation.getId()%>">                     
<%
                }
%>                        
                        <p><%=bvs[i]%></p>                       
<%
                if(i==bvs.length-1){
%>                        
                        </div>
                        <p class="vermas vermas-0 vermas-rojo vermas-<%=annotation.getId()%>">
                            <button aria-controls="vermas" aria-expanded="false" class="btn-vermas btn-vermas-<%=annotation.getId()%>" data-target="#vermas-<%=annotation.getId()%>" data-toggle="collapse" type="button">Ver más <span class="ion-plus-circled"></span></button>
                            <span class="linea"></span>
                        </p>                        
<%
                } // @rgjs corregir hidden de ver mas
            }   
        }
%>                                              
                    </div>
                </div>              
    <%      
    }
    %>
            </div>
        </div> 
        <p><a href="/es/repositorio/AdmAnotaciones" class="oswM">Administrar mis anotaciones</a></p>
    </section>   
