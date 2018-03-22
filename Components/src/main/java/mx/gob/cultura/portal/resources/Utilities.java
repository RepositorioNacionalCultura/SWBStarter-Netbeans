package mx.gob.cultura.portal.resources;

import javax.servlet.http.HttpServletRequest;

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
}
