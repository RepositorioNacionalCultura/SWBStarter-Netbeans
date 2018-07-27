<%-- 
    Document   : view
    Created on : 23/04/2018, 05:35:01 PM
    Author     : rene.jara
--%>
<%@page import="mx.gob.cultura.portal.resources.AnnotationsMgr"%>
<%@page import="org.semanticwb.model.WebPage"%>
<%@page import="org.semanticwb.model.Resource"%>
<%@page import="org.semanticwb.portal.api.SWBResourceURLImp"%>
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
    String lang=user.getLanguage();
    
    Resource resMng=paramRequest.getWebPage().getWebSite().getResource(paramRequest.getResourceBase().getAttribute("AnnRes", "197"));
    WebPage  wpMng=paramRequest.getWebPage().getWebSite().getWebPage(paramRequest.getResourceBase().getAttribute("AnnMng", "AdmAnotaciones"));      
    SWBResourceURLImp urlMng = new SWBResourceURLImp(request, resMng, wpMng, SWBResourceURL.UrlType_RENDER);
    urlMng.setMode(AnnotationsMgr.MODE_MANAGE);

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
                        for (i=0;i<bva.length && i<3;i++){
                            console.log(i+"-"+bva.length);
                            if (0<2){//para no borrar el if
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
                                        'type="button"><%=paramRequest.getLocaleString("lbl_see_more")%> <span class="ion-plus-circled"></span></button>'+
                                        '<span class="linea"></span>'+
                                        '</p>';
                                }
                            }    
                        }
                        lContent +='<a href="<%=wpMng.getUrl()%>?id='+element.id+'" class="btn-vermas" ><%=paramRequest.getLocaleString("lbl_see_more")%> <span class="ion-plus-circled"></span></a>';
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
            <h3 class="oswB rojo"><%=paramRequest.getLocaleString("lbl_title")%></h3>
            <p><strong><%=paramRequest.getLocaleString("lbl_subtitle")%></strong></p>
            <p><%=paramRequest.getLocaleString("lbl_description")%></p>
<%
    if(user==null || !user.isSigned()){
%>    
            <p><a href="#modal-sesion" data-toggle="modal" data-target="#modal-sesion" class="oswM"><%=paramRequest.getLocaleString("lbl_log_in")%></a></p>    
            <p><a href="/<%=lang%>/repositorio/Registro?beannotator" class="oswM"><%=paramRequest.getLocaleString("lbl_register")%></a></p> 
<%
    }else if(user.isSigned()&& !isAnnotator){
%>    
            <p><a href="/<%=lang%>/repositorio/Registro?beannotator" class="oswM"><%=paramRequest.getLocaleString("lbl_register")%></a></p>     
<%
    }else if(isAnnotator){
%>                
            <div class="media">
                <img class="align-self-start mr-3" src="/work/models/repositorio/img/usuario.jpg">
                <div class="media-body">
                    <textarea  name="bodyValue" id="bodyValue"></textarea>
                    <button class="btn btn-rojo" onclick="addAnnotation();return false;"><%=paramRequest.getLocaleString("btn_add")%></button>
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
        for(int i =0; i < bvs.length && i<3 ;i++){
            if (0<2){//para no borrar el if
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
                            <button aria-controls="vermas" aria-expanded="false" class="btn-vermas btn-vermas-<%=annotation.getId()%>" data-target="#vermas-<%=annotation.getId()%>" data-toggle="collapse" type="button"><%=paramRequest.getLocaleString("lbl_see_more")%> <span class="ion-plus-circled"></span></button>
                            <span class="linea"></span>
                        </p>                        
<%
                } // @rgjs corregir hidden de ver mas
            }   
        }
%>                                              
                        <a href="<%=wpMng.getUrl()%>?id=<%=annotation.getId()%>" class="btn-vermas" ><%=paramRequest.getLocaleString("lbl_see_more")%> <span class="ion-plus-circled"></span></a>
                    </div>
                </div>              
    <%      
    }
    %>
            </div>
        </div>
<%
    if(isAnnotator){  
%>            
    <p><a href="<%=urlMng.toString()%>" class="oswM">Administrar mis anotaciones</a></p>
<%
    }
%>
    </section>   
