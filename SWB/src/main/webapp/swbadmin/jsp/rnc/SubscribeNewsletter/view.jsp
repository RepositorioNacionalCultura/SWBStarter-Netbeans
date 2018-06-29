<%-- 
    Document   : view
    Created on : 23/04/2018, 05:35:01 PM
    Author     : rene.jara
--%>
<%@page import="org.semanticwb.model.UserRepository"%>
<%@page import="org.semanticwb.model.User"%>
<%@page import="mx.gob.cultura.portal.resources.SubscribeNewsletter"%>
<%@page import="org.semanticwb.portal.api.SWBResourceURL"%>
<%@page import="java.util.List"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<jsp:useBean id="paramRequest" scope="request" type="org.semanticwb.portal.api.SWBParamRequest" />
<%
    SWBResourceURL subURL = paramRequest.getRenderUrl();
    subURL.setMode(SubscribeNewsletter.ASYNC_SUBSCRIBE);
    subURL.setCallMethod(SWBResourceURL.Call_DIRECT);
           
%>
<script>
    function subscribeNL(){
    console.log("subscribeNL");
        if($('#emailNL').val().length>0){
            $.getJSON('<%=subURL.toString()%>',{'email':$('#emailNL').val()}, function (data) {                
               if(data.subscribed){;
                   $('#div_nl').html('<div class="rojo-bg"><%=paramRequest.getLocaleString("msg_newsletter")%></div>');
               }else{
                   $('#div_nl').html('<div class="rojo-bg"><%=paramRequest.getLocaleString("err_newsletter")%></div>');
               }
               $('#div_nl').removeClass("input-group");
               $('#div_nl').addClass("newsgracias");
                /*ulContent ='';
                $.each(data, function(index, element) {
                    ulContent += '<li><em>'+element.creatorName+'</em>'+element.bodyValue+'</li>';
                });
                $('#annotationList').html(ulContent);
                $('#bodyValue').val('');*/
            }).fail(function( jqxhr, textStatus, error ) {
                var err = textStatus + ", " + error;
                console.log( "Request Failed: " + err );
                $('#div_nl').html('<div class="rojo-bg">Request Failed: '+err+'</div>');
                //alert(err)
            });

        }
    }        
</script>   
    <div class="gris21-bg">
        <div class="container newscontainer">
            <div class="newsletter">
                <%=paramRequest.getLocaleString("label_newsletter")%>
                <div id="div_nl" class="input-group">
                    <input name="emailNL" id="emailNL" type="text" class="form-control" placeholder="<%=paramRequest.getLocaleString("placeholder_newsletter")%>" aria-label="<%=paramRequest.getLocaleString("placeholder_newsletter")%>" aria-describedby="basic-addon2">
                    <div class="input-group-append">
                        <button class="btn btn-rojo" onclick="subscribeNL();return false;" type="button"><%=paramRequest.getLocaleString("button_newsletter")%></button>
                    </div>
                </div>
            </div>
        </div>
    </div>
