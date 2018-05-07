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

/**
 * Strategy component that writes meta tags for social sharing of BIC.
 * @author Hasdai Pacheco
 */
public class SocialTags extends GenericAdmResource {

    @Override
    public void doView(HttpServletRequest request, HttpServletResponse response, SWBParamRequest paramRequest) {
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        String oId = request.getParameter("id");
        if (paramRequest.getCallMethod() == SWBParamRequest.Call_STRATEGY && null != oId) {

            String uri = paramRequest.getWebPage().getWebSite().getModelProperty("search_endPoint") + //getResourceBase().getAttribute("url","http://localhost:8080") +
                "/api/v1/search?identifier=" + oId;
            String fbAppId = paramRequest.getWebPage().getWebSite().getModelProperty("facebook_appid");

            GetBICRequest req = new GetBICRequest(uri);
            Entry entry = req.makeRequest();

            if (null != entry) {
                StringBuilder metas = new StringBuilder();
                metas.append("<meta charset=\"utf-8\" />\n");
                metas.append("");
                if (null != fbAppId) {
                    metas.append("<meta property=\"fb:app_id\" content=\"");
                    metas.append(fbAppId);
                    metas.append("\" />\n");
                }
                metas.append("<meta property=\"og:type\" content=\"website\" />\n");
//                metas.append("<meta property=\"og:description\" content=\"Visita el Repositorio Digital del Patrimonio Cultural de México. Este es un ejemplo de lo que puedes encontrar.\" />\n");
                metas.append("<meta property=\"og:url\" content=\"")
                        .append(request.getRequestURL())
                        .append("?")
                        .append(request.getQueryString())
                        .append("\" />\n");

                metas.append("<meta property=\"og:title\" content=\"")
                    .append(entry.getRecordtitle().get(0).getValue())
                    .append("\" />\n");

                metas.append("<meta property=\"og:image\" content=\"")
                    .append(entry.getDigitalobject().get(0).getUrl())
                    .append("\" />\n");

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
