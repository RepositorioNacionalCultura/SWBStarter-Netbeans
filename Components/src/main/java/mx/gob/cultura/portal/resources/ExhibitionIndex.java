/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.gob.cultura.portal.resources;

import java.util.Iterator;
import java.io.IOException;
import org.w3c.dom.Element;
import org.w3c.dom.Document;
import org.w3c.dom.DOMException;

import org.semanticwb.Logger;
import org.semanticwb.SWBUtils;
import org.semanticwb.model.WebPage;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import mx.gob.cultura.portal.utils.Utils;
import org.semanticwb.SWBPlatform;
import org.semanticwb.portal.api.SWBParamRequest;
import org.semanticwb.portal.api.SWBResourceException;
import org.semanticwb.portal.resources.TematicIndexXSL;

/**
 *
 * @author sergio.tellez
 */
public class ExhibitionIndex extends TematicIndexXSL {

    private final String webpath = SWBPlatform.getContextPath();
    private final String path = this.webpath + "/swbadmin/xsl/TematicIndexXSL/";
    private static final Logger LOGGER = SWBUtils.getLogger(ExhibitionIndex.class);

    @Override
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
}
