<%-- 
    Document   : view
    Created on : 23/04/2018, 05:39:10 PM
    Author     : rene.jara
--%>
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
    List<Map<String,String>> annotations = (List<Map<String,String>>) request.getAttribute("annotations");
    User user = paramRequest.getUser();
    String userId = "";
    boolean isAdmin=(boolean) request.getAttribute("isAdmin");
    if (user.isSigned()){
        userId=user.getId();                 
    }    
    int currentPage = (Integer)request.getAttribute("currentPage");
    int totalPages = (Integer)request.getAttribute("totalPages");
    String order = (String)request.getAttribute("order");
    String filter = (String)request.getAttribute("filter");

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
    if (!userId.isEmpty()){
%>

<script>
    function callAction(url,id,bodyValue){
            $.getJSON(url,{'id':id,'bodyValue':bodyValue}, function (data) {
                if(data.deleted){
                    $('#li'+id).replaceWith('<li>borrado</li>');
                }else{
                    $('#li'+data.id).replaceWith(buildLi(data,"***"));
                }    

            }).fail(function( jqxhr, textStatus, error ) {
                var err = textStatus + ", " + error;
                console.log( "Request Failed: " + err );
                alert(err);
                //$("#dialog-message-tree").text(err);
                //$("#dialog-message-tree").dialog("open");
            });

    }        
    /*function fillList(cp){
    console.log("fillList");

console.log("getJSON");            
            $.getJSON('<%=listURL%>',{'cp',0}, function (data) {
console.log("data:");
console.log(data);
                ulElem=$('#annotationList')
                ulElem.empty();            
                $.each(data, function(index, element) {
console.log(element);                    
                    ulElem.append(buildLi(element));
                });
            }).fail(function( jqxhr, textStatus, error ) {
                var err = textStatus + ", " + error;
                console.log( "Request Failed: " + err );
                alert(err);
                //$("#dialog-message-tree").text(err);
                //$("#dialog-message-tree").dialog("open");
            });

    }*/
    function buildLi(elem,ext){
    console.log("buildLi"+elem);    
        liContent ='';
        if(ext===undefined){ext='';}
<%  
        if(isAdmin){
%>        
        if(elem.isOwn && elem.isMod){
            liContent = '<li id="li'+elem.id+'">'
                +'<a href="/swb/repositorio/detalle?id='+elem.oid+'">'+elem.bicTitle+'('+elem.bicCreator+')</a>'
                +'<form><i>'+elem.creator+'</i>'+'<em>'+elem.created+'</em>'+'<textarea id="bv'+elem.id+'">'+elem.bodyValue+'</textarea>'
                +'<button type="button" onclick="callAction(\'<%=modURL.toString()%>\',\''+elem.id+'\',$(\'#bv'+elem.id+'\').val());return false;">Cambiar</button>'
                +'<a href="#" onclick="callAction(\'<%=delURL.toString()%>\',\''+elem.id+'\');return false;">Borrar</a>'
                +'<a href="#" onclick="callAction(\'<%=accURL.toString()%>\',\''+elem.id+'\');return false;">Autorizar</a>'
                +'</form>'+ext+'</li>';
        }else if(elem.isMod){
            liContent = '<li id="li'+elem.id+'">'
                +'<a href="/swb/repositorio/detalle?id='+elem.oid+'">'+elem.bicTitle+'('+elem.bicCreator+')</a>'
                +'<i>'+elem.creator+'</i>'+'<em>'+elem.created+'</em>'+elem.bodyValue                        
                +'<a href="#" onclick="callAction(\'<%=accURL.toString()%>\',\''+elem.id+'\');return false;">Autorizar</a>'
                +''+ext+'</li>';
        }else{            
            liContent = '<li id="li'+elem.id+'">'
                +'<a href="/swb/repositorio/detalle?id='+elem.oid+'">'+elem.bicTitle+'('+elem.bicCreator+')</a>'
                +'<i>'+elem.creator+'</i>'+'<em>'+elem.created+'</em>'+elem.bodyValue                                        
                +'<a href="#" onclick="callAction(\'<%=rjcURL.toString()%>\',\''+elem.id+'\');return false;">Desautorizar</a>'
                +''+ext+'</li>';
        }

<%
        }else{
%>        
        if(elem.isMod){
            liContent = '<li id="li'+elem.id+'">'
                +'<a href="/swb/repositorio/detalle?id='+elem.oid+'">'+elem.bicTitle+'('+elem.bicCreator+')</a>'
                +'<form><!--'+elem.creator+'-->'+'<em>'+elem.created+'</em>'+'<textarea id="bv'+elem.id+'">'+elem.bodyValue+'</textarea>'
                +'<button type="button" onclick="callAction(\'<%=modURL.toString()%>\',\''+elem.id+'\',$(\'#bv'+elem.id+'\').val());return false;">Cambiar</button>'
                +'<a href="#" onclick="callAction(\'<%=delURL.toString()%>\',\''+elem.id+'\');return false;">Borrar</a>'
                +'</form>'+ext+'</li>';
        }else{            
            liContent = '<li id="li'+elem.id+'">'
                +'<a href="/swb/repositorio/detalle?id='+elem.oid+'">'+elem.bicTitle+'('+elem.bicCreator+')</a>'
                +'<i>'+elem.creator+'</i>'+'<em>'+elem.created+'</em>'+elem.bodyValue                        
                +''+ext+'</li>';
        }
<%  
        }
%>  
        return liContent;
    }
</script>         

<%
    }
%>        

<!--div>
        <a href="<%=pagerUrl+"?p="+currentPage+"&o=&f="+filter%>">normal</a>
        <a href="<%=pagerUrl+"?p="+currentPage+"&o="+AnnotationsMgr.ORDER_DATE+"&f="+filter%>">fecha</a>
</div-->

<ul id="annotationList">
<%
    for(Map<String,String> annotation:annotations){
     %>
     <li id="li<%=annotation.get("id")%>">
        <a href="/swb/repositorio/detalle?id=<%=annotation.get("oid")%>"><%=annotation.get("bicTitle")%>(<%=annotation.get("bicCreator")%>)</a>
<%      
        if(annotation.containsKey("isOwn")&&annotation.containsKey("isMod")){   

%>       
        <form>
            <!-- <%=annotation.get("creator")%> -->
            <em><%=annotation.get("created")%></em>
            <textarea id="bodyValue<%=annotation.get("id")%>"><%=annotation.get("bodyValue")%></textarea>
            <button type="button" onclick="callAction('<%=modURL.toString()%>','<%=annotation.get("id")%>',$('#bodyValue<%=annotation.get("id")%>').val());return false;" >Cambiar</button>
        </form> 
<%      
           
        }else{ 
            if(annotation.containsKey("isOwn")){
%>                 
        <i><%=annotation.get("creator")%></i>
<%              
        }   
%>         
        <em><%=annotation.get("created")%></em>
        <%=annotation.get("bodyValue")%> <i><%=annotation.get("created")%> </i> 
<%
        }
        if (annotation.containsKey("isOwn")&&annotation.containsKey("isMod")){
%>         
             <a href="#" onclick="callAction('<%=delURL.toString()%>','<%=annotation.get("id")%>');return false;">Borrar</a>
<%      
        } 
        if(isAdmin){
            if (annotation.containsKey("isMod")){

%>         
            <a href="#" onclick="callAction('<%=accURL.toString()%>','<%=annotation.get("id")%>');return false;">Autorizar</a>             
<%
            }else{
%>         
             <a href="#" onclick="callAction('<%=rjcURL.toString()%>','<%=annotation.get("id")%>');return false;">Desautorizar</a>
<%
            }
        }
%>
    </li>           
<%      
    }
%>
</ul>
<div class="container paginacion">
    <hr>
    <ul id='pager' class="azul">
        <li><a href="<%=pagerUrl+"?p="+(currentPage>1?currentPage-1:1)+"&o="+order+"&f="+filter%>"><i class="ion-ios-arrow-back" aria-hidden="true"></i></a></li>
<%
    for(int i=1; i<=totalPages;i++){
%>        
        <li><a href="<%=pagerUrl+"?p="+i+"&o="+order+"&f="+filter%>" <%=currentPage==i?"class=\"select\"":""%>><%=i%></a></li>
<%                
    }
%>        
        <li><a href="<%=pagerUrl+"?p="+(currentPage<totalPages?currentPage+1:totalPages)+"&o="+order+"&f="+filter%>"><i class="ion-ios-arrow-forward" aria-hidden="true"></i></a></li>
    </ul>
</div>

<!--div id="dialog-message-tree" title="error">
    <p>
        <div id="dialog-text-tree"></div>
    </p>
</div>

<div id="dialog-success-tree" title="éxito">
    <p>
        <span class="ui-icon ui-icon-circle-check" style="float:left; margin:0 7px 50px 0;"></span>
        <div id="dialog-msg-tree"></div>
    </p>
</div-->

