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

        if (paramRequest.getCallMethod() == SWBParamRequest.Call_STRATEGY) {
            String oId = request.getParameter("id");

            String uri = getResourceBase().getAttribute("url","http://localhost:8080") +
                "/api/v1/search?identifier=" + oId;

            GetBICRequest req = new GetBICRequest(uri);
            Entry entry = req.makeRequest();

            if (null != entry) {
                StringBuilder metas = new StringBuilder();
                metas.append("<meta property=\"og:url\" content=\"")
                    .append(paramRequest.getResourceBase().getAttribute("fburl", ""))
                    .append("\"/>");

                metas.append("<meta property=\"og:title\" content=\"")
                    .append(entry.getRecordtitle().get(0).getValue())
                    .append("\"/>");

                metas.append("<meta property=\"og:image\" content=\"")
                    .append(entry.getDigitalobject().get(0).getUrl())
                    .append("\"/>");

                String appId = paramRequest.getResourceBase().getAttribute("appId", null);
                if (null != appId) {
                    metas.append("<meta property=\"fb:app_id\" content=\"")
                        .append(appId)
                        .append("\"/>");
                }

                try {
                    Writer out = response.getWriter();
                    out.write(metas.toString());
                } catch (IOException ioex) {
                    ioex.printStackTrace();
                }
            }
        } else {
            return;
        }
    }
}
