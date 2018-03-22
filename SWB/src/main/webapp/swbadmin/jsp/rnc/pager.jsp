<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>    
<%@page import="mx.gob.cultura.portal.response.Entry,java.util.List"%>
<%
    int numBloque = 0;
    int ultimoBloque = 0;
    int paginaFinalBloque = 0;
    int paginaInicialBloque = 0;
    int primerRegistroMostrado = 0;
    int ultimoRegistroMostrado = 0;
    Integer totalPages = (Integer)session.getAttribute("TOTAL_PAGES");
    //Integer numPageList = (Integer)session.getAttribute("NUM_PAGE_LIST");
    //Integer pageJumpSize = (Integer)session.getAttribute("NUM_PAGE_JUMP");
    Integer totalPaginas = (Integer)session.getAttribute("TOTAL_PAGES");
    Integer paginaActual = (Integer)session.getAttribute("NUM_PAGE_LIST");
    Integer paginasPorBloque = (Integer)session.getAttribute("PAGE_JUMP_SIZE");
    Integer registrosPorPagina = (Integer)session.getAttribute("NUM_RECORDS_VISIBLE");
    Integer totalRegistros = (Integer)session.getAttribute("NUM_RECORDS_TOTAL");
    if (null == paginaActual) paginaActual = 1;
    //List<Entry> references = (List<Entry>)session.getAttribute("PAGE_LIST");
    if (paginaActual != 0 && totalPaginas !=0 && totalRegistros != 0 && registrosPorPagina != 0) {
	numBloque = (paginaActual-1)/paginasPorBloque - (paginaActual-1)%paginasPorBloque/paginasPorBloque;
	ultimoBloque = (totalPaginas-1)/paginasPorBloque - (totalPaginas-1)%paginasPorBloque/paginasPorBloque;
	paginaInicialBloque = numBloque*paginasPorBloque+1;
	paginaFinalBloque = paginaInicialBloque+paginasPorBloque-1;
	primerRegistroMostrado = (paginaActual-1)*registrosPorPagina+1;
	ultimoRegistroMostrado = primerRegistroMostrado+registrosPorPagina-1;
	if (ultimoRegistroMostrado>totalRegistros) {
            ultimoRegistroMostrado = totalRegistros;
	}
	if (paginaFinalBloque>totalPaginas) {
            paginaFinalBloque = totalPaginas;
	}
    }
    String m = null != request.getAttribute("m") ? (String)request.getAttribute("m") : "g";
    String f = null != request.getAttribute("f") ? (String)request.getAttribute("f") : "relvdes";
%>
<div class="container paginacion">
    <ul class="azul">
		<!-- liga para saltar al bloque anterior -->
    <%
	if (totalPages > 1) { //TODO: Check condition
            if (numBloque==0) {
    %>
		<li><a href="#"><i class="ion-ios-arrow-back" aria-hidden="true"></i></a></li>
    <%
            }else {
		int primeraPaginaBloqueAnterior = (numBloque-1)*paginasPorBloque+1;
    %>
		<li><a class="ion-ios-arrow-back" aria-hidden="true" title="anterior" href="javascript:doPage(<% out.print(""+primeraPaginaBloqueAnterior+",'"+m+",'"+f); %>')">&nbsp;</a></li>
    <%
            }
	}
    %>
    <!-- numeración de páginas a mostrar -->
    <%
        for (int i=paginaInicialBloque; i <= paginaFinalBloque; i++) {
            if (i==paginaActual) {
                out.println("<li><a href=\"#\" class=\"select\">"+i+"</a></li>");
            }else {
		out.println("<li><a href=\"#\" onclick=\"javascript:doPage("+i+",'"+m+"','"+f+"')\">"+i+"</a></li>");
            }
        }
    %>	
	<!-- liga para saltar al bloque posterior -->
    <%
        if (totalPages > 1) {
	    if (numBloque==ultimoBloque || totalRegistros==0) {
    %>
                <li><a href="#"><i class="ion-ios-arrow-forward" aria-hidden="true"></i></a></li>
    <%
            }else {
                int primeraPaginaBloqueSiguiente = (numBloque+1)*paginasPorBloque+1;
    %>
		<li><a href="#" onclick="doPage('<%= primeraPaginaBloqueSiguiente+",'"+m+",'"+f %>')"><i class="ion-ios-arrow-forward" aria-hidden="true"></i></a></li>
    <%
            }
        }
    %>
    </ul>
</div>
<!-- final índice de paginación -->