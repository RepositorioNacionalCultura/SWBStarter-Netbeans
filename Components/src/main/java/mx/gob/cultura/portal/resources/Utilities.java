package mx.gob.cultura.portal.resources;

import java.util.Iterator;
import javax.servlet.http.HttpServletRequest;
import org.semanticwb.model.Resource;
import org.semanticwb.model.WebSite;

/**
 * Contiene funciones que pueden ejecutarse desde los recursos para evitar el 
 * duplicado de codigo
 * @author jose.jimenez
 */
public class Utilities {
    
    
    /**
     * Genera un tag script para disponer de la funcionalidad para compartir contenido de un sitio Web.
     * Se requiere que el recurso que incluya esta funcion, se ejecute en una plantilla de SWB que
     * tambien haga uso de una instancia del recurso {@link mx.gob.cultura.portal.resources.SessionInitializer}
     * por la dependencia que existe en cuanto a las funciones del API de Facebook.
     * @param request la peticion {@link javax.servlet.http.HttpServletRequest} en atencion
     * @return un tag de HTML con el script necesario para 
     */
    public static String getScriptFBShare(HttpServletRequest request) {
        
        StringBuilder ret = new StringBuilder(128);
        ret.append("<script>\n");
        ret.append("  function fbShare() {\n");
        ret.append("    FB.ui({\n");
        ret.append("      method: 'share',\n");
        ret.append("      display: 'popup',\n");
        ret.append("      href: '");
        ret.append(request.getRequestURL());
        ret.append("?");
        ret.append(request.getQueryString());
        ret.append("',\n");
        ret.append("    }, function(response){});\n");
        ret.append("  }\n");
        ret.append("  </script>\n");
        
        return ret.toString();
    }
    
    /**
     * Sustituye en la URL recibida, el identificador del recurso a ejecutar
     * con el identificador de un recurso activo cuya clase sea la indicada por {@code objectClass}
     * @param website el sitio en que se buscar&aacute; una instancia activa del 
     *          recurso con clase {@code objectClass}
     * @param objectClass  la clase del recurso del cual se busca una instancia activa
     * @param urlExample la URL que se quiere ejecutar, en la que se indicar&aacute; 
     *          la instancia del tipo de recurso con clase {@code objectClass}
     * @return la URL para que se ejecute el el recurso que crea la sesion del usuario
     */
    public static String getResourceURL(WebSite website, Class objectClass, String urlExample) {
        
        //con urlExample como: "/es/cultura/portada2018/_aid/80/_mto/3/_act/openSession"
        String resourceId = null;
        final String delimiterAction = "_aid/";
        final String delimiterMode = "_rid/";
        StringBuilder actionPath = new StringBuilder(64);
        Iterator<Resource> resourceIt = website.listResources();
        boolean isActionUrl = true;
        while (resourceIt.hasNext()) {
            Resource resource = resourceIt.next(); 
            if (objectClass.getCanonicalName().equals(
                    resource.getResourceType().getResourceClassName()) && resource.isActive()) {
                resourceId = resource.getId();
                break;
            }
        }
        if (null != resourceId) {
            String[] pathParts;
            if (urlExample.contains(delimiterAction)) {
                pathParts = urlExample.split(delimiterAction);
            } else {
                pathParts = urlExample.split(delimiterMode);
                isActionUrl = false;
            }
            
            if (pathParts.length > 1) {
                int index = pathParts[1].indexOf("/");
                actionPath.append(pathParts[0]);
                actionPath.append(isActionUrl ? delimiterAction : delimiterMode);
                actionPath.append(resourceId);
                actionPath.append(pathParts[1].substring(index));
            }
        }
        return actionPath.toString();
    }
}
