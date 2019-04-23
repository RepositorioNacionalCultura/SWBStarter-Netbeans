package mx.gob.cultura.portal.resources;


import java.io.IOException;
import java.util.logging.Logger;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.semanticwb.portal.api.GenericResource;
import org.semanticwb.portal.api.SWBParamRequest;


/**
 * Genera la interface para que el usuario inicie sesión, cuando no está autenticado
 * @author jose.jimenez
 */
public class HomeCollectionsSession extends GenericResource {
    
    private static final Logger LOG = Logger.getLogger(HomeCollectionsSession.class.getName());

    @Override
    public void doView(HttpServletRequest request, HttpServletResponse response, SWBParamRequest paramRequest) throws IOException {
        
        String url = "/swbadmin/jsp/rnc/homeCollectionsSession.jsp";
        RequestDispatcher rd = request.getRequestDispatcher(url);
        try {
            request.setAttribute("paramsRequest", (Object)paramRequest);
            rd.include((ServletRequest)request, (ServletResponse)response);
        }
        catch (ServletException se) {
            HomeCollectionsSession.LOG.info(se.getMessage());
        }
    }
}
