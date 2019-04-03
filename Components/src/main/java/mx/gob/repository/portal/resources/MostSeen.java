package mx.gob.repository.portal.resources;

import java.util.Map;

import mx.gob.cultura.portal.response.Entry;
import mx.gob.cultura.portal.request.ListBICRequest;

import org.semanticwb.portal.api.SWBParamRequest;
import org.semanticwb.portal.api.GenericAdmResource;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import mx.gob.cultura.portal.response.Title;
import org.semanticwb.model.WebSite;


/**
 * Muestra los n elementos con mayor número de consultas
 * @author jose.jimenez
 */
public class MostSeen extends GenericAdmResource {
    private static final Logger LOG = Logger.getLogger(MostSeen.class.getName());

    @Override
    public void doView(HttpServletRequest request, HttpServletResponse response, SWBParamRequest paramRequest) throws java.io.IOException {

        List<Entry> references;
        //String url = "/swbadmin/jsp/rnc/mostSeenCarousel.jsp";
        String url = "/work/models/"+paramRequest.getWebPage().getWebSite().getId()+"/jsp/rnc/resources/mostSeenCarousel.jsp";
        RequestDispatcher rd = request.getRequestDispatcher(url);
        try {
            references = getReferences(paramRequest.getWebPage().getWebSite());
            request.setAttribute("mostSeenList", references);
            request.setAttribute("paramRequest", paramRequest);
            rd.include(request, response);
        } catch (ServletException se) {
            se.printStackTrace(System.err);
            LOG.info(se.getMessage());
        }
        
        while (paramRequest.getWebPage().listFriendlyURLs().hasNext()) {
            
        }
    }

    /**
     * Obtiene los n elementos con mayor numero de visitas registradas en base de datos
     * @param webSite el sitio del cual se obtiene la ruta del endPoint a consultar
     * @return la lista de elementos mas vistos en el sitio indicado. Si el sitio no
     *         contiene una propiedad con el nombre {@literal search_endPoint}, la lista estara vacia.
     */
    private List<Entry> getReferences(WebSite webSite) {
        Integer numElements;
        List<Entry> publicationList = new ArrayList<>();
        String baseUri = webSite.getModelProperty("search_endPoint");
        String version = null != webSite.getModelProperty("version_endPoint") ? webSite.getModelProperty("version_endPoint") : "v1";
        try {
            numElements = Integer.parseInt(this.getResourceBase().getAttribute("numElements", "10"));
        } catch (NumberFormatException nfe) {
            numElements = 10;
        }
        if (null != baseUri) {
            ListBICRequest req = new ListBICRequest(baseUri + "/api/" + version + "/search?sort=-resourcestats.views&size=" + numElements);
            String json = req.doRequest();
            Map mapper = SearchCulturalProperty.getMapper(json);
            List<Object> objs = (ArrayList)mapper.get("records");
            if (null != objs) {
                for (Object record : objs) {
                    Map map = (Map)record;
                    Entry rec = ArtDetail.getMapEntry(map);
                    if (null != rec && null != rec.getId() && !rec.getId().isEmpty())
                        publicationList.add(rec);
                }
            }
        }
        return publicationList;
    }
}
