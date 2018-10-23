/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.gob.cultura.portal.resources;

import org.semanticwb.portal.api.GenericResource;
import org.semanticwb.portal.api.SWBParamRequest;
import org.semanticwb.portal.api.SWBResourceException;

import javax.servlet.ServletException;
import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Logger;

/**
 *
 * @author sergio.tellez
 */
public class PagerAction extends GenericResource {

    protected static final String MODE_PAGE = "PAGE";
    protected static final String MODE_PREV = "PREV";
    protected static final String MODE_NEXT = "NEXT";
    protected static final String MODE_SORT = "SORT";
    protected static final String MODE_PAGES = "PAGES";
    protected static final String TOTAL_PAGES = "TOTAL_PAGES";
    protected static final String NUM_PAGE_JUMP = "NUM_PAGE_JUMP";
    protected static final String NUM_PAGE_LIST = "NUM_PAGE_LIST";
    protected static final String NUM_RECORDS_TOTAL = "NUM_RECORDS_TOTAL";
    protected static final String NUM_RECORDS_VISIBLE = "NUM_RECORDS_VISIBLE";
    
    protected int PAGE_NUM_ROW = 8;
    protected int PAGE_JUMP_SIZE = 5;
    protected String PAGE_LIST = "PAGE_LIST";
    protected String FULL_LIST = "FULL_LIST";
    
    private static final Logger LOG = Logger.getLogger(PagerAction.class.getName());

    @Override
    public void processRequest(HttpServletRequest request, HttpServletResponse response, SWBParamRequest paramRequest) throws SWBResourceException, IOException {
        response.setContentType("text/html; charset=UTF-8");
        String mode = paramRequest.getMode();
        if (MODE_PAGE.equals(mode)) {
            doPage(request, response, paramRequest);
        }else if (MODE_PREV.equals(mode)) {
            doPrev(request, response, paramRequest);
        }else if (MODE_NEXT.equals(mode)) {
            doNext(request, response, paramRequest);
        }else if (MODE_PAGES.equals(mode)) {
            doPages(request, response, paramRequest);
        }else if (MODE_SORT.equals(mode)) {
            doSort(request, response, paramRequest);
        }else
            super.processRequest(request, response, paramRequest);
    }

    @Override
    public void doView(HttpServletRequest request, HttpServletResponse response, SWBParamRequest paramRequest) throws SWBResourceException, IOException {
        request.getSession().removeAttribute(this.FULL_LIST);
        request.getSession().removeAttribute(this.PAGE_LIST);
    }
    
    public void init(HttpServletRequest request, HttpServletResponse response, SWBParamRequest paramRequest) throws SWBResourceException, java.io.IOException {
        page(getPage(request), request);
    }

    public void doPages(HttpServletRequest request, HttpServletResponse response, SWBParamRequest paramRequest) throws java.io.IOException {
        String url = "/swbadmin/jsp/rnc/pager.jsp";
        RequestDispatcher rd = request.getRequestDispatcher(url);
        try {
            rd.include(request, response);
        }catch (ServletException se) {
            LOG.info(se.getMessage());
        }
    }
    
    public void doPage(HttpServletRequest request, HttpServletResponse response, SWBParamRequest paramRequest) throws SWBResourceException, java.io.IOException {
        page(getPage(request), request);
        String url = "/swbadmin/jsp/rnc/rows.jsp";
        if (null != request.getParameter("m") && "l".equalsIgnoreCase(request.getParameter("m")))
            request.setAttribute("mode", "row lista");
        else request.setAttribute("mode", "card-columns");
        request.setAttribute("m",request.getParameter("m"));
        RequestDispatcher rd = request.getRequestDispatcher(url);
        try {
            request.setAttribute("paramRequest", paramRequest);
            rd.include(request, response);
        }catch (ServletException se) {
            LOG.info(se.getMessage());
        }
    }

    public void doSort(HttpServletRequest request, HttpServletResponse response, SWBParamRequest paramRequest) throws SWBResourceException, java.io.IOException {
        String url = "/swbadmin/jsp/rnc/rows.jsp";
        request.setAttribute("mode", "card-columns");
        RequestDispatcher rd = request.getRequestDispatcher(url);
        try {
            rd.include(request, response);
        }catch (ServletException se) {
            LOG.info(se.getMessage());
        }
    }

    public void doNext(HttpServletRequest request, HttpServletResponse response, SWBParamRequest paramRequest) throws SWBResourceException, java.io.IOException {
        int pagenum = 0;
        Integer totalPages = (Integer)request.getAttribute(TOTAL_PAGES);
        Integer pagenumParam = (Integer)request.getAttribute(NUM_PAGE_LIST);
        if (pagenumParam!=null) pagenum = pagenumParam;
        if (pagenum <= 0) pagenum = 1;
        pagenum++;
        if (pagenum>totalPages) pagenum = totalPages;
        request.setAttribute(NUM_PAGE_LIST, pagenum);
        page(pagenum, request);

    }

    public void doPrev(HttpServletRequest request, HttpServletResponse response, SWBParamRequest paramRequest) throws SWBResourceException, java.io.IOException {
        int pagenum = 0;
        Integer pagenumParam = (Integer)request.getAttribute(NUM_PAGE_LIST);
        if (pagenumParam!=null) pagenum = pagenumParam;
        pagenum--;
        if (pagenum<=0) pagenum = 1;
        request.setAttribute(NUM_PAGE_LIST, pagenum);
        page(pagenum, request);
    }
    
    public int getPage(HttpServletRequest request) throws SWBResourceException, java.io.IOException {
        int pagenum = 0;
        String p = request.getParameter("p");
        if (null != p)
            pagenum = Integer.parseInt(p);
        if (pagenum<=0) pagenum = 1;
        request.setAttribute(NUM_PAGE_LIST, pagenum);
        request.setAttribute("PAGE_NUM_ROW", PAGE_NUM_ROW);
        return pagenum;
    }

    protected void page(int pagenum, HttpServletRequest request) {
        ArrayList<?> rows = (ArrayList<?>)request.getAttribute(FULL_LIST);
        Integer total = (Integer)request.getAttribute(NUM_RECORDS_TOTAL);
        if (null==total) total = 0;
        if (null==rows) rows = new ArrayList();
        try {
            Integer totalPages = total/PAGE_NUM_ROW;
            if (total%PAGE_NUM_ROW != 0)
                totalPages ++;
            request.setAttribute(TOTAL_PAGES, totalPages);
            Integer currentLeap = (pagenum-1)/PAGE_JUMP_SIZE;
            request.setAttribute(NUM_PAGE_JUMP, currentLeap);
            request.setAttribute("PAGE_JUMP_SIZE", PAGE_JUMP_SIZE);
            request.setAttribute(PAGE_LIST, rows);
            request.setAttribute(NUM_RECORDS_VISIBLE, rows.size());
        }catch(Exception e) {
            LOG.info(e.getMessage());
        }
    }
    
    /**protected void page(int pagenum, HttpSession session) {
        ArrayList<?> rows = (ArrayList<?>)session.getAttribute(FULL_LIST);
        Integer total = (Integer)session.getAttribute(NUM_RECORDS_TOTAL);
        if (null==total) total = 0;
        if (null==rows) rows = new ArrayList();
        try {
            Integer totalPages = total/PAGE_NUM_ROW;
            if (total%PAGE_NUM_ROW != 0)
                totalPages ++;
            session.setAttribute(TOTAL_PAGES, totalPages);
            Integer currentLeap = (pagenum-1)/PAGE_JUMP_SIZE;
            session.setAttribute(NUM_PAGE_JUMP, currentLeap);
            session.setAttribute("PAGE_JUMP_SIZE", PAGE_JUMP_SIZE);
            session.setAttribute(PAGE_LIST, rows);
            session.setAttribute(NUM_RECORDS_VISIBLE, rows.size());
        }catch(Exception e) {
            LOG.info(e.getMessage());
        }
    }**/

    /**private ArrayList getRows(int page, List<?> rows) {
        int pageCount = 1;
        if (rows==null || rows.isEmpty()) return new ArrayList();
        Map<Integer, ArrayList<?>> pagesRows = new HashMap<>();
        ArrayList pageRows = new ArrayList();
        pagesRows.put(pageCount, pageRows);
        for (int i=0; i<rows.size(); i++) {
            pageRows.add(rows.get(i));
            if (i+1 < rows.size() && ((i+1) % PAGE_NUM_ROW) == 0) {
                pageCount++;
                pageRows = new ArrayList();
                pagesRows.put(pageCount, pageRows);
            }
        }
        ArrayList rowsPage = pagesRows.get(page);
        if (rowsPage==null) rowsPage = new ArrayList();
        return rowsPage;
    }**/
}
