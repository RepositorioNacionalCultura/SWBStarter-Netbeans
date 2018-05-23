<%-- 
    Document   : homeCollectionsSession
    Created on : 27/02/2018, 12:16:48 PM
    Author     : jose.jimenez
--%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page import="org.semanticwb.portal.api.SWBParamRequest, org.semanticwb.portal.api.SWBResourceException, org.semanticwb.model.WebPage" %>
<%@page import="mx.gob.cultura.portal.resources.SessionInitializer" %>
<%
    SWBParamRequest paramsRequest = (SWBParamRequest) request.getAttribute("paramsRequest");
    String lbl_sectionHeader;
    String txt_register;
    String txt_alreadyReg;
    String txt_logIn;
    String txt_noAccount;
    String txt_createAccount;
    String txt_continueWith;
    String faceLink = SessionInitializer.getFacebookLink(paramsRequest.getWebPage().getWebSite().getId());
    try {
        lbl_sectionHeader = paramsRequest.getLocaleString("lbl_sectionHeader");
    } catch (SWBResourceException swbr) {
        lbl_sectionHeader = "CREA TUS COLECCIONES";
    }
    try {
        txt_register = paramsRequest.getLocaleString("txt_register");
    } catch (SWBResourceException swbr) {
        txt_register = "Debes estar registrado para poder guardar tus favoritos";
    }
    try {
        txt_alreadyReg = paramsRequest.getLocaleString("txt_alreadyReg");
    } catch (SWBResourceException swbr) {
        txt_alreadyReg = "Si ya estás registrado, ";
    }
    try {
        txt_logIn = paramsRequest.getLocaleString("txt_logIn");
    } catch (SWBResourceException swbr) {
        txt_logIn = "inicia sesión aquí.";
    }
    try {
        txt_noAccount = paramsRequest.getLocaleString("txt_noAccount");
    } catch (SWBResourceException swbr) {
        txt_noAccount = "¿No tienes una cuenta? ";
    }
    try {
        txt_createAccount = paramsRequest.getLocaleString("txt_createAccount");
    } catch (SWBResourceException swbr) {
        txt_createAccount = "Crea aquí tu cuenta";
    }
    try {
        txt_continueWith = paramsRequest.getLocaleString("txt_continueWith");
    } catch (SWBResourceException swbr) {
        txt_continueWith = "Inicia con ";
    }
%>
    <section id="colecciones" class="gris-bg">
        <div class="container">
            <div class="row">
                <div class="col-12 col-md-5 colecciones1">
                    <img src="/work/models/cultura/img/img-colecciones-1.png" class="" >
                    <img src="/work/models/cultura/img/img-colecciones-2.png" class="" >
                    <img src="/work/models/cultura/img/img-colecciones-3.png" class="" >
                </div>
                <div class="col-12 col-md-7 colecciones2">
                    <h3 class="oswM rojo"><%=lbl_sectionHeader%></h3>
                    <p class="registrate"><%=txt_register%></p>
                    <hr>
                    <p><%=txt_alreadyReg%>
                        <a href="#" data-toggle="modal" data-target="#modal-sesion"><%=txt_logIn%></a>
                    </p>
                    <p><%=txt_noAccount%><a href="#" class=""><%=txt_createAccount%></a></p>
                    <hr>
                    <p>
                        <%=txt_continueWith%>
                        <%=faceLink%>
                        <img src="/work/models/cultura/img/icono-tw.png" >
                        <img src="/work/models/cultura/img/icono-goo.png" >
                    </p>
                </div>
            </div>
        </div>
    </section>