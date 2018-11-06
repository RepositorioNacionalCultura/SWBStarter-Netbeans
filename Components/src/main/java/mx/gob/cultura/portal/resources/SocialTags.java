package mx.gob.cultura.portal.resources;

import mx.gob.cultura.portal.request.GetBICRequest;
import mx.gob.cultura.portal.response.Entry;
import org.semanticwb.portal.api.GenericAdmResource;
import org.semanticwb.portal.api.SWBParamRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import mx.gob.cultura.portal.persist.CollectionMgr;
import static mx.gob.cultura.portal.utils.Constants.COLLECTION;
import mx.gob.cultura.portal.response.Collection;

/**
 * Strategy component that writes meta tags for social sharing of BIC.
 *
 * @author Hasdai Pacheco
 */
public class SocialTags extends GenericAdmResource {

    CollectionMgr mgr = CollectionMgr.getInstance();

    @Override
    public void doView(HttpServletRequest request, HttpServletResponse response, SWBParamRequest paramRequest) {
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
//        System.out.println("SocialTags..."+request.getRequestURL());
        String oId = request.getParameter("id");
        if (paramRequest.getCallMethod() == SWBParamRequest.Call_STRATEGY && null != oId) {

            String uri = paramRequest.getWebPage().getWebSite().getModelProperty("search_endPoint")
                    + //getResourceBase().getAttribute("url","http://localhost:8080") +
                    "/api/v1/search?identifier=" + oId;
            String fbAppId = paramRequest.getWebPage().getWebSite().getModelProperty("facebook_appid");

            String reqHost = request.getRequestURL().toString();
            int indexColon = reqHost.indexOf(":", 7);
            String pathBegining = null;
            if (indexColon != -1) {
                pathBegining = reqHost.substring(0, reqHost.indexOf("/", indexColon));
            } else {
                pathBegining = reqHost.substring(0, reqHost.indexOf("/", 10));
            }

            GetBICRequest req = new GetBICRequest(uri);
            Entry entry = req.makeRequest();
            String title = null;
            String urlImage = null;
            String description = null;
            String default_description = paramRequest.getWebPage().getWebSite().getModelProperty("share_default_description");
            if (null == default_description) {
                default_description = "";
            }
            if (null != entry) {
                title = !entry.getRecordtitle().isEmpty() ? entry.getRecordtitle().get(0).getValue() : "Sin t&iacute;tulo";
                urlImage = entry.getResourcethumbnail() != null ? (entry.getResourcethumbnail().startsWith("/")
                        ? pathBegining + entry.getResourcethumbnail() : entry.getResourcethumbnail()) : "";
                description = !entry.getDescription().isEmpty() ? entry.getDescription().get(0) : default_description; //

                StringBuilder metas = new StringBuilder(128);
                metas.append("<meta charset=\"utf-8\" />\n");
                metas.append("");
                if (null != fbAppId) {
                    metas.append("<meta property=\"fb:app_id\" content=\"");
                    metas.append(fbAppId);
                    metas.append("\" />\n");
                }
                metas.append("<meta property=\"og:type\" content=\"website\" />\n");
                metas.append("<meta property=\"og:description\" content=\"");
                metas.append(description);
                metas.append("\" />\n");
                metas.append("<meta property=\"og:url\" content=\"")
                        .append(request.getRequestURL())
                        .append("?")
                        .append(request.getQueryString())
                        .append("\" />\n");

                metas.append("<meta property=\"og:title\" content=\"")
                        .append(title)
                        .append("\" />\n");

                metas.append("<meta property=\"og:image\" content=\"")
                        .append(urlImage)
                        .append("\" />\n");
                metas.append("<meta name=\"twitter:card\" content=\"summary\" />");
                metas.append("<meta name=\"twitter:image\" content=\"")
                        .append(urlImage)
                        .append("\" />\n");
                metas.append("<script async src=\"https://platform.twitter.com/widgets.js\" charset=\"utf-8\"></script>\n");

                try {
                    Writer out = response.getWriter();
                    out.write(metas.toString());
                } catch (IOException ioex) {
                    ioex.printStackTrace(System.err);
                }
            } 
            else {  // Revisar si es colección
                //System.out.println("Por coleccion....");
                uri = paramRequest.getWebPage().getWebSite().getModelProperty("search_endPoint")
                    + //getResourceBase().getAttribute("url","http://localhost:8080") +
                    "/api/v1/search?identifier=";
                Collection collection = mgr.findById(oId); 
                if (null != collection) {
                    title = collection.getTitle();
                    if (null == title) {
                        title = "Sin título";
                    }
                    urlImage = null;
                    description = collection.getDescription();
                    if (null == description || description.trim().length()==0) {
                        default_description = paramRequest.getWebPage().getWebSite().getModelProperty("share_default_description");
                        if (null == default_description) {
                            default_description = "Sin descripción";
                        }
                        description = default_description;
                    }
                    if (collection.getElements() != null && !collection.getElements().isEmpty()) {
                        //System.out.println("Size:"+collection.getElements().size());
                        req = new GetBICRequest(uri+(String)collection.getElements().get(0));
                        entry = req.makeRequest();
                        if (null != entry) {
                            if (entry.getDigitalObject() != null && !entry.getDigitalObject().isEmpty()) {
                                urlImage = entry.getResourcethumbnail();
                            }
                        }
                    }
                    StringBuilder metas = new StringBuilder(128);
                    metas.append("<meta charset=\"utf-8\" />\n");
                    metas.append("");
                    if (null != fbAppId) {
                        metas.append("<meta property=\"fb:app_id\" content=\"");
                        metas.append(fbAppId);
                        metas.append("\" />\n");
                    }
                    metas.append("<meta property=\"og:type\" content=\"website\" />\n");
                    metas.append("<meta property=\"og:description\" content=\"");
                    metas.append(description);
                    metas.append("\" />\n");
                    metas.append("<meta property=\"og:url\" content=\"")
                            .append(request.getRequestURL())
                            .append("?")
                            .append(request.getQueryString())
                            .append("\" />\n");

                    metas.append("<meta property=\"og:title\" content=\"")
                            .append(title)
                            .append("\" />\n");

                    metas.append("<meta property=\"og:image\" content=\"")
                            .append(urlImage)
                            .append("\" />\n");
                    metas.append("<meta name=\"twitter:card\" content=\"summary\" />");
                    metas.append("<meta name=\"twitter:image\" content=\"")
                            .append(urlImage)
                            .append("\" />\n");
                    metas.append("<script async src=\"https://platform.twitter.com/widgets.js\" charset=\"utf-8\"></script>\n");

                    try {
                        Writer out = response.getWriter();
                        out.write(metas.toString());
                    } catch (IOException ioex) {
                        ioex.printStackTrace(System.err);
                    }

                }
            }
        }
    }
}
