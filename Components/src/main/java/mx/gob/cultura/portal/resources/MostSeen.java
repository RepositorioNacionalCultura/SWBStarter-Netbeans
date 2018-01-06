package mx.gob.cultura.portal.resources;


import mx.gob.cultura.portal.request.ListBICRequest;
import mx.gob.cultura.portal.response.Document;
import mx.gob.cultura.portal.response.Entry;
import org.semanticwb.portal.api.GenericResource;
import org.semanticwb.portal.api.SWBParamRequest;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import org.semanticwb.SWBPlatform;


/**
 * Muestra los n elementos con mayor número de consultas
 * @author jose.jimenez
 */
public class MostSeen extends GenericResource {
    private static final Logger LOG = Logger.getLogger(MostSeen.class.getName());

    @Override
    public void doView(HttpServletRequest request, HttpServletResponse response,
                       SWBParamRequest paramRequest) throws java.io.IOException {

        List<Entry> references;
        String url = "/swbadmin/jsp/rnc/mostSeenCarousel.jsp";
        RequestDispatcher rd = request.getRequestDispatcher(url);
        try {
            references = getReferences();
            request.setAttribute("mostSeenList", references);
            request.setAttribute("paramRequest", paramRequest);
            rd.include(request, response);
        } catch (ServletException se) {
            se.printStackTrace();
            LOG.info(se.getMessage());
        }
    }

    private List<Entry> getReferences() {
        String uri = SWBPlatform.getEnv("rnc/endpointURL",getResourceBase().getAttribute("endpointURL","https://search.innovatic.com.mx")) + "/api/v1/search?sort=-resourcestats.views&size=10";
        List<Entry> publicationList = new ArrayList<>();
        ListBICRequest req = new ListBICRequest(uri);
        Document resp = req.makeRequest();
        if (null != resp) {
            publicationList = resp.getRecords();
        }
        return publicationList;
    }
}
