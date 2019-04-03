package mx.gob.repository.portal.resources;

import org.semanticwb.datamanager.DataObject;
import org.semanticwb.model.User;
import org.semanticwb.portal.api.SWBActionResponse;
import org.semanticwb.portal.api.SWBParamRequest;
import org.semanticwb.portal.api.SWBResourceException;
import org.semanticwb.portal.resources.JSPResource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * JSP Resource for SWBForms integration on SemanticWebBuilder Portal.
 * @author Hasdai Pacheco
 */
public class SWBFormsJSPResource extends JSPResource {

    @Override
    public void doView(HttpServletRequest request, HttpServletResponse response, SWBParamRequest paramRequest) throws SWBResourceException, IOException {
        User user = paramRequest.getUser();
        setUser(request, user);
        super.doView(request, response, paramRequest);
    }

    @Override
    public void processAction(HttpServletRequest request, SWBActionResponse actionResponse) throws SWBResourceException, IOException {
        User user = actionResponse.getUser();
        setUser(request, user);
        super.processAction(request, actionResponse);
    }

    /**
     * Syncs SemanticWebBuilder Portal User with SWBForms user before dispatching JSP.
     * @param request {@link HttpServletRequest} object.
     * @param swbuser {@link User} object.
     */
    private void setUser(HttpServletRequest request, User swbuser) {
        if (null != swbuser && swbuser.isSigned()) {
            DataObject r = new DataObject();
            r.put("email", swbuser.getEmail());

            request.getSession().setAttribute("_USER_", r);
        } else {
            request.getSession().removeAttribute("_USER_");
        }
    }
}
