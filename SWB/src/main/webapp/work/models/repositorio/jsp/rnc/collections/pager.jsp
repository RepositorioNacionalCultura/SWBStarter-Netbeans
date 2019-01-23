<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>    
<%@page import="mx.gob.cultura.portal.response.Collection, java.util.List"%>
<%
    int numBloque = 0;
    int ultimoBloque = 0;
    int paginaFinalBloque = 1;
    int paginaInicialBloque = 1;
    int primerRegistroMostrado = 0;
    int ultimoRegistroMostrado = 0;
    Integer totalPages = (Integer) request.getAttribute("TOTAL_PAGES");
    Integer totalPaginas = (Integer) request.getAttribute("TOTAL_PAGES");
    Integer paginaActual = (Integer) request.getAttribute("NUM_PAGE_LIST");
    Integer paginasPorBloque = (Integer) request.getAttribute("PAGE_JUMP_SIZE");
    Integer totalRegistros = (Integer) request.getAttribute("NUM_RECORDS_TOTAL");
    Integer registrosPorPagina = (Integer) request.getAttribute("NUM_RECORDS_VISIBLE");
    Integer t = null != request.getAttribute("ct") ? (Integer)request.getAttribute("ct") : 0;
    if (null == paginaActual) {
        paginaActual = 1;
    }
    if (paginaActual != 0 && totalPaginas != 0 && totalRegistros != 0 && registrosPorPagina != 0) {
        numBloque = (paginaActual - 1) / paginasPorBloque - (paginaActual - 1) % paginasPorBloque / paginasPorBloque;
        ultimoBloque = (totalPaginas - 1) / paginasPorBloque - (totalPaginas - 1) % paginasPorBloque / paginasPorBloque;
        paginaInicialBloque = numBloque * paginasPorBloque + 1;
        paginaFinalBloque = paginaInicialBloque + paginasPorBloque - 1;
        primerRegistroMostrado = (paginaActual - 1) * registrosPorPagina + 1;
        ultimoRegistroMostrado = primerRegistroMostrado + registrosPorPagina - 1;
        if (ultimoRegistroMostrado > totalRegistros) {
            ultimoRegistroMostrado = totalRegistros;
        }
        if (paginaFinalBloque > totalPaginas) {
            paginaFinalBloque = totalPaginas;
        }
    }
%>
<div class="container paginacion">
    <hr>
    <ul class="azul">
        <!-- liga para saltar al bloque anterior -->
        <%
            if (totalPages > 1) { //TODO: Check condition
                if (numBloque == 0) {
        %>
                    <li><a href="#"><i class="ion-ios-arrow-back" aria-hidden="true"></i></a></li>
                <%
                } else {
                    int primeraPaginaBloqueAnterior = (numBloque - 1) * paginasPorBloque + 1;
                    String it = primeraPaginaBloqueAnterior+","+t;
                %>
                    <li><a class="ion-ios-arrow-back" aria-hidden="true" title="anterior" href="javascript:doPage(<% out.print("'" + it); %>')">&nbsp;</a></li>
            <%
                    }
                }
            %>
        <!-- numeración de páginas a mostrar -->
        <%
            for (int i = paginaInicialBloque; i <= paginaFinalBloque; i++) {
                if (i == paginaActual) {
                    out.println("<li><a href=\"#\" class=\"select\">" + i + "</a></li>");
                } else {
                    String it = i+","+t;
                    out.println("<li><a href=\"#\" onclick=\"javascript:doPage(" + it + ")\">" + i + "</a></li>");
                }
            }
        %>
        <!-- liga para saltar al bloque posterior -->
        <%
            if (totalPages > 1) {
                if (numBloque == ultimoBloque || totalRegistros == 0) {
        %>
                    <li><a href="#"><i class="ion-ios-arrow-forward" aria-hidden="true"></i></a></li>
                <%
                } else {
                    int primeraPaginaBloqueSiguiente = (numBloque + 1) * paginasPorBloque + 1;
                    String it = primeraPaginaBloqueSiguiente+","+t;
                %>
        <li><a href="#" onclick="doPage('<%= it%>')"><i class="ion-ios-arrow-forward" aria-hidden="true"></i></a></li>
                <%
                        }
                    }
                %>
    </ul>
</div>
<!-- final índice de paginación -->