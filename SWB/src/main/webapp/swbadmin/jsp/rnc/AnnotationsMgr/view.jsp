<%-- 
    Document   : view.jsp
    Created on : 24/07/2018, 10:51:22 PM
    Author     : rene.jara
--%>
<%@page import="mx.gob.cultura.portal.utils.Utils"%>
<%@page import="java.util.Map"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="org.semanticwb.model.UserRepository"%>
<%@page import="mx.gob.cultura.portal.resources.AnnotationsMgr"%>
<%@page import="org.semanticwb.portal.api.SWBResourceURL"%>
<%@page import="java.util.ArrayList"%>
<%@page import="mx.gob.cultura.portal.persist.AnnotationMgr"%>
<%@page import="org.semanticwb.model.User"%>
<%@page import="mx.gob.cultura.portal.response.Annotation"%>
<%@page import="java.util.List"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<jsp:useBean id="paramRequest" scope="request" type="org.semanticwb.portal.api.SWBParamRequest" />
<%
    Map<String,String> annotation = (Map<String,String>) request.getAttribute("annotation");
    User user = paramRequest.getUser();
    String userId = "";
    boolean isAdmin=(boolean) request.getAttribute("isAdmin");
    boolean isAnnotator = (boolean)request.getAttribute("isAnnotator");
    if (user.isSigned()){
        userId=user.getId();                 
    }    

    String pagerUrl = paramRequest.getWebPage().getUrl();

    SWBResourceURL listURL = paramRequest.getRenderUrl().setCallMethod(SWBResourceURL.Call_DIRECT);
    listURL.setMode(AnnotationsMgr.ASYNC_LIST);
    SWBResourceURL accURL = paramRequest.getRenderUrl().setCallMethod(SWBResourceURL.Call_DIRECT);
    accURL.setMode(AnnotationsMgr.ASYNC_ACCEPT);
    SWBResourceURL rjcURL = paramRequest.getRenderUrl().setCallMethod(SWBResourceURL.Call_DIRECT);
    rjcURL.setMode(AnnotationsMgr.ASYNC_REJECT);
    SWBResourceURL modURL = paramRequest.getRenderUrl().setCallMethod(SWBResourceURL.Call_DIRECT);
    modURL.setMode(AnnotationsMgr.ASYNC_MODIFY);
    SWBResourceURL delURL = paramRequest.getRenderUrl().setCallMethod(SWBResourceURL.Call_DIRECT);
    delURL.setMode(AnnotationsMgr.ASYNC_DELETE);  
    String scriptFB = Utils.getScriptFBShare(request);
%>
<%=scriptFB%>
<%
    if (!userId.isEmpty()&&(isAdmin||isAnnotator)){
%>

<script>
    function callAction(url,id){
        console.log("callAction"+id);
        $.getJSON(url,{'id':id}, function (data) {                        
            if(data.deleted){
                $('#div_bodyValue').replaceWith('<div>Borrado </div>');
                waitBack;
            }else{
                updateActions(data);
            }  
        }).fail(function( jqxhr, textStatus, error ) {
            var err = textStatus + ", " + error;
            console.log( "Request Failed: " + err );
            alert(err);
            //$("#dialog-message-tree").text(err);
            //$("#dialog-message-tree").dialog("open");
        });
    }        
    
    function callUpdate(url,id,bodyValue){
        console.log("callUpdate"+id);
        $.getJSON(url,{'id':id,'bodyValue':bodyValue}, function (data) { 
            updateActions(data);
            updateDiv(data);
        }).fail(function( jqxhr, textStatus, error ) {
            var err = textStatus + ", " + error;
            console.log( "Request Failed: " + err );
            alert(err);
            //$("#dialog-message-tree").text(err);
            //$("#dialog-message-tree").dialog("open");
        });
    }
    
    function updateActions(element){
        console.log("updateActions"+element); 
        lContent ='';            
<%  
        if(isAdmin){
%>        
console.log("1");        
        if(element.isOwn && element.isMod){
console.log("2");                    
            /*
            lContent += '<textarea id="bv'+element.id+'">'+element.bodyValue+'</textarea>'+
            */
            lContent += '<a href="#" onclick="editEnable();return false;" class="btn btn-blanco"><span class="ion-edit rojo"></span> Editar anotación</a>';
            lContent += '<a href="#" onclick="callAction(\'<%=delURL.toString()%>\',\''+element.id+'\');return false;" class="btn btn-blanco"><span class="ion-edit rojo"></span> Borrar</a>'+
                        '<a href="#" onclick="callAction(\'<%=accURL.toString()%>\',\''+element.id+'\');return false;" class="btn btn-blanco"><span class="ion-edit rojo"></span> Autorizar</a>';                        
            liContent=lContent;
//console.log(liContent);
        }else{
console.log("3");                    
            bvs=element.bodyValue;
            if(element.isMod){
console.log("4");                        
                lContent += '<a href="#" onclick="callAction(\'<%=accURL.toString()%>\',\''+element.id+'\');return false;" class="btn btn-blanco"><span class="ion-edit rojo"></span> Autorizar</a>';
            }else{   
console.log("5");                        
                lContent += '<a href="#" onclick="callAction(\'<%=rjcURL.toString()%>\',\''+element.id+'\');return false;" class="btn btn-blanco"><span class="ion-edit rojo"></span> Desautorizar</a>';
            }     
            liContent=lContent;
        }

<%
        }else{
%>        
        if(element.isMod){
            /*
            lContent += '<textarea id="bv'+element.id+'">'+element.bodyValue+'</textarea>'+
            */
            lContent += '<a href="#" onclick="editEnable();return false;" class="btn btn-blanco"><span class="ion-edit rojo"></span> Editar anotación</a';
            lContent += '<a href="#" onclick="callAction(\'<%=delURL.toString()%>\',\''+element.id+'\');return false;" class="btn btn-blanco"><span class="ion-edit rojo"></span> Borrar</a>';
            liContent=lContent;
        }else{ 
            bvs=element.bodyValue;
            liContent=lContent;
        }
<%  
        }
%>             
//console.log(liContent);        
            $('#div_actions').replaceWith('<div class="row redes" id="div_actions">'+liContent+'</div>');
               
    }
    
    function updateDiv(element){
        console.log("updateDiv"+element); 
        lContent ='';                   
        if(element.isMod){
            bvs=element.bodyValue;
            if(bvs){            
                bva=bvs.split('\n');
                lContent +='<div id="bodyValueTar" class="d-none">'+
                            '<textarea id="bodyValue" rows="'+bva.length+'" cols="80" >'+
                            element.bodyValue+
                            '</textarea>'+
                            '<a href="#" onclick="callUpdate(\'<%=modURL.toString()%>\','+element.id+',$("#bodyValue").val());return false;" class="btn btn-blanco "><span class="ion-edit rojo"></span> Guardar</a>'+
                            '</div>';
                $('#bodyValueTar').replaceWith(lContent);
                console.log("bodyValueTar");
                lContent ='<div id="bodyValueTxt">';
                for (i=0;i<bva.length;i++){
                    lContent +='<p>'+bva[i]+'</p>';   
                }  
                lContent +='</div>';
                $('#bodyValueTxt').replaceWith(lContent);
                console.log("bodyValueTxt");
                //console.log(lContent);
            }
         
        }                           
    }
    
    function editEnable(){
        $('#bodyValueTar').removeClass('d-none');
        $('#bodyValueTxt').addClass('d-none');
    }
    function waitBack(){
        window.setTimeout(function(){window.location=document.referrer;},1500);
        
    }
    function back(){
        window.location=document.referrer;
        
    }
</script>         
<%
    }
    if(!annotation.isEmpty()){
%>        
    
    <section id="contenidoInterna">
       <div class="container coleccionSecc anotaciondetalle">
                <div class="row">
                    <div class="col-3 col-sm-4 coleccionSecc-02">
                        <a href="/swb/repositorio/detalle?id=<%=annotation.get("oid")%>"><img src="<%=annotation.get("bicThumbnail")%>" /></a> 
                    </div>
                    <div class="col-9 col-sm-8 coleccionSecc-01">
                        <div class="precontent">
                            <h2 class="oswM rojo normalcase"><a href="/swb/repositorio/detalle?id=<%=annotation.get("oid")%>"><%=annotation.get("bicTitle")%></a></h2>
                            <p class="bold"><%=annotation.get("bicCreator")%></p>
                            <div class="row perfilHead">
                                <img src="/work/models/repositorio/img/usuario.jpg" class="circle">
                                <p><%=annotation.get("creator")%>, <%=annotation.get("creatorOrg")%> <br> <%=annotation.get("created")%></p>
                            </div>
                            <hr class="rojo">
                            <div class="row redes" id="div_actions">
                                <a href="#" onclick="fbShare();"><span class="ion-social-facebook"></span> Compartir</a>
                                <a href="#"><span class="ion-social-twitter"></span> Tweet</a>
<%      
        if(annotation.containsKey("isOwn")&&annotation.containsKey("isMod")){   

%>       
            <!--textarea id="bodyValue<//%=id%>"><%=annotation.get("bodyValue")%></textarea-->
            <a href="#" onclick="editEnable();return false;" class="btn btn-blanco "><span class="ion-edit rojo"></span> Editar anotación</a>
            
<%      
           
        }
%>                                
   <%
        if (annotation.containsKey("isOwn")&&annotation.containsKey("isMod")){
%>         
             <a href="#" onclick="callAction('<%=delURL.toString()%>','<%=annotation.get("id")%>');return false;" class="btn btn-blanco "><span class="ion-edit rojo"></span> Borrar</a>
<%      
        } 
        if(isAdmin){
            if (annotation.containsKey("isMod")){

%>         
            <a href="#" onclick="callAction('<%=accURL.toString()%>','<%=annotation.get("id")%>');return false;" class="btn btn-blanco" ><span class="ion-edit rojo"></span> Autorizar</a>             
<%
            }else{
%>         
             <a href="#" onclick="callAction('<%=rjcURL.toString()%>','<%=annotation.get("id")%>');return false;" class="btn btn-blanco"><span class="ion-edit rojo"></span> Desautorizar</a>
<%
            }
        }
%>                                             
                            </div>
                        </div>
                        
                    </div>                    
                </div>
            </div>
        <div class="espacioTop"></div>
        <div class="container" id="div_bodyValue" >
<%            
            String[] bvs=annotation.get("bodyValue").split("\n");
            
        if (annotation.containsKey("isOwn")&&annotation.containsKey("isMod")){
%>         
            <div id="bodyValueTar" class="d-none">
                <textarea id="bodyValue" rows="<%=bvs.length%>" cols="80" ><%=annotation.get("bodyValue")%></textarea>
                <a href="#" onclick="callUpdate('<%=modURL.toString()%>','<%=annotation.get("id")%>',$('#bodyValue').val());return false;" class="btn btn-blanco "><span class="ion-edit rojo"></span> Guardar</a>
            </div>    
<%      
        }
%>         
            <div id="bodyValueTxt" >
<%
            for(int i =0; i < bvs.length;i++){
%>                        
                        <p><%=bvs[i]%></p>                       
<%
            } 
%>
            </div>
            <button class="btn btn-rojo" onclick="back();return false;">Regresar</button>
        </div>
    </section>
<%
    }
%>                    
