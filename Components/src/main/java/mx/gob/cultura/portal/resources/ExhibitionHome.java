/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.gob.cultura.portal.resources;

import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.Iterator;
import java.io.IOException;
import java.util.ArrayList;

import org.w3c.dom.Element;
import org.w3c.dom.Document;
import org.w3c.dom.DOMException;

import org.semanticwb.Logger;
import org.semanticwb.SWBUtils;
import org.semanticwb.model.WebPage;

import javax.servlet.ServletException;
import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.semanticwb.SWBPlatform;
import mx.gob.cultura.portal.utils.Utils;
import org.semanticwb.portal.api.SWBParamRequest;
import org.semanticwb.portal.api.GenericAdmResource;
import org.semanticwb.portal.api.SWBResourceException;

import static mx.gob.cultura.portal.utils.Constants.FULL_LIST;
import static mx.gob.cultura.portal.utils.Constants.MODE_PAGE;
import static mx.gob.cultura.portal.utils.Constants.NUM_PAGE_JUMP;
import static mx.gob.cultura.portal.utils.Constants.PAGE_NUM_ROW;
import static mx.gob.cultura.portal.utils.Constants.PARAM_REQUEST;

import static mx.gob.cultura.portal.utils.Constants.NUM_ROW;
import static mx.gob.cultura.portal.utils.Constants.NUM_PAGE_LIST;
import static mx.gob.cultura.portal.utils.Constants.NUM_RECORDS_TOTAL;
import static mx.gob.cultura.portal.utils.Constants.NUM_RECORDS_VISIBLE;

import static mx.gob.cultura.portal.utils.Constants.PAGE_LIST;
import static mx.gob.cultura.portal.utils.Constants.PAGE_JUMP_SIZE;
import static mx.gob.cultura.portal.utils.Constants.STR_JUMP_SIZE;
import static mx.gob.cultura.portal.utils.Constants.TOTAL_PAGES;

/**
 *
 * @author sergio.tellez
 */
public class ExhibitionHome extends GenericAdmResource {

    private final String webpath = SWBPlatform.getContextPath();
    private final String path = this.webpath + "/swbadmin/xsl/TematicIndexXSL/";
    private static final Logger LOGGER = SWBUtils.getLogger(ExhibitionHome.class);
    
    @Override
    public void processRequest(HttpServletRequest request, HttpServletResponse response, SWBParamRequest paramRequest) throws SWBResourceException, IOException {
        response.setContentType("text/html; charset=UTF-8");
        String mode = paramRequest.getMode();
        if (MODE_PAGE.equals(mode)) {
            doPage(request, response, paramRequest);
        }else
            super.processRequest(request, response, paramRequest);
    }
    
    @Override
    public void doView(HttpServletRequest request, HttpServletResponse response, SWBParamRequest paramRequest) throws SWBResourceException, IOException {
        response.setContentType("text/html; charset=UTF-8");
        String jsppath = "/swbadmin/jsp/rnc/exhibitions/tematic/init.jsp";
        RequestDispatcher rd = request.getRequestDispatcher(jsppath);
        try {
            request.setAttribute(PARAM_REQUEST, paramRequest);
            List<org.bson.Document> exhibitionList = getExhibitions(paramRequest);
            request.setAttribute("exhibitions", exhibitionList);
            request.setAttribute(FULL_LIST, exhibitionList);
            request.setAttribute(NUM_RECORDS_TOTAL, exhibitionList.size());
            init(request);
            rd.include(request, response);
        } catch (ServletException se) {
            LOGGER.info(se.getMessage());
        }
    }
    
    public void doPage(HttpServletRequest request, HttpServletResponse response, SWBParamRequest paramRequest) throws SWBResourceException, java.io.IOException {
        int pagenum = 0;
        String p = request.getParameter("p");
        if (null != p)
            pagenum = Integer.parseInt(p);
        if (pagenum<=0) pagenum = 1;
        request.setAttribute(NUM_PAGE_LIST, pagenum);
        request.setAttribute(PAGE_NUM_ROW, NUM_ROW);
        page(pagenum, request);
        String url = "/swbadmin/jsp/rnc/exhibitions/tematic/rows.jsp";
        RequestDispatcher rd = request.getRequestDispatcher(url);
        try {
            request.setAttribute(PARAM_REQUEST, paramRequest);
            rd.include(request, response);
        }catch (ServletException se) {
            LOGGER.info(se.getMessage());
        }
    }
    
    private List<org.bson.Document> getExhibitions(SWBParamRequest paramRequest) throws SWBResourceException, IOException {
        WebPage pageBase = paramRequest.getWebPage();
        List<String> elements = new ArrayList<>();
        List<org.bson.Document> exhibitionList = new ArrayList<>();
        if (null != getResourceBase().getAttribute("pageBase"))
            pageBase = paramRequest.getWebPage().getWebSite().getWebPage(getResourceBase().getAttribute("pageBase"));
        String usrlanguage = paramRequest.getUser().getLanguage() == null ? "es" : paramRequest.getUser().getLanguage();
        Iterator<WebPage> exhibitions = pageBase.listChilds(usrlanguage, Boolean.TRUE, Boolean.FALSE, Boolean.FALSE, null);
        while (exhibitions.hasNext()) {
            boolean add = false;
            WebPage exhibition = (WebPage) exhibitions.next();
            org.bson.Document bson = new org.bson.Document("url", exhibition.getUrl(usrlanguage, false)).append("path", this.path).append("id", exhibition.getId())
                .append("target", null != exhibition.getTarget() && !"".equalsIgnoreCase(exhibition.getTarget()) ? exhibition.getTarget() : "_self")
                .append("desc", exhibition.getDisplayDescription(usrlanguage) == null ? "" : exhibition.getDisplayDescription(usrlanguage))
                .append("title", exhibition.getDisplayName(usrlanguage)).append("author", exhibition.getCreator().getFullName());
            if (null != exhibition.getProperty("posters") && !exhibition.getProperty("posters").isEmpty()) {
                if (exhibition.getProperty("posters").indexOf("#") >0) {
                    String [] posters = exhibition.getProperty("posters").split("#");
                    for (int i=0; i<posters.length; i++) {
                        elements.add(posters[i]);
                    }
                }else elements.add(exhibition.getProperty("posters"));
                bson.append("posters", elements);
            }
            if (null != paramRequest.getUser() && paramRequest.getUser().isSigned()) {
                add = paramRequest.getUser().getId().equalsIgnoreCase(exhibition.getCreator().getId());
            }else add = exhibition.isValid() && paramRequest.getUser().haveAccess(exhibition);
            if (add)
                exhibitionList.add(bson);
        }
        return exhibitionList;
    }

    public Document getDom(HttpServletRequest request, HttpServletResponse response, SWBParamRequest paramRequest) throws SWBResourceException, IOException {
        int ison = 0;
        int igrandson = 0;
        try {
            String usrlanguage = paramRequest.getUser().getLanguage() == null ? "es" : paramRequest.getUser().getLanguage();
            Document dom = SWBUtils.XML.getNewDocument();
            Element out = dom.createElement("resource");
            dom.appendChild(out);
            Element father = dom.createElement("father");
            out.appendChild(father);
            WebPage pageBase = paramRequest.getWebPage();
            if (null != getResourceBase().getAttribute("pageBase"))
                pageBase = paramRequest.getWebPage().getWebSite().getWebPage(getResourceBase().getAttribute("pageBase"));
            Element fathertitle = dom.createElement("fathertitle");
            fathertitle.appendChild(dom.createTextNode(pageBase.getDisplayName(usrlanguage)));
            father.setAttribute("path", this.path);
            father.setAttribute("id", pageBase.getId());
            father.setAttribute("fatherref", pageBase.getUrl(usrlanguage, false));
            father.appendChild(fathertitle);
            if (paramRequest.getWebPage().equals(pageBase))
                father.setAttribute("current", "1");
            else father.setAttribute("current", "0");
            father.setAttribute("desc", pageBase.getDisplayDescription(usrlanguage) == null ? "" : pageBase.getDisplayDescription(usrlanguage));
            String descr = pageBase.getDisplayDescription(usrlanguage);
            if (null != descr) {
                father.setAttribute("hasfatherdescription", "1");
                Element fatherdescription = dom.createElement("fatherdescription");
                fatherdescription.appendChild(dom.createTextNode(descr));
                father.appendChild(fatherdescription);
            }
            Iterator<WebPage> hijos = pageBase.listChilds(usrlanguage, Boolean.TRUE, Boolean.FALSE, Boolean.FALSE, null);
            ison = 0;
            while (hijos.hasNext()) {
                WebPage hijo = (WebPage) hijos.next();
                if ((hijo.isValid()) && (paramRequest.getUser().haveAccess(hijo))) {
                    ison++;
                    Element son = dom.createElement("son");
                    son.setAttribute("sonref", hijo.getUrl(usrlanguage, false));
                    son.setAttribute("path", this.path);
                    son.setAttribute("id", hijo.getId());
                    if ((null != hijo.getTarget()) && (!"".equalsIgnoreCase(hijo.getTarget())))
                        son.setAttribute("target", hijo.getTarget());
                    if (paramRequest.getWebPage().equals(hijo))
                        son.setAttribute("current", "1");
                    else son.setAttribute("current", "0");
                    son.setAttribute("desc", hijo.getDisplayDescription(usrlanguage) == null ? "" : hijo.getDisplayDescription(usrlanguage));
                    Element sontitle = dom.createElement("sontitle");
                    sontitle.appendChild(dom.createTextNode(hijo.getDisplayName(usrlanguage)));
                    son.appendChild(sontitle);
                    father.appendChild(son);
                    descr = hijo.getDisplayDescription(usrlanguage);
                    if (null != descr) {
                        son.setAttribute("hassondescription", "1");
                        Element sondescription = dom.createElement("sondescription");
                        sondescription.appendChild(dom.createTextNode(descr));
                        son.appendChild(sondescription);
                    }
                    if (null != hijo.getProperty("posters") && !hijo.getProperty("posters").isEmpty()) {
                        int index = Utils.toInt(hijo.getProperty("posters"));
                        if (index > 0) {
                            if (index < 3) {
                                son.setAttribute("posters", "1");
                                son.setAttribute("poster1", hijo.getProperty("poster1"));
                                son.setAttribute("poster2", hijo.getProperty("poster2"));
                            }else {
                                son.setAttribute("posters", "3");
                                son.setAttribute("poster1", hijo.getProperty("poster1"));
                                son.setAttribute("poster2", hijo.getProperty("poster2"));
                                son.setAttribute("poster3", hijo.getProperty("poster3"));
                            }
                        }
                    }else
                        son.setAttribute("posters", "0");
                    Iterator<WebPage> nietos = hijo.listChilds(usrlanguage, Boolean.TRUE, Boolean.FALSE, Boolean.FALSE, null);
                    igrandson = 0;
                    while (nietos.hasNext()) {
                        WebPage nieto = (WebPage) nietos.next();
                        if ((nieto.isValid()) && (paramRequest.getUser().haveAccess(nieto))) {
                            igrandson++;
                            Element grandson = dom.createElement("grandson");
                            grandson.setAttribute("grandsonref", nieto.getUrl(usrlanguage, false));
                            grandson.setAttribute("path", this.path);
                            grandson.setAttribute("id", nieto.getId());
                            if ((nieto.getTarget() != null) && (!"".equalsIgnoreCase(nieto.getTarget()))) {
                                grandson.setAttribute("target", nieto.getTarget());
                            }
                            if (paramRequest.getWebPage().equals(nieto)) {
                                grandson.setAttribute("current", "1");
                            } else {
                                grandson.setAttribute("current", "0");
                            }
                            son.setAttribute("desc", nieto.getDisplayDescription(usrlanguage) == null ? "" : nieto
                                    .getDisplayDescription(usrlanguage));
                            Element grandsontitle = dom.createElement("grandsontitle");
                            grandsontitle.appendChild(dom.createTextNode(nieto.getDisplayName(usrlanguage)));
                            grandson.appendChild(grandsontitle);

                            descr = nieto.getDisplayDescription(usrlanguage);
                            if (descr != null) {
                                grandson.setAttribute("hasgrandsondescription", "1");
                                Element grandsondescription = dom.createElement("grandsondescription");
                                grandsondescription.appendChild(dom.createTextNode(descr));
                                grandson.appendChild(grandsondescription);
                            }
                            son.appendChild(grandson);
                        }
                    }
                    son.setAttribute("totalgrandson", Integer.toString(igrandson));
                }
            }
            father.setAttribute("totalson", Integer.toString(ison));
            return dom;
        } catch (DOMException e) {
            LOGGER.error("Error while generating the comments form in resource " + getResourceBase().getResourceType().getResourceClassName() 
                    + " with identifier " + getResourceBase().getId() + " - " + getResourceBase().getTitle(), e);
        }
        return null;
    }
    
    private void init(HttpServletRequest request) throws SWBResourceException, java.io.IOException {
        int pagenum = 0;
        String p = request.getParameter("p");
        if (null != p) pagenum = Integer.parseInt(p);
        if (pagenum<=0) pagenum = 1;
        request.setAttribute(NUM_PAGE_LIST, pagenum);
        request.setAttribute(PAGE_NUM_ROW, NUM_ROW);
        page(pagenum, request);
    }
    
    private void page(int pagenum, HttpServletRequest request) {
        List<?> rows = (List<?>)request.getAttribute(FULL_LIST);
        Integer total = (Integer)request.getAttribute(NUM_RECORDS_TOTAL);
        if (null==total) total = 0;
        if (null==rows) rows = new ArrayList();
        try {
            Integer totalPages = total/NUM_ROW;
            if (total%NUM_ROW != 0)
                totalPages ++;
            request.setAttribute(TOTAL_PAGES, totalPages);
            Integer currentLeap = (pagenum-1)/PAGE_JUMP_SIZE;
            request.setAttribute(NUM_PAGE_JUMP, currentLeap);
            request.setAttribute(STR_JUMP_SIZE, PAGE_JUMP_SIZE);
            ArrayList rowsPage = getRows(pagenum, rows);
            request.setAttribute(PAGE_LIST, rowsPage);
            request.setAttribute(NUM_RECORDS_TOTAL, rows.size());
            request.setAttribute(NUM_RECORDS_VISIBLE, rowsPage.size());
        }catch(Exception e) {
            LOGGER.info(e.getMessage());
        }
    }
    
    private ArrayList getRows(int page, List<?> rows) {
        int pageCount = 1;
        if (rows==null || rows.isEmpty()) return new ArrayList();
        Map<Integer, ArrayList<?>> pagesRows = new HashMap<>();
        ArrayList pageRows = new ArrayList();
        pagesRows.put(pageCount, pageRows);
        for (int i=0; i<rows.size(); i++) {
            pageRows.add(rows.get(i));
            if (i+1 < rows.size() && ((i+1) % NUM_ROW) == 0) {
                pageCount++;
                pageRows = new ArrayList();
                pagesRows.put(pageCount, pageRows);
            }
        }
        ArrayList rowsPage = pagesRows.get(page);
        if (rowsPage==null) rowsPage = new ArrayList();
        return rowsPage;
    }
}
